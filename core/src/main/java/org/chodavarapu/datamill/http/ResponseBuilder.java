package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.json.Json;
import rx.Observer;

import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ResponseBuilder {
    Response badRequest();
    Response badRequest(String content);
    <T> ResponseBuilder header(String name, T value);
    Response internalServerError();
    Response internalServerError(String content);
    Response noContent();
    Response notFound();
    Response ok();
    Response ok(String content);
    Response ok(byte[] content);
    ResponseBuilder streamingEntity(Consumer<Observer<byte[]>> entityStreamer);
    ResponseBuilder streamingJson(Consumer<Observer<Json>> jsonStreamer);
    Response unauthorized();
    Response unauthorized(String content);
    Response forbidden();
    Response forbidden(String content);
    Response conflict(String content);
}
