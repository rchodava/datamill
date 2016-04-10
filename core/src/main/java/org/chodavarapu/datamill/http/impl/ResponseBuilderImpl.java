package org.chodavarapu.datamill.http.impl;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.ResponseBuilder;
import org.chodavarapu.datamill.http.Status;
import org.chodavarapu.datamill.values.StringValue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderImpl implements ResponseBuilder {
    private final Multimap<String, String> headers = LinkedListMultimap.create();

    @Override
    public Response badRequest() {
        return new ResponseImpl(Status.BAD_REQUEST, headers);
    }

    @Override
    public Response badRequest(String content) {
        return new ResponseImpl(Status.BAD_REQUEST, headers, new ValueEntity(new StringValue(content)));
    }

    @Override
    public <T> ResponseBuilder header(String name, T value) {
        headers.put(name, value.toString());
        return this;
    }

    @Override
    public Response internalServerError() {
        return new ResponseImpl(Status.INTERNAL_SERVER_ERROR, headers);
    }

    @Override
    public Response internalServerError(String content) {
        return new ResponseImpl(Status.INTERNAL_SERVER_ERROR, headers, new ValueEntity(new StringValue(content)));
    }

    @Override
    public Response noContent() {
        return new ResponseImpl(Status.NO_CONTENT, headers);
    }

    @Override
    public Response notFound() {
        return new ResponseImpl(Status.NOT_FOUND, headers);
    }

    @Override
    public Response ok() {
        return new ResponseImpl(Status.OK, headers);
    }

    @Override
    public Response ok(String content) {
        return new ResponseImpl(Status.OK, headers, new ValueEntity(new StringValue(content)));
    }

    @Override
    public Response ok(byte[] content) {
        return new ResponseImpl(Status.OK, headers, new BytesEntity(content));
    }

    @Override
    public Response unauthorized() {
        return new ResponseImpl(Status.UNAUTHORIZED, headers);
    }

    @Override
    public Response unauthorized(String content) {
        return new ResponseImpl(Status.UNAUTHORIZED, headers, new ValueEntity(new StringValue(content)));
    }
}
