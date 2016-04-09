package org.chodavarapu.datamill.http.impl;

import com.google.common.collect.Multimap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import org.chodavarapu.datamill.http.ServerRequest;
import rx.Observable;
import rx.subjects.PublishSubject;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ClientToServerChannelHandler extends ChannelInboundHandlerAdapter {
    private final BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor;
    private final Route route;
    private final ExecutorService threadPool;

    private PublishSubject<byte[]> entityStream;
    private ServerRequestImpl serverRequest;

    public ClientToServerChannelHandler(
            ExecutorService threadPool,
            Route route,
            BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor) {
        this.threadPool = threadPool;
        this.route = route;
        this.errorResponseConstructor = errorResponseConstructor;
    }

    private void sendGeneralServerError(ChannelHandlerContext context) {
        context.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        context.read();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;

            if (HttpUtil.is100ContinueExpected(request)) {
                sendContinueResponse(context);
            }

            entityStream = PublishSubject.create();
            serverRequest = ServerRequestBuilder.buildServerRequest(request, entityStream);

            processRequest(context, request);

            if (request.decoderResult().isFailure()) {
                entityStream.onError(request.decoderResult().cause());
            }
        }

        if (message instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) message;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                entityStream.onNext(content.array());

                if (httpContent.decoderResult().isFailure()) {
                    entityStream.onError(httpContent.decoderResult().cause());
                }
            }

            if (message instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) message;
                if (!trailer.trailingHeaders().isEmpty()) {
                    serverRequest.setTrailingHeaders(ServerRequestBuilder.buildHeadersMap(trailer.trailingHeaders()));
                }

                entityStream.onCompleted();
            }
        }

        context.read();
    }

    private void processRequest(ChannelHandlerContext context, HttpRequest originalRequest) {
        threadPool.execute(() -> {
            try {
                Observable<Response> responseObservable = route.apply(serverRequest);
                if (responseObservable != null) {
                    threadPool.execute(() -> {
                        Response response = responseObservable.onErrorResumeNext(throwable -> {
                            Observable<Response> errorResponse = errorResponseConstructor.apply(serverRequest, throwable);
                            if (errorResponse != null) {
                                return errorResponse.onErrorResumeNext(Observable.just(null));
                            }

                            return Observable.just(null);
                        }).toBlocking().lastOrDefault(null);

                        sendResponse(context, originalRequest, response);
                    });
                } else {
                    sendGeneralServerError(context);
                }
            } catch (Exception e) {
                sendGeneralServerError(context);
            }
        });
    }

    private void fillResponse(HttpRequest originalRequest, HttpResponse response,
                              Multimap<String, String> headers, int contentLength) {
        boolean keepAlive = HttpUtil.isKeepAlive(originalRequest);
        if (keepAlive) {
            // http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        if (contentLength > -1) {
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, contentLength);
        }

        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> header : headers.entries()) {
                response.headers().add(header.getKey(), header.getValue());
            }
        }

    }

    private void sendResponseStart(ChannelHandlerContext context, HttpRequest originalRequest,
                                   int status, Multimap<String, String> headers, int contentLength) {
        HttpResponse response = new DefaultHttpResponse(
                originalRequest.protocolVersion(),
                HttpResponseStatus.valueOf(status));

        fillResponse(originalRequest, response, headers, contentLength);

        context.write(response);
    }

    private void sendContent(ChannelHandlerContext context, byte[] responseBytes) {
        HttpContent content = new DefaultHttpContent(responseBytes == null ?
                Unpooled.EMPTY_BUFFER :
                Unpooled.wrappedBuffer(responseBytes));

        context.write(content);
    }

    private void sendResponseEnd(ChannelHandlerContext context, HttpRequest originalRequest) {
        writeAndFlush(context, originalRequest, LastHttpContent.EMPTY_LAST_CONTENT);
    }

    private void sendFullResponse(ChannelHandlerContext context, HttpRequest originalRequest,
                                  int status, Multimap<String, String> headers) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                originalRequest.protocolVersion(),
                HttpResponseStatus.valueOf(status),
                Unpooled.EMPTY_BUFFER);

        fillResponse(originalRequest, response, headers, 0);

        writeAndFlush(context, originalRequest, response);
    }

    private void sendResponse(ChannelHandlerContext context, HttpRequest originalRequest, Response serverResponse) {
        Entity responseEntity = serverResponse.entity();
        if (responseEntity != null) {
            threadPool.execute(() -> {
                boolean[] first = {true};
                responseEntity.asChunks()
                        .doOnNext(bytes -> {
                            if (first[0]) {
                                sendResponseStart(context, originalRequest,
                                        serverResponse.status().getCode(),
                                        serverResponse.headers(),
                                        bytes == null ? -1 : bytes.length);
                                sendContent(context, bytes);

                                first[0] = false;
                            } else {
                                sendContent(context, bytes);
                            }
                        })
                        .finallyDo(() -> {
                            sendResponseEnd(context, originalRequest);
                        })
                        .toBlocking().lastOrDefault(null);
            });
        } else {
            sendFullResponse(context, originalRequest, serverResponse.status().getCode(), serverResponse.headers());
        }
    }

    private static void sendContinueResponse(ChannelHandlerContext context) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        context.write(response);
    }

    private void writeAndFlush(ChannelHandlerContext context, HttpRequest originalRequest, HttpObject response) {
        ChannelFuture writeFuture = context.writeAndFlush(response);
        boolean keepAlive = HttpUtil.isKeepAlive(originalRequest);
        if (!keepAlive) {
            writeFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
