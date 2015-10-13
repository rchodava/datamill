package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.ResponseBuilder;
import org.chodavarapu.datamill.http.Status;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderImpl implements ResponseBuilder {
    private final Map<String, String> headers = new HashMap<>();

    @Override
    public <T> ResponseBuilder header(String name, T value) {
        headers.put(name, value.toString());
        return this;
    }

    @Override
    public Observable<Response> noContent() {
        return Observable.just(new ResponseImpl(Status.NO_CONTENT));
    }

    @Override
    public Observable<Response> notFound() {
        return Observable.just(new ResponseImpl(Status.NOT_FOUND));
    }

    @Override
    public Observable<Response> ok() {
        return Observable.just(new ResponseImpl(Status.OK));
    }

    @Override
    public Observable<Response> ok(String content) {
        return Observable.just(new ResponseImpl(Status.OK, content));
    }

    @Override
    public Observable<Response> unauthorized() {
        return Observable.just(new ResponseImpl(Status.UNAUTHORIZED));
    }
}
