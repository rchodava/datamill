package foundation.stack.datamill.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import foundation.stack.datamill.http.builder.RouteBuilder;
import foundation.stack.datamill.http.impl.ClientToServerChannelInitializer;
import foundation.stack.datamill.http.impl.RouteBuilderImpl;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor;
    private EventLoopGroup eventLoopGroup;
    private final Function<RouteBuilder, Route> routeConstructor;
    private Channel serverChannel;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final boolean daemon;

    public Server(Function<RouteBuilder, Route> routeConstructor) {
        this(routeConstructor, null);
    }

    public Server(Function<RouteBuilder, Route> routeConstructor, boolean daemon) {
        this(routeConstructor, null, daemon);
    }

    public Server(
            Function<RouteBuilder, Route> routeConstructor,
            BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor) {
        this(routeConstructor, errorResponseConstructor, false);
    }

    public Server(
            Function<RouteBuilder, Route> routeConstructor,
            BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor,
            boolean daemon) {
        this.routeConstructor = routeConstructor;
        this.errorResponseConstructor = errorResponseConstructor;
        this.daemon = daemon;
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

        eventLoopGroup = daemon ?
                new NioEventLoopGroup(0, new DefaultThreadFactory(NioEventLoopGroup.class, true)) :
                new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 8)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15000)
                .handler(new LoggingHandler())
                .childHandler(new ClientToServerChannelInitializer(null, threadPool, route, errorResponseConstructor))
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        try {
            logger.debug("Starting HTTP server on {}:{}", host, port);
            serverChannel = bootstrap.bind(host, port).sync().channel();
            logger.debug("HTTP server listening on port {}:{}", host, port);
        } catch (InterruptedException e) {
            logger.debug("Error occurred while HTTP server was listening on {}:{}", host, port, e);
            stop();
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

    public void stop() {
        try {
            logger.debug("Shutting down HTTP server");
            serverChannel.close().sync();
        } catch (InterruptedException e) {
            logger.debug("Error occurred during HTTP server shut down", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
            logger.debug("HTTP server was shut down");
        }
    }
}
