package org.chodavarapu.datamill.http.impl;

import io.vertx.core.http.HttpServerRequest;
import org.chodavarapu.datamill.http.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestImpl implements Request {
    private final HttpServerRequest request;

    public RequestImpl(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public RequestEntity entity() {
        return null;
    }

    @Override
    public Method method() {
        switch (request.method()) {
            case OPTIONS: return Method.OPTIONS;
            case GET: return Method.GET;
            case HEAD: return Method.HEAD;
            case POST: return Method.POST;
            case PUT: return Method.PUT;
            case DELETE: return Method.DELETE;
            case TRACE: return Method.TRACE;
            case CONNECT: return Method.CONNECT;
            case PATCH: return Method.PATCH;
        }

        return null;
    }

    @Override
    public ResponseBuilder respond() {
        return null;
    }

    @Override
    public String uri() {
        return request.uri();
    }
}
