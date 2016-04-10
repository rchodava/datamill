package org.chodavarapu.datamill.http.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.chodavarapu.datamill.http.Route;
import org.chodavarapu.datamill.http.ServerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientToServerChannelHandlerTest {
    @Mock
    private ChannelHandlerContext context;

    @Mock
    private Route route;

    @Mock
    private ExecutorService threadPool;

    @Captor
    private ArgumentCaptor<ServerRequest> requestCaptor;

    @Captor
    private ArgumentCaptor<FullHttpResponse> responseCaptor;

    @Test
    public void sendContinueResponseIfRequested() {
        ClientToServerChannelHandler handler = new ClientToServerChannelHandler(threadPool, route, null);

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "localhost");
        request.headers().add(HttpHeaderNames.EXPECT, HttpHeaderValues.CONTINUE);

        handler.channelRead(context, request);

        verify(context).write(responseCaptor.capture());

        FullHttpResponse response = responseCaptor.getValue();
        assertEquals(HttpVersion.HTTP_1_1, response.protocolVersion());
        assertEquals(HttpResponseStatus.CONTINUE, response.status());
    }

    @Test
    public void readEntitySentWithFullRequest() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();

        ClientToServerChannelHandler handler = new ClientToServerChannelHandler(service, route, null);

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "localhost");
        request.content().writeBytes("Test Content".getBytes());

        when(route.apply(any())).thenReturn(new ResponseBuilderImpl().ok());

        handler.channelRead(context, request);

        service.shutdown();
        service.awaitTermination(1, TimeUnit.SECONDS);

        verify(route).apply(requestCaptor.capture());

        ServerRequest appliedRequest = requestCaptor.getValue();
        assertEquals("Test Content", appliedRequest.entity().asString().toBlocking().last());
    }

    @Test
    public void readEntitySentWithMultipleChunks() throws Exception {
        when(route.apply(any())).thenReturn(new ResponseBuilderImpl().ok());

        ExecutorService service = Executors.newSingleThreadExecutor();

        ClientToServerChannelHandler handler = new ClientToServerChannelHandler(service, route, null);

        DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "localhost");
        handler.channelRead(context, request);

        DefaultHttpContent content = new DefaultHttpContent(Unpooled.wrappedBuffer("Test Content ".getBytes()));
        handler.channelRead(context, content);

        content = new DefaultHttpContent(Unpooled.wrappedBuffer("Additional Content".getBytes()));
        handler.channelRead(context, content);

        DefaultLastHttpContent last = new DefaultLastHttpContent();
        handler.channelRead(context, last);

        service.shutdown();
        service.awaitTermination(1, TimeUnit.SECONDS);

        verify(route).apply(requestCaptor.capture());

        ServerRequest appliedRequest = requestCaptor.getValue();
        assertEquals("Test Content Additional Content", appliedRequest.entity().asString().toBlocking().last());
    }
}
