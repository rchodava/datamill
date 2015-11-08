package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.*;
import org.chodavarapu.datamill.values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestBuilderImpl implements RequestBuilder {
    private Entity entity;
    private String method;
    private final Map<String, String> headers = new HashMap<>();
    private String uri;
    private final Map<String, String> uriParameters = new HashMap<>();

    @Override
    public Request build() {
        return new RequestImpl(method, headers, uri, uriParameters, entity);
    }

    @Override
    public RequestBuilder entity(Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    public RequestBuilder entity(Value entity) {
        this.entity = new ValueEntity(entity);
        return this;
    }

    @Override
    public RequestBuilder header(RequestHeader header, String value) {
        return header(header.getName(), value);
    }

    @Override
    public RequestBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public RequestBuilder method(Method method) {
        this.method = method.name();
        return this;
    }

    @Override
    public RequestBuilder method(String method) {
        this.method = method;
        return this;
    }

    @Override
    public RequestBuilder uri(String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public <T> RequestBuilder uriParameter(String name, T value) {
        this.uriParameters.put(name, value.toString());
        return this;
    }
}
