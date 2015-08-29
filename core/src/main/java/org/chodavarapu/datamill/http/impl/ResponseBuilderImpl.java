package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.ResponseBuilder;
import org.chodavarapu.datamill.http.Status;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderImpl implements ResponseBuilder {
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
}
