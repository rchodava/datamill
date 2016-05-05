package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.ResponseBuilder;
import org.chodavarapu.datamill.http.Status;
import org.chodavarapu.datamill.json.JsonArray;
import org.chodavarapu.datamill.json.JsonObject;
import org.json.JSONArray;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderTest {
    @Test
    public void responseCodes() {
        ResponseBuilder builder = new ResponseBuilderImpl();
        assertEquals(Status.BAD_REQUEST, builder.badRequest().status());
        assertEquals(Status.BAD_REQUEST, builder.badRequest("Content").status());
        assertEquals("Content", builder.badRequest("Content").entity().asString().toBlocking().last());
        assertEquals(Status.INTERNAL_SERVER_ERROR, builder.internalServerError().status());
        assertEquals(Status.INTERNAL_SERVER_ERROR, builder.internalServerError("Content").status());
        assertEquals("Content", builder.internalServerError("Content").entity().asString().toBlocking().last());
        assertEquals(Status.NOT_FOUND, builder.notFound().status());
        assertEquals(Status.OK, builder.ok().status());
        assertEquals(Status.OK, builder.ok("Content").status());
        assertEquals("Content", builder.ok("Content").entity().asString().toBlocking().last());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized().status());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized("Content").status());
        assertEquals("Content", builder.unauthorized("Content").entity().asString().toBlocking().last());
        assertEquals(Status.NO_CONTENT, builder.noContent().status());
        assertEquals(Status.FORBIDDEN, builder.forbidden().status());
        assertEquals(Status.FORBIDDEN, builder.forbidden("Content").status());
        assertEquals("Content", builder.forbidden("Content").entity().asString().toBlocking().last());
    }

    @Test
    public void streamingEntites() {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        ResponseBuilderImpl builder = new ResponseBuilderImpl(threadPool);

        builder.streamingEntity(observer -> {
            observer.onNext("Test Content ".getBytes());
            observer.onNext("More Content".getBytes());
            observer.onCompleted();
        });
        assertEquals("Test Content More Content", builder.ok().entity().asString().toBlocking().lastOrDefault(null));

        builder.streamingJson(observer -> {
            observer.onNext(new JsonObject().put("test", "value"));
            observer.onNext(new JsonArray(new String[] { "test1", "test2" }));
            observer.onCompleted();
        });

        JSONArray array = new JSONArray(builder.ok().entity().asString().toBlocking().lastOrDefault(null));
        assertEquals("value", array.getJSONObject(0).get("test"));
        assertEquals("test1", array.getJSONArray(1).get(0));
        assertEquals("test2", array.getJSONArray(1).get(1));
    }
}
