package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.ResponseBuilder;
import org.chodavarapu.datamill.http.Status;
import org.chodavarapu.datamill.values.StringValue;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderImpl implements ResponseBuilder {
    private final Map<String, String> headers = new HashMap<>();

    @Override
    public Observable<Response> badRequest() {
        return Observable.just(new ResponseImpl(Status.BAD_REQUEST, headers));
    }

    @Override
    public Observable<Response> badRequest(String content) {
        return Observable.just(new ResponseImpl(Status.BAD_REQUEST, headers, new ValueEntity(new StringValue(content))));
    }

    @Override
    public <T> ResponseBuilder header(String name, T value) {
        headers.put(name, value.toString());
        return this;
    }

    @Override
    public Observable<Response> internalServerError() {
        return Observable.just(new ResponseImpl(Status.INTERNAL_SERVER_ERROR, headers));
    }

    @Override
    public Observable<Response> internalServerError(String content) {
        return Observable.just(new ResponseImpl(Status.INTERNAL_SERVER_ERROR, headers, new ValueEntity(new StringValue(content))));
    }

    @Override
    public Observable<Response> noContent() {
        return Observable.just(new ResponseImpl(Status.NO_CONTENT, headers));
    }

    @Override
    public Observable<Response> notFound() {
        return Observable.just(new ResponseImpl(Status.NOT_FOUND, headers));
    }

    @Override
    public Observable<Response> ok() {
        return Observable.just(new ResponseImpl(Status.OK, headers));
    }

    @Override
    public Observable<Response> ok(String content) {
        return Observable.just(new ResponseImpl(Status.OK, headers, new ValueEntity(new StringValue(content))));
    }

    @Override
    public Observable<Response> unauthorized() {
        return Observable.just(new ResponseImpl(Status.UNAUTHORIZED, headers));
    }

    @Override
    public Observable<Response> unauthorized(String content) {
        return Observable.just(new ResponseImpl(Status.UNAUTHORIZED, headers, new ValueEntity(new StringValue(content))));
    }
}
