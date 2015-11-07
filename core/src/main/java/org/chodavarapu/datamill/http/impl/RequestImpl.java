package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.RequestEntity;
import org.chodavarapu.datamill.http.RequestHeader;
import org.chodavarapu.datamill.values.StringValue;
import org.chodavarapu.datamill.values.Value;

import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestImpl implements Request {
    private final Value entity;
    private final String method;
    private final Map<String, String> headers;
    private final String uri;

    public RequestImpl(String method, Map<String, String> headers, String uri, Value entity) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.entity = entity;
    }

    @Override
    public RequestEntity entity() {
        return null;
    }

    @Override
    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public Optional<Value> header(String header) {
        String value = headers.get(header);
        return Optional.ofNullable(value != null ? new StringValue(value) : null);
    }

    @Override
    public Optional<Value> header(RequestHeader header) {
        return header(header.getName());
    }

    @Override
    public Method method() {
        return Method.valueOf(method);
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public Value uriParameter(String parameter) {
        return null;
    }
}
