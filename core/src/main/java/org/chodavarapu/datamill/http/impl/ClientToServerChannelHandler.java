package org.chodavarapu.datamill.http.impl;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.chodavarapu.datamill.http.Route;
import rx.subjects.PublishSubject;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ClientToServerChannelHandler extends ChannelInboundHandlerAdapter {
    private final Route route;
    private PublishSubject<byte[]> entityStream;

    private final StringBuilder buf = new StringBuilder();

    public ClientToServerChannelHandler(Route route) {
        this.route = route;
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

            entityStream = PublishSubject.create();

            Charset messageCharset = HttpUtil.getCharset(request);

            new ServerRequestImpl(
                    request.method().name(),
                    extractHeaders(request),
                    request.uri(),
                    messageCharset,
                    new RequestEntity(entityStream, messageCharset));

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
                entityStream.onCompleted();

                LastHttpContent trailer = (LastHttpContent) message;
                if (!trailer.trailingHeaders().isEmpty()) {
                    buf.append("\r\n");
                    for (CharSequence name: trailer.trailingHeaders().names()) {
                        for (CharSequence value: trailer.trailingHeaders().getAll(name)) {
                            buf.append("TRAILING HEADER: ");
                            buf.append(name).append(" = ").append(value).append("\r\n");
                        }
                    }
                    buf.append("\r\n");
                }

                if (!writeResponse(trailer, context)) {
                    // If keep-alive is off, close the connection once the content is fully written.
                    // TODO: DO not flush after every read, instead wait till channel read complete and flush after writes
                    // TODO: Use void channel prommise
                    // TODO: Use channel is writeable
                    context.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }

    private Multimap<String, String> extractHeaders(HttpRequest request) {
        Multimap<String, String> headers;

        HttpHeaders requestHeaders = request.headers();
        if (!requestHeaders.isEmpty()) {
            ImmutableMultimap.Builder<String, String> builder = ImmutableMultimap.builder();

            for (Map.Entry<String, String> header : requestHeaders) {
                String key = header.getKey();
                String value = header.getValue();

                if (key != null && value != null) {
                    builder.put(key, value);
                }
            }

            headers = builder.build();
        } else {
            headers = null;
        }

        return headers;
    }


    //    private void writeIfPossible(Channel channel) {
//        while(needsToWrite && channel.isWritable()) {
//            channel.writeAndFlush(createMessage());
//        }
//    }
//
    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, currentObj.decoderResult().isSuccess()? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        if (keepAlive) {
            // Add 'Content-Length' header only for a keep-alive connection.
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            // Add keep alive header as per:
            // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Encode the cookie.
        String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                for (Cookie cookie: cookies) {
                    response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
                }
            }
        } else {
            // Browser sent no cookie.  Add some.
            response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key1", "value1"));
            response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key2", "value2"));
        }

        // Write the response.
        ctx.write(response);

        return keepAlive;
    }

    private static void sendContinueResponse(ChannelHandlerContext context) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        context.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
