package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.ResponseBuilder;
import org.chodavarapu.datamill.http.Status;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderImpl implements ResponseBuilder {
    @Override
    public Response noContent() {
        return new ResponseImpl(Status.NO_CONTENT);
    }

    @Override
    public Response notFound() {
        return new ResponseImpl(Status.NOT_FOUND);
    }

    @Override
    public Response ok() {
        return new ResponseImpl(Status.OK);
    }
}
