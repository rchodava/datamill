package org.chodavarapu.datamill.http.impl;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.RequestHeader;
import org.chodavarapu.datamill.json.JsonObject;
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
                .header(RequestHeader.ACCEPT, "application/json")
                .header("Content-Type", "application/json")
                .method(Method.GET)
                .uri("http://sample.com")
                .build();

        assertEquals("application/json", request.header("Accept").get().asString());
        assertEquals("application/json", request.header(RequestHeader.CONTENT_TYPE).get().asString());
        assertEquals(Method.GET, request.method());
        assertEquals("http://sample.com", request.uri());
    }

    @Test
    public void entity() {
        Request request = new RequestBuilderImpl()
                .entity(new JsonObject().put("name", "value"))
                .build();

        assertEquals("value", request.entity().asJson().toBlocking().last().get("name").asString());
        assertEquals("value", new JsonObject(request.entity().asString().toBlocking().last()).get("name").asString());
        assertEquals("value", new JsonObject(new String(request.entity().asBytes().toBlocking().last())).get("name").asString());
        assertEquals("value", new JsonObject(new String(request.entity().asChunks()
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
        assertEquals(Method.CONNECT, new RequestBuilderImpl().method("CONNECT").build().method());
        assertEquals(Method.GET, new RequestBuilderImpl().method("GET").build().method());
        assertEquals(Method.HEAD, new RequestBuilderImpl().method("HEAD").build().method());
        assertEquals(Method.POST, new RequestBuilderImpl().method("POST").build().method());
        assertEquals(Method.PUT, new RequestBuilderImpl().method("PUT").build().method());
        assertEquals(Method.DELETE, new RequestBuilderImpl().method("DELETE").build().method());
        assertEquals(Method.TRACE, new RequestBuilderImpl().method("TRACE").build().method());
        assertEquals(Method.CONNECT, new RequestBuilderImpl().method("CONNECT").build().method());
        assertEquals(Method.PATCH, new RequestBuilderImpl().method("PATCH").build().method());
    }
}
