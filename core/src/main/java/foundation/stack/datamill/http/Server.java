package foundation.stack.datamill.http;

import foundation.stack.datamill.http.builder.RouteBuilder;
import foundation.stack.datamill.http.impl.ClientToServerChannelInitializer;
import foundation.stack.datamill.http.impl.RouteBuilderImpl;
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
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.net.ssl.SSLException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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
    private final ExecutorService threadPool;
    private final boolean daemon;

    private final static Certificate defaultCertificate = new SelfSignedCertificate();

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

        this.threadPool = Executors.newCachedThreadPool(daemon ?
            new DaemonThreadFactory(Executors.defaultThreadFactory()) :
            Executors.defaultThreadFactory());
    }

    public Server listen(String host, int port, Certificate certificate) {
        SslContext sslContext = null;

        if (certificate != null) {
            try {
                sslContext = createSslContext(certificate);
            } catch (SSLException e) {
                logger.error("Could not create sslContext", e);
            }
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
                .childHandler(new ClientToServerChannelInitializer(sslContext, threadPool, route, errorResponseConstructor))
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

    public Server listen(String host, int port, boolean secure) {
        return secure ? listen(host, port, defaultCertificate) : listen(host, port, null);
    }

    public Server listen(String host, int port) {
        return listen(host, port, null);
    }

    public Server listen(int port) {
        return listen("localhost", port, null);
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

    private SslContext createSslContext(Certificate certificate) throws SSLException {
        return SslContextBuilder.forServer(certificate.getCertificate(), certificate.getPrivateKey()).build();
    }

    public static class SelfSignedCertificate implements Certificate {

        private io.netty.handler.ssl.util.SelfSignedCertificate certificate;

        public SelfSignedCertificate() {
            try {
                certificate = new io.netty.handler.ssl.util.SelfSignedCertificate();
            } catch (CertificateException e) {
                logger.error("Could not create default certificate", e);
            }
        }

        public SelfSignedCertificate(String fqdn) {
            try {
                certificate = new io.netty.handler.ssl.util.SelfSignedCertificate(fqdn);
            } catch (CertificateException e) {
                logger.error("Could not create default certificate", e);
            }
        }

        @Override
        public InputStream getCertificate() {
            try {
                return new FileInputStream(certificate.certificate());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public InputStream getPrivateKey() {
            try {
                return new FileInputStream(certificate.privateKey());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class DaemonThreadFactory implements ThreadFactory {
        private final ThreadFactory threadFactory;

        public DaemonThreadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = threadFactory.newThread(r);
            if (thread != null) {
                thread.setDaemon(true);
            }

            return thread;
        }
    }
}
