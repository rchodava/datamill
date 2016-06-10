package foundation.stack.datamill.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObjectTest {
    private final JsonObject testObject = new JsonObject("{\"name\": \"value\"}");

    private void testThrowsConversionException(JsonObject json, Consumer<JsonObject> conversion) {
        try {
            conversion.accept(json);
            fail("Failed to throw an expected conversion exception!");
        } catch (JsonException e) {
        }
    }

    private void testThrowsConversionException(Consumer<JsonObject> conversion) {
        testThrowsConversionException(testObject, conversion);
    }

    @Test
    public void unsupportedConversions() {
        testThrowsConversionException(j -> j.asBoolean());
        testThrowsConversionException(j -> j.asByte());
        testThrowsConversionException(j -> j.asCharacter());
        testThrowsConversionException(j -> j.asDouble());
        testThrowsConversionException(j -> j.asFloat());
        testThrowsConversionException(j -> j.asInteger());
        testThrowsConversionException(j -> j.asLocalDateTime());
        testThrowsConversionException(j -> j.asLong());
        testThrowsConversionException(j -> j.asShort());

        assertFalse(testObject.isBoolean());
        assertFalse(testObject.isByte());
        assertFalse(testObject.isCharacter());
        assertFalse(testObject.isDouble());
        assertFalse(testObject.isFloat());
        assertFalse(testObject.isInteger());
        assertFalse(testObject.isLong());
        assertFalse(testObject.isNumeric());
        assertFalse(testObject.isShort());
        assertFalse(testObject.isString());
    }

    @Test
    public void stringConversion() throws Exception {
        String json = new JsonObject("{\"name\": \"value\"}").put("name2", 2).asString();

        JsonParser parser = new JsonFactory().createParser(json);
        parser.nextToken();
        assertEquals("name", parser.nextFieldName());
        assertEquals("value", parser.nextTextValue());
        assertEquals("name2", parser.nextFieldName());
        assertEquals(2, parser.nextIntValue(0));
    }

    @Test
    public void propertyConversions() {
        JsonObject json = new JsonObject("{\"character\": \"v\", \"numeric\": 2, \"boolean\": true, \"string\": \"value\", \"bytes\": [1, 2]}");
        assertEquals(true, json.get("boolean").asBoolean());
        assertEquals(2, json.get("numeric").asByte());
        assertEquals('v', json.get("character").asCharacter());
        assertEquals((char) 2, json.get("numeric").asCharacter());
        assertEquals(2d, json.get("numeric").asDouble(), 0.001);
        assertEquals(2f, json.get("numeric").asFloat(), 0.001);
        assertEquals(2, json.get("numeric").asInteger());
        assertEquals(2l, json.get("numeric").asLong());
        assertEquals(2, json.get("numeric").asShort());
        assertEquals("value", json.get("string").asString());
        assertArrayEquals("value".getBytes(), json.get("string").asByteArray());
        assertArrayEquals(new byte[] {(byte) 1, (byte) 2}, json.get("bytes").asByteArray());

        testThrowsConversionException(json, j -> j.get("string").asCharacter());
    }
}
