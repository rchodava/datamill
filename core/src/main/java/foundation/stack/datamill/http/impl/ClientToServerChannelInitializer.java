package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Route;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import foundation.stack.datamill.http.ServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ClientToServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(ClientToServerChannelInitializer.class);

    private final BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor;
    private final Route route;
    private final SslContext sslContext;
    private final ExecutorService threadPool;

    public ClientToServerChannelInitializer(SslContext sslContext, ExecutorService threadPool,
                                            Route route, BiFunction<ServerRequest, Throwable, Observable<Response>> errorResponseConstructor) {
        this.sslContext = sslContext;
        this.threadPool = threadPool;

        this.route = route;
        this.errorResponseConstructor = errorResponseConstructor;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        logger.debug("Initializing channel from client {} to the server", channel.remoteAddress());

        ChannelPipeline pipeline = channel.pipeline();

        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(channel.alloc()));
        }

        pipeline.addLast(new HttpServerCodec(4096, 8192, 65536));
        pipeline.addLast(new HttpContentDecompressor());
        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new ClientToServerChannelHandler(threadPool, route, errorResponseConstructor));
    }
}
