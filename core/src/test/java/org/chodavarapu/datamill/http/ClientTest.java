package org.chodavarapu.datamill.http;

import com.google.common.collect.ImmutableMap;
import org.chodavarapu.datamill.http.impl.ValueEntity;
import org.chodavarapu.datamill.values.StringValue;
import org.junit.Test;
import rx.Observable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ClientTest {
    private void verifyConnectionSetup(TestClient client, Method method, String uri, Map<String, String> headers, String entity)
            throws Exception {
        assertEquals(uri, client.getLastUri());
        verify(client.getMockConnection(), times(1)).setRequestMethod(method.name());

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                verify(client.getMockConnection(), times(1)).addRequestProperty(header.getKey(), header.getValue());
            }
        } else {
            verify(client.getMockConnection(), times(0)).addRequestProperty(anyString(), anyString());
        }

        if (entity != null) {
            assertEquals(entity, client.getLastOutputValue());
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
    }

    @Test
    public void patchRequests() throws Exception {
        verifyConnectionSetup(createClientAndRequest(c -> c.patch("http://sample.com",
                new StringValue("test"))),
                Method.PATCH, "http://sample.com", null, "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.patch("http://sample.com",
                new ValueEntity(new StringValue("test")))),
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
                new ValueEntity(new StringValue("test")))),
                Method.PATCH, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.patch(
                rb -> rb.uri("http://sample.com")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .entity(new StringValue("test"))
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
                new ValueEntity(new StringValue("test")))),
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
                new ValueEntity(new StringValue("test")))),
                Method.POST, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.post(
                rb -> rb.uri("http://sample.com")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .entity(new StringValue("test"))
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
                new ValueEntity(new StringValue("test")))),
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
                new ValueEntity(new StringValue("test")))),
                Method.PUT, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
        verifyConnectionSetup(createClientAndRequest(c -> c.put(
                rb -> rb.uri("http://sample.com")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .entity(new StringValue("test"))
                        .build())),
                Method.PUT, "http://sample.com", ImmutableMap.of(
                        "Content-Type", "application/json",
                        "Accept", "application/json"), "test");
    }

    private class TestClient extends Client {
        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        private String uri;
        private HttpURLConnection mockConnection;

        public TestClient() {
            mockConnection = mock(HttpURLConnection.class);
            try {
                when(mockConnection.getOutputStream()).thenReturn(outputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected URLConnection createConnection(String uri) throws IOException {
            this.uri = uri;
            return mockConnection;
        }

        public String getLastUri() {
            return uri;
        }

        public HttpURLConnection getMockConnection() {
            return mockConnection;
        }

        public String getLastOutputValue() {
            return new String(outputStream.toByteArray());
        }
    }
}
