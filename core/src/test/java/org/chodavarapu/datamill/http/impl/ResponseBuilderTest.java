package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.ResponseBuilder;
import org.chodavarapu.datamill.http.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseBuilderTest {
    @Test
    public void responseCodes() {
        ResponseBuilder builder = new ResponseBuilderImpl();
        assertEquals(Status.NOT_FOUND, builder.notFound().toBlocking().last().status());
        assertEquals(Status.OK, builder.ok().toBlocking().last().status());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized().toBlocking().last().status());
        assertEquals(Status.NO_CONTENT, builder.noContent().toBlocking().last().status());
    }
}
