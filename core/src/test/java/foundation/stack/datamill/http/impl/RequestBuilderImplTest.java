package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Request;
import foundation.stack.datamill.http.RequestHeader;
import foundation.stack.datamill.json.JsonArray;
import foundation.stack.datamill.json.JsonObject;
import org.apache.commons.io.output.ByteArrayOutputStream;
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
    public void body() {
        Request request = new RequestBuilderImpl()
                .body(new JsonObject().put("name", "value"))
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

        request = new RequestBuilderImpl()
                .body(new JsonArray("[{\"name\" : \"value\"}]"))
                .build();

        assertEquals("value", request.body().asJsonArray().toBlocking().last().get("name").asString());
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

    @Test
    public void requestHeaderBuildingIsCaseInsensitive() {
        Request request = new RequestBuilderImpl()
                .method(Method.GET)
                .header("HEADER1", "valueh1v1")
                .header("HEADER1", "valueh1v2")
                .header("HEADER2", "valueh2v1").build();

        assertEquals(Method.GET, request.method());
        assertEquals("GET", request.rawMethod());
        assertEquals("valueh1v1", request.firstHeader("header1").asString());
        assertEquals("valueh2v1", request.firstHeader("header2").asString());

        request = new RequestBuilderImpl()
                .method(Method.GET)
                .header("header1", "valueh1v1")
                .header("header1", "valueh1v2")
                .header("header2", "valueh2v1").build();

        assertEquals(Method.GET, request.method());
        assertEquals("GET", request.rawMethod());
        assertEquals("valueh1v1", request.firstHeader("header1").asString());
        assertEquals("valueh2v1", request.firstHeader("header2").asString());
    }
}
