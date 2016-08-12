package foundation.stack.datamill.http.impl;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.*;
import foundation.stack.datamill.values.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestBuilderImpl implements RequestBuilder {
    private Body body;
    private final Multimap<String, String> headers = LinkedListMultimap.create();
    private String method;
    private final Map<String, Object> options = new HashMap<>();
    private final Multimap<String, String> queryParameters = LinkedListMultimap.create();
    private String uri;
    private final Map<String, String> uriParameters = new HashMap<>();

    @Override
    public Request build() {
        return new RequestImpl(method, headers, uri, queryParameters, uriParameters, options, body);
    }

    @Override
    public RequestBuilder connectTimeout(int milliseconds) {
        options.put(Request.OPTION_CONNECT_TIMEOUT, milliseconds);
        return this;
    }

    @Override
    public RequestBuilder body(Body body) {
        this.body = body;
        return this;
    }

    @Override
    public RequestBuilder body(Value entity) {
        this.body = new ValueBody(entity);
        return this;
    }

    @Override
    public RequestBuilder header(RequestHeader header, String value) {
        return header(header.getName(), value);
    }

    @Override
    public RequestBuilder header(String name, String value) {
        headers.put(name.toLowerCase(), value);
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
    public RequestBuilder queryParameter(String name, String value) {
        queryParameters.put(name, value);
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
