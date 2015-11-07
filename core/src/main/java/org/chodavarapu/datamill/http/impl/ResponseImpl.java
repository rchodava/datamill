package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Status;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseImpl implements Response {
    private Map<String, String> headers;
    private Status status;
    private Entity entity;

    public ResponseImpl(Status status) {
        this(status, null, null);
    }

    public ResponseImpl(Status status, Map<String, String> headers) {
        this(status, headers, null);
    }

    public  ResponseImpl(Status status, Entity entity) {
        this(status, null, entity);
    }

    public ResponseImpl(Status status, Map<String, String> headers, Entity entity) {
        this.status = status;
        this.headers = headers;
        this.entity = entity;
    }

    @Override
    public Entity entity() {
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
