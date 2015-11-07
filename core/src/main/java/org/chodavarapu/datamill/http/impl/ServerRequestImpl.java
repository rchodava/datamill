package org.chodavarapu.datamill.http.impl;

import com.google.common.base.Joiner;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import org.chodavarapu.datamill.http.*;
import org.chodavarapu.datamill.values.StringValue;
import org.chodavarapu.datamill.values.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ServerRequestImpl implements ServerRequest {
    private RequestEntity entity;

    private final HttpServerRequest request;

    private Map<String, String> uriParameters;

    public ServerRequestImpl(HttpServerRequest request) {
        this.request = request;
        this.entity = new RequestEntityImpl(request);
    }

    @Override
    public RequestEntity entity() {
        return entity;
    }

    @Override
    public Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();

        MultiMap requestHeaders = request.headers();
        for (String header : requestHeaders.names()) {
            List<String> values = requestHeaders.getAll(header);
            if (values.size() > 1) {
                headers.put(header, Joiner.on(',').join(values));
            } else if (values.size() > 0) {
                headers.put(header, values.get(0));
            }
        }

        return headers;
    }

    @Override
    public Optional<Value> header(String header) {
        String value = request.getHeader(header);
        return Optional.ofNullable(value != null ? new StringValue(value) : null);
    }

    @Override
    public Optional<Value> header(RequestHeader header) {
        return header(header.getName());
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
