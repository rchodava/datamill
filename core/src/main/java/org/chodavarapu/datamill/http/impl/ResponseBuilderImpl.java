package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.ResponseBuilder;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderImpl implements ResponseBuilder {
    @Override
    public Response ok() {
        return new ResponseImpl(HttpServletResponse.SC_OK);
    }
}
