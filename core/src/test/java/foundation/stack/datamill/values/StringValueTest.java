package foundation.stack.datamill.values;

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
        assertFalse((Boolean) value.asObject(boolean.class));
        assertFalse((Boolean) value.asObject(Boolean.class));
        value = new StringValue("false");
        assertFalse(value.asBoolean());
        assertFalse((Boolean) value.asObject(boolean.class));
        assertFalse((Boolean) value.asObject(Boolean.class));
        value = new StringValue("null");
        assertFalse(value.asBoolean());
        assertFalse((Boolean) value.asObject(boolean.class));
        assertFalse((Boolean) value.asObject(Boolean.class));
        value = new StringValue("0");
        assertFalse(value.asBoolean());
        assertFalse((Boolean) value.asObject(boolean.class));
        assertFalse((Boolean) value.asObject(Boolean.class));
        value = new StringValue("undefined");
        assertFalse(value.asBoolean());
        assertFalse((Boolean) value.asObject(boolean.class));
        assertFalse((Boolean) value.asObject(Boolean.class));
        value = new StringValue("NaN");
        assertFalse(value.asBoolean());
        assertFalse((Boolean) value.asObject(boolean.class));
        assertFalse((Boolean) value.asObject(Boolean.class));

        value = new StringValue("true");
        assertTrue(value.asBoolean());
        assertTrue((Boolean) value.asObject(boolean.class));
        assertTrue((Boolean) value.asObject(Boolean.class));
        value = new StringValue("some string");
        assertTrue(value.asBoolean());
        assertTrue((Boolean) value.asObject(boolean.class));
        assertTrue((Boolean) value.asObject(Boolean.class));

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
        assertArrayEquals("test".getBytes(), (byte[]) value.asObject(byte[].class));
    }

    @Test
    public void characterConversion() {
        StringValue value = new StringValue("c");
        assertTrue(value.isCharacter());
        assertEquals('c', value.asCharacter());
        assertEquals('c', value.asObject(char.class));
        assertEquals('c', value.asObject(Character.class));
    }

    @Test
    public void integerConversions() {
        StringValue value = new StringValue("12");
        assertTrue(value.isNumeric());
        assertTrue(value.isByte());
        assertEquals(12, value.asByte());
        assertEquals((byte) 12, value.asObject(byte.class));
        assertEquals((byte) 12, value.asObject(Byte.class));
        assertTrue(value.isShort());
        assertEquals(12, value.asShort());
        assertEquals((short) 12, value.asObject(short.class));
        assertEquals((short) 12, value.asObject(Short.class));
        assertTrue(value.isInteger());
        assertEquals(12, value.asInteger());
        assertEquals(12, value.asObject(int.class));
        assertEquals(12, value.asObject(Integer.class));
        assertTrue(value.isLong());
        assertEquals(12, value.asLong());
        assertEquals((long) 12, value.asObject(long.class));
        assertEquals((long) 12, value.asObject(Long.class));
    }

    @Test
    public void floatConversions() {
        StringValue value = new StringValue("12.1");
        assertTrue(value.isNumeric());
        assertTrue(value.isFloat());
        assertEquals(12.1f, value.asFloat(), 0.001f);
        assertEquals(12.1f, value.asObject(float.class));
        assertEquals(12.1f, value.asObject(Float.class));
        assertTrue(value.isDouble());
        assertEquals(12.1d, value.asDouble(), 0.001d);
        assertEquals(12.1d, value.asObject(double.class));
        assertEquals(12.1d, value.asObject(Double.class));
    }

    @Test
    public void stringConversion() {
        StringValue value = new StringValue("test");
        assertTrue(value.isString());
        assertEquals("test", value.asString());
        assertEquals("test", value.asObject(String.class));
    }

    @Test
    public void timeConversions() {
        LocalDateTime now = LocalDateTime.now();
        String time = now.toString();
        StringValue value = new StringValue(time);
        assertEquals(now, value.asLocalDateTime());
        assertEquals(now, value.asObject(LocalDateTime.class));
    }
}
