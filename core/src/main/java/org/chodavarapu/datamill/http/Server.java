package org.chodavarapu.datamill.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import org.chodavarapu.datamill.http.builder.RouteBuilder;
import org.chodavarapu.datamill.http.impl.ClientToServerChannelInitializer;
import org.chodavarapu.datamill.http.impl.ServerRequestImpl;
import org.chodavarapu.datamill.http.impl.RouteBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Server extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final Function<RouteBuilder, Route> routeConstructor;
    private final BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor;
    private HttpServer server;

    public Server(Function<RouteBuilder, Route> routeConstructor) {
        this(routeConstructor, null);
    }

    public Server(
            Function<RouteBuilder, Route> routeConstructor,
            BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor) {
        this.routeConstructor = routeConstructor;
        this.errorResponseConstructor = errorResponseConstructor;
    }

    private Observable<byte[]> sendResponse(Response response, HttpServerRequest originalRequest) {
        if (response != null) {
            originalRequest.response().setStatusCode(response.status().getCode());

            if (response.headers() != null) {
                for (Map.Entry<String, String> header : response.headers().entrySet()) {
                    originalRequest.response().headers().add(header.getKey(), header.getValue());
                }
            }

            if (response.entity() == null) {
                originalRequest.response().end();
            } else {
                return response.entity().asBytes()
                        .doOnNext(bytes -> originalRequest.response().end(Buffer.buffer(bytes)))
                        .doOnError(throwable -> originalRequest.response().end());
            }
        }

        return Observable.just(null);
    }

    private void sendGeneralServerError(HttpServerRequest originalRequest) {
        originalRequest.response().setStatusCode(500).end();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Route route = routeConstructor.apply(new RouteBuilderImpl());

        server = vertx.createHttpServer();
        server.requestHandler(r -> {
            Observable<Response> responseObservable = route.apply(new ServerRequestImpl(r));
            if (responseObservable != null) {
                responseObservable.flatMap(routeResponse -> sendResponse(routeResponse, r))
                        .onErrorResumeNext(t -> {
                            if (errorResponseConstructor != null) {
                                Observable<Response> errorResponseObservable =
                                        errorResponseConstructor.apply(new ServerRequestImpl(r), t);
                                if (errorResponseObservable != null) {
                                    return errorResponseObservable.flatMap(errorResponse -> sendResponse(errorResponse, r))
                                            .doOnError(secondError -> sendGeneralServerError(r))
                                            .map(__ -> null);
                                } else {
                                    sendGeneralServerError(r);
                                }
                            } else {
                                sendGeneralServerError(r);
                            }

                            return Observable.just(null);
                        })
                        .subscribe();
            } else {
                r.response().setStatusCode(404).end();
            }
        });

        startFuture.complete();
    }

    public Server listen(String host, int port, boolean secure) {
        SslContext sslContext = null;
        try {
            if (secure) {
                SelfSignedCertificate certificate = new SelfSignedCertificate();
                sslContext = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
            }
        } catch (SSLException | CertificateException e) {

        }

        Route route = routeConstructor.apply(new RouteBuilderImpl());

        NioEventLoopGroup commonEventLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(commonEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, 8)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                    .handler(new LoggingHandler())
                    .childHandler(new ClientToServerChannelInitializer(null, route))
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.AUTO_READ, false)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            try {
                logger.debug("Starting HTTP server on {}:{}", host, port);
                Channel serverChannel = bootstrap.bind(host, port).sync().channel();

                logger.debug("HTTP server listening on port {}:{}", host, port);
                serverChannel.closeFuture().sync();

                logger.debug("HTTP server shutting down");
            } catch (InterruptedException e) {
                logger.debug("Error occurred, HTTP server shutting down", e);
            }
        } finally {
            commonEventLoopGroup.shutdownGracefully();
        }

        return this;
    }

    public Server listen(String host, int port) {
        return listen(host, port, false);
    }

    public Server listen(int port) {
        return listen("localhost", port);
    }

    public Server listen(int port, boolean secure) {
        return listen("localhost", port, secure);
    }
}
