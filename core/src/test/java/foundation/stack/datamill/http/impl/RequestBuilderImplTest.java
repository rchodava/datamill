package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Request;
import foundation.stack.datamill.http.RequestHeader;
import org.apache.commons.io.output.ByteArrayOutputStream;
import foundation.stack.datamill.json.JsonObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestBuilderImplTest {
    @Test
    public void requestBuilding() {
        Request request = new RequestBuilderImpl()
                .connectTimeout(500)
                .header(RequestHeader.ACCEPT, "application/json")
                .header("Content-Type", "application/json")
                .method(Method.GET)
                .uri("http://sample.com")
                .build();

        assertEquals("application/json", request.firstHeader("Accept").asString());
        assertEquals("application/json", request.firstHeader(RequestHeader.CONTENT_TYPE).asString());
        assertEquals(Method.GET, request.method());
        assertEquals("http://sample.com", request.uri());
        assertEquals(500, request.options().get(Request.OPTION_CONNECT_TIMEOUT));
    }

    @Test
    public void entity() {
        Request request = new RequestBuilderImpl()
                .entity(new JsonObject().put("name", "value"))
                .build();

        assertEquals("value", request.body().asJson().toBlocking().last().get("name").asString());
        assertEquals("value", new JsonObject(request.body().asString().toBlocking().last()).get("name").asString());
        assertEquals("value", new JsonObject(new String(request.body().asBytes().toBlocking().last())).get("name").asString());
        assertEquals("value", new JsonObject(new String(request.body().asChunks()
                .collect(
                        () -> new ByteArrayOutputStream(),
                        (stream, chunk) -> {
                            try {
                                stream.write(chunk);
                            } catch (IOException e) { }
                        })
                .map(stream -> stream.toByteArray())
                .toBlocking().last())).get("name").asString());
    }

    @Test
    public void methods() {
        assertEquals(Method.GET, new RequestBuilderImpl().method("GET").build().method());
        assertEquals(Method.HEAD, new RequestBuilderImpl().method("HEAD").build().method());
        assertEquals(Method.POST, new RequestBuilderImpl().method("POST").build().method());
        assertEquals(Method.PUT, new RequestBuilderImpl().method("PUT").build().method());
        assertEquals(Method.DELETE, new RequestBuilderImpl().method("DELETE").build().method());
        assertEquals(Method.TRACE, new RequestBuilderImpl().method("TRACE").build().method());
        assertEquals(Method.PATCH, new RequestBuilderImpl().method("PATCH").build().method());
    }
}
