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
        assertEquals(Status.BAD_REQUEST, builder.badRequest().status());
        assertEquals(Status.BAD_REQUEST, builder.badRequest("Content").status());
        assertEquals("Content", builder.badRequest("Content").entity().asString().toBlocking().last());
        assertEquals(Status.INTERNAL_SERVER_ERROR, builder.internalServerError().status());
        assertEquals(Status.INTERNAL_SERVER_ERROR, builder.internalServerError("Content").status());
        assertEquals("Content", builder.internalServerError("Content").entity().asString().toBlocking().last());
        assertEquals(Status.NOT_FOUND, builder.notFound().status());
        assertEquals(Status.OK, builder.ok().status());
        assertEquals(Status.OK, builder.ok("Content").status());
        assertEquals("Content", builder.ok("Content").entity().asString().toBlocking().last());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized().status());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized("Content").status());
        assertEquals("Content", builder.unauthorized("Content").entity().asString().toBlocking().last());
        assertEquals(Status.NO_CONTENT, builder.noContent().status());
    }
}
