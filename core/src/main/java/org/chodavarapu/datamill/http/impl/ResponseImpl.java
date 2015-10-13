package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Status;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseImpl implements Response {
    private Map<String, String> headers;
    private Status status;
    private Object entity;

    public ResponseImpl(Status status) {
        this(status, null);
    }

    public ResponseImpl(Status status, Map<String, String> headers) {
        this(status, headers, null);
    }

    public  ResponseImpl(Status status, Object entity) {
        this(status, null, entity);
    }

    public ResponseImpl(Status status, Map<String, String> headers, Object entity) {
        this.status = status;
        this.headers = headers;
        this.entity = entity;
    }

    @Override
    public Object entity() {
        return entity;
    }

    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public Status status() {
        return status;
    }
}
