package foundation.stack.datamill.http.impl;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Request;
import foundation.stack.datamill.http.RequestHeader;
import foundation.stack.datamill.values.StringValue;
import foundation.stack.datamill.values.Value;

import java.util.Collection;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class AbstractRequestImpl implements Request {
    protected static Value firstValue(Multimap<String, String> entries, String name) {
        if (entries != null) {
            Collection<String> values = entries.get(name.toLowerCase());
            if (!values.isEmpty()) {
                return new StringValue(values.iterator().next());
            }
        }

        return null;
    }

    private final Multimap<String, String> headers;
    private final String method;
    private final String uri;
    private Map<String, String> uriParameters;
    private final Body body;

    protected AbstractRequestImpl(String method, Multimap<String, String> headers, String uri, Body body) {
        this.method = method;
        this.headers = headers != null ? headers : ArrayListMultimap.create();
        this.uri = uri;
        this.body = body;
    }

    @Override
    public Body body() {
        return body;
    }

    @Override
    public Value firstHeader(String header) {
        return firstValue(headers, header);
    }

    @Override
    public Value firstHeader(RequestHeader header) {
        return firstHeader(header.getName());
    }

    @Override
    public Value firstQueryParameter(String name) {
        return firstValue(queryParameters(), name);
    }

    @Override
    public Multimap<String, String> headers() {
        return headers;
    }

    @Override
    public Method method() {
        try {
            return Method.valueOf(method);
        } catch (IllegalArgumentException e) {
            return Method.UNKNOWN;
        }
    }

    @Override
    public String rawMethod() {
        return method;
    }

    protected void setUriParameters(Map<String, String> uriParameters) {
        this.uriParameters = uriParameters;
    }

    @Override
    public String uri() {
        return uri;
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

    @Override
    public Map<String, String> uriParameters() {
        return uriParameters;
    }
}
