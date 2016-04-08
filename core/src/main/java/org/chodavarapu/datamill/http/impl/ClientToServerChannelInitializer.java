package org.chodavarapu.datamill.http.impl;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import org.chodavarapu.datamill.http.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ClientToServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(ClientToServerChannelInitializer.class);

    private final SslContext sslContext;
    private final Route route;

    public ClientToServerChannelInitializer(SslContext sslContext, Route route) {
        this.sslContext = sslContext;
        this.route = route;
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
        pipeline.addLast(new ClientToServerChannelHandler(route));
    }
}
