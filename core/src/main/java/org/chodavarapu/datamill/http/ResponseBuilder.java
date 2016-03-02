package org.chodavarapu.datamill.http;

import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ResponseBuilder {
    Observable<Response> badRequest();
    Observable<Response> badRequest(String content);
    <T> ResponseBuilder header(String name, T value);
    Observable<Response> internalServerError();
    Observable<Response> internalServerError(String content);
    Observable<Response> noContent();
    Observable<Response> notFound();
    Observable<Response> ok();
    Observable<Response> ok(String content);
    Observable<Response> unauthorized();
    Observable<Response> unauthorized(String content);
}
