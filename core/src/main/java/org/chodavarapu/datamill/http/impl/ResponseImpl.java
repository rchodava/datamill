package org.chodavarapu.datamill.http.impl;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Status;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseImpl implements Response {
    private Multimap<String, String> headers;
    private Status status;
    private Entity entity;

    public ResponseImpl(Status status) {
        this(status, (Multimap<String, String>) null, null);
    }

    public ResponseImpl(Status status, Map<String, String> headers) {
        this(status, headers, null);
    }

    public ResponseImpl(Status status, Multimap<String, String> headers) {
        this(status, headers, null);
    }

    public  ResponseImpl(Status status, Entity entity) {
        this(status, (Multimap<String, String>) null, entity);
    }

    public ResponseImpl(Status status, Map<String, String> headers, Entity entity) {
        this(status, Multimaps.forMap(headers), entity);
    }

    public ResponseImpl(Status status, Multimap<String, String> headers, Entity entity) {
        this.status = status;
        this.headers = headers;
        this.entity = entity;
    }

    @Override
    public Entity entity() {
        return entity;
    }

    @Override
    public Multimap<String, String> headers() {
        return headers;
    }

    @Override
    public Status status() {
        return status;
    }
}
