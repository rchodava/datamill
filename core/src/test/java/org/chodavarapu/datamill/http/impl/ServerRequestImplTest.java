package org.chodavarapu.datamill.http.impl;

import com.google.common.collect.ImmutableMultimap;
import org.chodavarapu.datamill.http.Method;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ServerRequestImplTest {
    @Test
    public void basicRequestProperties() {
        ServerRequestImpl request = new ServerRequestImpl(
                "GET", ImmutableMultimap.of("header1", "valueh1v1", "header1", "valueh1v2", "header2", "valueh2v1"),
                "http://localhost:8080?param1=value1&param2=value2&param2=value3", Charset.defaultCharset(), null);
        assertEquals(Method.GET, request.method());
        assertEquals("GET", request.rawMethod());
        assertEquals("valueh1v1", request.firstHeader("header1").asString());
        assertEquals("valueh2v1", request.firstHeader("header2").asString());
        assertEquals(null, request.firstHeader("header3"));
        assertEquals("value1", request.firstQueryParameter("param1").asString());
        assertEquals("value2", request.firstQueryParameter("param2").asString());
    }
}
