package foundation.stack.datamill.http.impl;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BytesBodyTest {
    @Test
    public void entityTests() {
        assertEquals("test", new BytesBody("test".getBytes()).asString().toBlocking().last());
        assertEquals("test", new String(new BytesBody("test".getBytes()).asBytes().toBlocking().last()));
        assertEquals("test", new String(new BytesBody("test".getBytes())
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
        Assert.assertEquals("value", new BytesBody("{\"name\":\"value\"}".getBytes())
                .asJson().toBlocking().last().get("name").asString());
        assertEquals("value", new BytesBody("[{\"name\":\"value\"}]".getBytes())
                .asJsonArray().toBlocking().last().get("name").asString());
    }
}
