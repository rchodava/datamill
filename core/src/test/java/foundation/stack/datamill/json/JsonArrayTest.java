package foundation.stack.datamill.json;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonArrayTest {
    @Test
    public void asObjectsTest() throws Exception {
        List<JsonObject> objects = new JsonArray("[{\"key\": \"value\"}, {\"key\":\"value\"}]").asJsonObjects();
        assertEquals(2, objects.size());
        assertEquals("value", objects.get(0).get("key").asString());
    }
}
