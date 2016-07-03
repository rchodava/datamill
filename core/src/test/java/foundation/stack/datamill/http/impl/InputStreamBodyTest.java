package foundation.stack.datamill.http.impl;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class InputStreamBodyTest {
    @Test
    public void entityStreaming() {
        assertEquals("test", new InputStreamBody(new ByteArrayInputStream("test".getBytes()))
                .asString().toBlocking().last());
        assertEquals("test", new String(new InputStreamBody(new ByteArrayInputStream("test".getBytes()))
                .asBytes().toBlocking().last()));
        assertEquals("test", new String(new InputStreamBody(new ByteArrayInputStream("test".getBytes()))
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
        Assert.assertEquals("value", new InputStreamBody(new ByteArrayInputStream("{\"name\":\"value\"}".getBytes()))
                .asJson().toBlocking().last().get("name").asString());
        assertEquals("value", new InputStreamBody(new ByteArrayInputStream("[{\"name\":\"value\"}]".getBytes()))
                .asJsonArray().toBlocking().last().get("name").asString());
    }
}
