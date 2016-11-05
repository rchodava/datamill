package foundation.stack.datamill.http.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
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

    @Captor
    private ArgumentCaptor<HttpObject> responseFragmentsCaptor;

    @Captor
    private ArgumentCaptor<HttpObject> responseStartCaptor;

    private void waitForExecutorToFinishAllTasks(ExecutorService executor) throws Exception {
        for (int i = 0; i < 5; i++) {
            // We submit an empty task and wait for it so that other tasks submitted ahead of this get executed first
            // and we wait for their completion. But each of those may have submitted others which we need to wait for
            // as well which is why this is done in a loop.
            executor.submit(() -> {}).get();
        }
    }

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

        when(route.apply(any())).thenReturn(Observable.just(new ResponseBuilderImpl().ok()));

        handler.channelRead(context, request);

        waitForExecutorToFinishAllTasks(service);

        verify(route).apply(requestCaptor.capture());

        ServerRequest appliedRequest = requestCaptor.getValue();
        assertEquals("Test Content", appliedRequest.body().asString().toBlocking().last());
    }

    @Test
    public void readEntitySentWithMultipleChunks() throws Exception {
        when(route.apply(any())).thenReturn(Observable.just(new ResponseBuilderImpl().ok()));

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

        waitForExecutorToFinishAllTasks(service);

        verify(route).apply(requestCaptor.capture());

        ServerRequest appliedRequest = requestCaptor.getValue();
        assertEquals("Test Content Additional Content", appliedRequest.body().asString().toBlocking().last());
    }

    @Test
    public void singleChunkResponseSent() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        ClientToServerChannelHandler handler = new ClientToServerChannelHandler(service, route, null);

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "localhost");

        when(route.apply(any())).thenReturn(Observable.just(new ResponseBuilderImpl().ok()));

        handler.channelRead(context, request);

        waitForExecutorToFinishAllTasks(service);

        verify(context).writeAndFlush(responseCaptor.capture());

        assertEquals(HttpResponseStatus.OK, responseCaptor.getValue().status());
    }

    @Test
    public void multipleResponseChunksSent() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();
        ClientToServerChannelHandler handler = new ClientToServerChannelHandler(service, route, null);

        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "localhost");

        when(route.apply(any())).thenReturn(Observable.just(new ResponseBuilderImpl().ok("Test Content")));

        handler.channelRead(context, request);

        waitForExecutorToFinishAllTasks(service);

        verify(context).write(responseStartCaptor.capture());
        verify(context, times(2)).writeAndFlush(responseFragmentsCaptor.capture());

        assertEquals(HttpResponseStatus.OK, ((HttpResponse) responseStartCaptor.getValue()).status());
        byte[] bytes = new byte[((HttpContent) responseFragmentsCaptor.getAllValues().get(0)).content().readableBytes()];
        ((HttpContent) responseFragmentsCaptor.getAllValues().get(0)).content().readBytes(bytes);
        assertArrayEquals("Test Content".getBytes(), bytes);
    }
}
