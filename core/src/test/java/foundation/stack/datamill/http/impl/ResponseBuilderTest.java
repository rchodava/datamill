package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Status;
import foundation.stack.datamill.http.ResponseBuilder;
import foundation.stack.datamill.json.JsonArray;
import foundation.stack.datamill.json.JsonObject;
import org.json.JSONArray;
import org.junit.Test;
import rx.Observable;

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
        assertEquals("Content", builder.badRequest("Content").body().get().asString().toBlocking().last());
        assertEquals(Status.INTERNAL_SERVER_ERROR, builder.internalServerError().status());
        assertEquals(Status.INTERNAL_SERVER_ERROR, builder.internalServerError("Content").status());
        assertEquals("Content", builder.internalServerError("Content").body().get().asString().toBlocking().last());
        assertEquals(Status.NOT_FOUND, builder.notFound().status());
        assertEquals(Status.OK, builder.ok().status());
        assertEquals(Status.OK, builder.ok("Content").status());
        assertEquals("Content", builder.ok("Content").body().get().asString().toBlocking().last());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized().status());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized("Content").status());
        assertEquals("Content", builder.unauthorized("Content").body().get().asString().toBlocking().last());
        assertEquals(Status.NO_CONTENT, builder.noContent().status());
        assertEquals(Status.FORBIDDEN, builder.forbidden().status());
        assertEquals(Status.FORBIDDEN, builder.forbidden("Content").status());
        assertEquals("Content", builder.forbidden("Content").body().get().asString().toBlocking().last());
        assertEquals(Status.CONFLICT, builder.conflict("Content").status());
        assertEquals("Content", builder.conflict("Content").body().get().asString().toBlocking().last());
        assertEquals(Status.TEMPORARY_REDIRECT, builder.status(Status.TEMPORARY_REDIRECT).status());
        assertEquals(Status.TEMPORARY_REDIRECT, builder.status(Status.TEMPORARY_REDIRECT, "Content").status());
        assertEquals("Content", builder.status(Status.TEMPORARY_REDIRECT, "Content").body().get().asString().toBlocking().last());
    }

    @Test
    public void streamingEntites() {
        ResponseBuilderImpl builder = new ResponseBuilderImpl();

        builder.streamingBody(observer -> {
            observer.onNext("Test Content ".getBytes());
            observer.onNext("More Content".getBytes());

            return Observable.empty();
        });
        assertEquals("Test Content More Content", builder.ok().body().get().asString().toBlocking().lastOrDefault(null));
    }

    @Test
    public void streamingJson() {
        ResponseBuilderImpl builder = new ResponseBuilderImpl();

        builder.streamingJson(observer -> {
            observer.onNext(new JsonObject().put("test", "value"));
            observer.onNext(new JsonArray(new String[] { "test1", "test2" }));

            return Observable.empty();
        });

        JSONArray streamingJsonArray = new JSONArray(builder.ok().body().get().asString().toBlocking().lastOrDefault(null));
        assertEquals("value", streamingJsonArray.getJSONObject(0).get("test"));
        assertEquals("test1", streamingJsonArray.getJSONArray(1).get(0));
        assertEquals("test2", streamingJsonArray.getJSONArray(1).get(1));

        builder.streamingJson(observer -> {
            observer.onNext(new JsonObject().put("test", "value"));
            observer.onNext(new JsonArray(new String[] { "test1", "test2" }));

            return Observable.concat(
                    Observable.just(new JsonObject().put("response1", "response1value")),
                    Observable.just(new JsonObject().put("response2", "response2value")));
        });

        streamingJsonArray = new JSONArray(builder.ok().body().get().asString().toBlocking().lastOrDefault(null));
        assertEquals("value", streamingJsonArray.getJSONObject(0).get("test"));
        assertEquals("test1", streamingJsonArray.getJSONArray(1).get(0));
        assertEquals("test2", streamingJsonArray.getJSONArray(1).get(1));
        assertEquals("response1value", streamingJsonArray.getJSONObject(2).get("response1"));
        assertEquals("response2value", streamingJsonArray.getJSONObject(3).get("response2"));

        builder.streamingJson(observer -> Observable.concat(
                    Observable.just(new JsonObject().put("response1", "response1value")),
                    Observable.just(new JsonObject().put("response2", "response2value"))));

        streamingJsonArray = new JSONArray(builder.ok().body().get().asString().toBlocking().lastOrDefault(null));
        assertEquals("response1value", streamingJsonArray.getJSONObject(0).get("response1"));
        assertEquals("response2value", streamingJsonArray.getJSONObject(1).get("response2"));

        builder.streamingJson(observer -> {
            observer.onNext(new JsonObject().put("test", "value"));

            return Observable.just(new JsonObject().put("response2", "response2value"));
        });

        streamingJsonArray = new JSONArray(builder.ok().body().get().asString().toBlocking().lastOrDefault(null));
        assertEquals("value", streamingJsonArray.getJSONObject(0).get("test"));
        assertEquals("response2value", streamingJsonArray.getJSONObject(1).get("response2"));
    }
}
