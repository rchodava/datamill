package org.chodavarapu.datamill.values;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class StringValueTest {
    @Test
    public void booleanConversions() {
        StringValue value = new StringValue("");
        assertFalse(value.asBoolean());
        value = new StringValue("false");
        assertFalse(value.asBoolean());
        value = new StringValue("null");
        assertFalse(value.asBoolean());
        value = new StringValue("0");
        assertFalse(value.asBoolean());
        value = new StringValue("undefined");
        assertFalse(value.asBoolean());
        value = new StringValue("NaN");
        assertFalse(value.asBoolean());

        value = new StringValue("true");
        assertTrue(value.asBoolean());
        value = new StringValue("some string");
        assertTrue(value.asBoolean());

        value = new StringValue("false");
        assertTrue(value.isBoolean());
        value = new StringValue("true");
        assertTrue(value.isBoolean());
        value = new StringValue("0");
        assertTrue(value.isBoolean());
        value = new StringValue("1");
        assertTrue(value.isBoolean());
    }

    @Test
    public void binaryConversion() {
        StringValue value = new StringValue("test");
        assertTrue(value.isString());
        assertArrayEquals("test".getBytes(), value.asByteArray());
    }

    @Test
    public void characterConversion() {
        StringValue value = new StringValue("c");
        assertTrue(value.isCharacter());
        assertEquals('c', value.asCharacter());
    }

    @Test
    public void integerConversions() {
        StringValue value = new StringValue("12");
        assertTrue(value.isNumeric());
        assertTrue(value.isByte());
        assertEquals(12, value.asByte());
        assertTrue(value.isShort());
        assertEquals(12, value.asShort());
        assertTrue(value.isInteger());
        assertEquals(12, value.asInteger());
        assertTrue(value.isLong());
        assertEquals(12, value.asLong());
    }

    @Test
    public void floatConversions() {
        StringValue value = new StringValue("12.1");
        assertTrue(value.isNumeric());
        assertTrue(value.isFloat());
        assertEquals(12.1f, value.asFloat(), 0.001f);
        assertTrue(value.isDouble());
        assertEquals(12.1d, value.asDouble(), 0.001d);
    }

    @Test
    public void stringConversion() {
        StringValue value = new StringValue("test");
        assertTrue(value.isString());
        assertEquals("test", value.asString());
    }

    @Test
    public void timeConversions() {
        LocalDateTime now = LocalDateTime.now();
        String time = now.toString();
        StringValue value = new StringValue(time);
        assertEquals(now, value.asLocalDateTime());
    }
}
