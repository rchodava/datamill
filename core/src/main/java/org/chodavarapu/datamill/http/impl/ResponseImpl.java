package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Status;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseImpl implements Response {
    private Status status;
    private Object entity;

    public ResponseImpl(Status status) {
        this.status = status;
    }

    public ResponseImpl(Status status, Object entity) {
        this.status = status;
        this.entity = entity;
    }

    @Override
    public Object entity() {
        return entity;
    }

    @Override
    public Status status() {
        return status;
    }
}
