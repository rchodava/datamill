package foundation.stack.datamill.http.impl;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Status;

import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseImpl implements Response {
    private Multimap<String, String> headers;
    private Status status;
    private Optional<Body> body;

    public ResponseImpl(Status status) {
        this(status, (Multimap<String, String>) null, null);
    }

    public ResponseImpl(Status status, Map<String, String> headers) {
        this(status, headers, null);
    }

    public ResponseImpl(Status status, Multimap<String, String> headers) {
        this(status, headers, null);
    }

    public  ResponseImpl(Status status, Body body) {
        this(status, (Multimap<String, String>) null, body);
    }

    public ResponseImpl(Status status, Map<String, String> headers, Body body) {
        this(status, Multimaps.forMap(headers), body);
    }

    public ResponseImpl(Status status, Multimap<String, String> headers, Body body) {
        this.status = status;
        this.headers = headers;

        this.body = Optional.ofNullable(body);
    }

    @Override
    public Optional<Body> body() {
        return body;
    }

    @Override
    public Multimap<String, String> headers() {
        return headers;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public String toString() {
        return "ResponseImpl{" +
                "headers=" + headers +
                ", status=" + status +
                ", body=" + body +
                '}';
    }
}
