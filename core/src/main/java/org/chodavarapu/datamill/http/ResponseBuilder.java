package org.chodavarapu.datamill.http;

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
    Response unauthorized();
    Response unauthorized(String content);
}
