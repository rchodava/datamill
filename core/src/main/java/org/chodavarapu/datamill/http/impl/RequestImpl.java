package org.chodavarapu.datamill.http.impl;

import io.vertx.core.http.HttpServerRequest;
import org.chodavarapu.datamill.http.*;
import org.chodavarapu.datamill.values.StringValue;
import org.chodavarapu.datamill.values.Value;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestImpl implements Request {
    private RequestEntity entity;

    private final HttpServerRequest request;

    private Map<String, String> uriParameters;

    public RequestImpl(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public RequestEntity entity() {
        if (entity == null) {
            entity = new RequestEntityImpl(request);
        }

        return entity;
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
        return new ResponseBuilderImpl();
    }

    @Override
    public String uri() {
        return request.uri();
    }

    void setUriParameters(Map<String, String> uriParameters) {
        this.uriParameters = uriParameters;
    }

    @Override
    public Value uriParameter(String parameter) {
        if (uriParameters != null) {
            String value = uriParameters.get(parameter);
            if (value != null) {
                return new StringValue(value);
            }
        }
        return null;
    }
}
