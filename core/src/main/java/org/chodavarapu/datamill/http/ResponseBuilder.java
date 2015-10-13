package org.chodavarapu.datamill.http;

import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ResponseBuilder {
    <T> ResponseBuilder header(String name, T value);
    Observable<Response> noContent();
    Observable<Response> notFound();
    Observable<Response> ok();
    Observable<Response> ok(String content);
    Observable<Response> unauthorized();
}
