package foundation.stack.datamill.http;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import foundation.stack.datamill.http.impl.ValueBody;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import foundation.stack.datamill.values.StringValue;
import org.junit.Test;
import rx.Observable;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ClientTest {
    private void verifyConnectionSetup(TestClient client, Method method, String uri, Map<String, String> headers, String entity)
            throws Exception {
        HttpUriRequest request = client.getRequest();

        assertThat(request.getURI().toString(), equalTo(uri));
        assertThat(request.getMethod(), equalTo(method.name()));

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                verify(request, times(1)).addHeader(header.getKey().toLowerCase(), header.getValue());
            }
        }

        if (entity != null) {
            byte[] testAsBytes = entity.getBytes();
            verify(client.getSpiedPipedOutputStream()).write(testAsBytes, 0, testAsBytes.length);
        }
    }

    private void verifyConnectionSetup(TestClient client, Method method, String uri, Map<String, String> headers)
            throws Exception {
        verifyConnectionSetup(client, method, uri, headers, null);
    }

    private TestClient createClientAndRequest(Function<TestClient, Observable<Response>> requestor) {
        TestClient client = new TestClient();
        requestor.apply(client).toBlocking().last();
        return client;
    }

    @Test
    public void deleteRequests() throws Exception {
        verifyConnectionSetup(createClientAndRequest(c -> c.delete("http://sample.com")), Method.DELETE, "http://sample.com", null);
        verifyConnectionSetup(createClientAndRequest(c -> c.delete("http://sample.com",
                ImmutableMap.of("Authorization", "Bearer token"))),
                Method.DELETE, "http://sample.com", ImmutableMap.of("Authorization", "Bearer token"));
        verifyConnectionSetup(createClientAndRequest(c -> c.delete(
                rb -> rb.uri("http://sample.com")
                        .header("Authorization", "Bearer token")
                        .build())),
                Method.DELETE, "http://sample.com", ImmutableMap.of("Authorization", "Bearer token"));
    }

    @Test
    public void getRequests() throws Exception {
        verifyConnectionSetup(createClientAndRequest(c -> c.get("http://sample.com")), Method.GET, "http://sample.com", null);
        verifyConnectionSetup(createClientAndRequest(c -> c.get("http://sample.com",
                ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"))),
                Method.GET, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"));
        verifyConnectionSetup(createClientAndRequest(c -> c.get(
                rb -> rb.uri("http://sample.com")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .build())),
                Method.GET, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"));
        verifyConnectionSetup(createClientAndRequest(c -> c.get(
                rb -> rb.uri("http://sample.com")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .queryParameter("test", "value")
                        .queryParameter("test2", "value%$@")
                        .build())),
                Method.GET, "http://sample.com?test=value&test2=value%25%24%40", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"));
    }

    @Test
    public void patchRequests() throws Exception {
        verifyConnectionSetup(createClientAndRequest(c -> c.patch("http://sample.com",
                new StringValue("test"))),
                Method.PATCH, "http://sample.com", null, "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.patch("http://sample.com",
                new ValueBody(new StringValue("test")))),
                Method.PATCH, "http://sample.com", null, "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.patch("http://sample.com",
                ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"),
                new StringValue("test"))),
                Method.PATCH, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.patch("http://sample.com",
                ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"),
                new ValueBody(new StringValue("test")))),
                Method.PATCH, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.patch(
                rb -> rb.uri("http://sample.com")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .body(new StringValue("test"))
                        .build())),
                Method.PATCH, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
    }

    @Test
    public void postRequests() throws Exception {
        verifyConnectionSetup(createClientAndRequest(c -> c.post("http://sample.com",
                new StringValue("test"))),
                Method.POST, "http://sample.com", null, "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.post("http://sample.com",
                new ValueBody(new StringValue("test")))),
                Method.POST, "http://sample.com", null, "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.post("http://sample.com",
                ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"),
                new StringValue("test"))),
                Method.POST, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.post("http://sample.com",
                ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"),
                new ValueBody(new StringValue("test")))),
                Method.POST, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.post(
                rb -> rb.uri("http://sample.com")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .body(new StringValue("test"))
                        .build())),
                Method.POST, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
    }

    @Test
    public void putRequests() throws Exception {
        verifyConnectionSetup(createClientAndRequest(c -> c.put("http://sample.com",
                new StringValue("test"))),
                Method.PUT, "http://sample.com", null, "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.put("http://sample.com",
                new ValueBody(new StringValue("test")))),
                Method.PUT, "http://sample.com", null, "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.put("http://sample.com",
                ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"),
                new StringValue("test"))),
                Method.PUT, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.put("http://sample.com",
                ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"),
                new ValueBody(new StringValue("test")))),
                Method.PUT, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.put(
                rb -> rb.uri("http://sample.com")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .body(new StringValue("test"))
                        .build())),
                Method.PUT, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
    }

    private class TestClient extends Client {
        private PipedOutputStream spiedPipedOutputStream;
        private HttpUriRequest request;

        public TestClient() {
        }


        PipedOutputStream getSpiedPipedOutputStream() {
            return spiedPipedOutputStream;
        }

        @Override
        protected PipedOutputStream buildPipedOutputStream() {
            this.spiedPipedOutputStream =  spy(PipedOutputStream.class);
            return this.spiedPipedOutputStream;
        }

        @Override
        protected CloseableHttpResponse doExecute(CloseableHttpClient httpClient, HttpUriRequest request) throws IOException {
            CloseableHttpResponse response = mock(CloseableHttpResponse.class);
            when(response.getAllHeaders()).thenReturn(new Header[0]);
            when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"));
            when(response.getEntity()).thenReturn(mock(HttpEntity.class));

            if (request instanceof HttpEntityEnclosingRequest) {
                ByteStreams.toByteArray(((HttpEntityEnclosingRequest) request).getEntity().getContent());
            }

            return response;
        }

        public HttpUriRequest getRequest() {
            return request;
        }

        protected HttpUriRequest buildHttpRequest(Method method, URI uri) {
            switch (method) {
                case OPTIONS:
                    HttpOptions httpOptions = new HttpOptions(uri);
                    this.request = spy(httpOptions);
                    return request;
                case GET:
                    HttpGet httpGet = new HttpGet(uri);
                    this.request = spy(httpGet);
                    return request;
                case HEAD:
                    HttpHead httpHead = new HttpHead(uri);
                    this.request = spy(httpHead);
                    return request;
                case POST:
                    HttpPost httpPost = new HttpPost(uri);
                    this.request = spy(httpPost);
                    return request;
                case PUT:
                    HttpPut httpPut = new HttpPut(uri);
                    this.request = spy(httpPut);
                    return request;
                case DELETE:
                    HttpDelete httpDelete = new HttpDelete(uri);
                    this.request = spy(httpDelete);
                    return request;
                case TRACE:
                    HttpTrace httpTrace = new HttpTrace(uri);
                    this.request = spy(httpTrace);
                    return request;
                case PATCH:
                    HttpPatch httpPatch = new HttpPatch(uri);
                    this.request = spy(httpPatch);
                    return request;
                default: throw new IllegalArgumentException("Method " + method + " is not implemented!");
            }
        }
    }
}
