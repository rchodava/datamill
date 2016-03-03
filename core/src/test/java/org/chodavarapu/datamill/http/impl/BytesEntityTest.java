package org.chodavarapu.datamill.http.impl;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BytesEntityTest {
    @Test
    public void entityTests() {
        assertEquals("test", new BytesEntity("test".getBytes()).asString().toBlocking().last());
        assertEquals("test", new String(new BytesEntity("test".getBytes()).asBytes().toBlocking().last()));
        assertEquals("test", new String(new BytesEntity("test".getBytes())
                .asChunks()
                .collect(
                        () -> new ByteArrayOutputStream(),
                        (stream, chunk) -> {
                            try {
                                stream.write(chunk);
                            } catch (IOException e) {
                            }
                        })
                .map(stream -> stream.toByteArray()).toBlocking().last()));
        assertEquals("value", new BytesEntity("{\"name\":\"value\"}".getBytes())
                .asJson().toBlocking().last().get("name").asString());
    }
}
