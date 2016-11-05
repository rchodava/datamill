package foundation.stack.datamill.http.impl;

import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.subjects.ReplaySubject;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ClientToServerChannelHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ClientToServerChannelHandler.class);

    private final BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor;
    private final Route route;
    private final ExecutorService threadPool;
    private volatile boolean channelClosed;
    private volatile Subscription entitySubscription;

    private ReplaySubject<ByteBuffer> bodyStream;
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
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        channelClosed = true;
        if (entitySubscription != null) {
            if (!entitySubscription.isUnsubscribed()) {
                entitySubscription.unsubscribe();
            }

            entitySubscription = null;
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) {
        if (message instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) message;

            if (HttpUtil.is100ContinueExpected(request)) {
                sendContinueResponse(context);
            }

            bodyStream = ReplaySubject.create();
            serverRequest = ServerRequestBuilder.buildServerRequest(request, bodyStream);

            processRequest(context, request);

            if (request.decoderResult().isFailure()) {
                bodyStream.onError(request.decoderResult().cause());
            }
        }

        if (message instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) message;

            ByteBuf content = httpContent.content();
            if (content.isReadable()) {
                bodyStream.onNext(content.nioBuffer());

                if (httpContent.decoderResult().isFailure()) {
                    bodyStream.onError(httpContent.decoderResult().cause());
                }
            }

            if (message instanceof LastHttpContent) {
                LastHttpContent trailer = (LastHttpContent) message;
                if (!trailer.trailingHeaders().isEmpty()) {
                    serverRequest.setTrailingHeaders(ServerRequestBuilder.buildHeadersMap(trailer.trailingHeaders()));
                }

                bodyStream.onCompleted();
            }
        }
    }

    private void processRequest(ChannelHandlerContext context, HttpRequest originalRequest) {
        threadPool.execute(() -> {
            try {
                Observable<Response> responseObservable = route.apply(serverRequest);
                if (responseObservable != null) {
                    threadPool.execute(() -> {
                        Response response = responseObservable.onErrorResumeNext(throwable -> {
                            if (errorResponseConstructor != null) {
                                Observable<Response> errorResponse =
                                        errorResponseConstructor.apply(serverRequest, throwable);
                                if (errorResponse != null) {
                                    logger.debug("Error occurred handling request, invoking application error handler");
                                    return errorResponse.onErrorResumeNext(Observable.just(null));
                                }
                            } else {
                                logger.debug("Error occurred handling request - no application error handler was available to handle it - {}", throwable);
                            }

                            return Observable.just(new ResponseImpl(Status.INTERNAL_SERVER_ERROR));
                        }).toBlocking().lastOrDefault(new ResponseImpl(Status.NOT_FOUND));

                        sendResponse(context, originalRequest, response);
                    });
                } else {
                    logger.debug("Error occurred handling request, sending a generic server error (500)");
                    sendGeneralServerError(context);
                }
            } catch (Exception e) {
                logger.debug("Error occurred handling request, sending a generic server error (500)", e);
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

        if (headers != null && !headers.isEmpty()) {
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

    private void sendContent(ChannelHandlerContext context, ByteBuffer contentBuffer) {
        HttpContent content = new DefaultHttpContent(contentBuffer == null ?
                Unpooled.EMPTY_BUFFER :
                Unpooled.wrappedBuffer(contentBuffer));

        context.writeAndFlush(content);
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
        Optional<Body> responseBody = serverResponse.body();
        if (responseBody != null && responseBody.isPresent()) {
            threadPool.execute(() -> {
                if (!channelClosed) {
                    boolean[] first = {true};
                    entitySubscription = responseBody.get().asBufferChunks()
                            .doOnNext(buffer -> {
                                if (first[0]) {
                                    sendResponseStart(context, originalRequest,
                                            serverResponse.status().getCode(),
                                            serverResponse.headers(), -1);
                                    sendContent(context, buffer);

                                    first[0] = false;
                                } else {
                                    sendContent(context, buffer);
                                }
                            })
                            .doAfterTerminate(() -> {
                                if (first[0]) {
                                    sendFullResponse(context, originalRequest,
                                            serverResponse.status().getCode(),
                                            serverResponse.headers());
                                } else {
                                    sendResponseEnd(context, originalRequest);
                                }
                            }).subscribe();
                }
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
