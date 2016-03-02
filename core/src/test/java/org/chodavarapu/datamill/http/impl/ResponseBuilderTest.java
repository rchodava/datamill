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
        assertEquals(Status.BAD_REQUEST, builder.badRequest().toBlocking().last().status());
        assertEquals(Status.BAD_REQUEST, builder.badRequest("Content").toBlocking().last().status());
        assertEquals("Content", builder.badRequest("Content").toBlocking().last().entity().asString().toBlocking().last());
        assertEquals(Status.INTERNAL_SERVER_ERROR, builder.internalServerError().toBlocking().last().status());
        assertEquals(Status.INTERNAL_SERVER_ERROR, builder.internalServerError("Content").toBlocking().last().status());
        assertEquals("Content", builder.internalServerError("Content").toBlocking().last().entity().asString().toBlocking().last());
        assertEquals(Status.NOT_FOUND, builder.notFound().toBlocking().last().status());
        assertEquals(Status.OK, builder.ok().toBlocking().last().status());
        assertEquals(Status.OK, builder.ok("Content").toBlocking().last().status());
        assertEquals("Content", builder.ok("Content").toBlocking().last().entity().asString().toBlocking().last());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized().toBlocking().last().status());
        assertEquals(Status.UNAUTHORIZED, builder.unauthorized("Content").toBlocking().last().status());
        assertEquals("Content", builder.unauthorized("Content").toBlocking().last().entity().asString().toBlocking().last());
        assertEquals(Status.NO_CONTENT, builder.noContent().toBlocking().last().status());
    }
}
