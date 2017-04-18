package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.values.StringValue;
import foundation.stack.datamill.values.Value;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SimpleValueConverterTest {
    @Test
    public void convert() throws Exception {
        assertEquals(true, SimpleValueConverter.convert(true, boolean.class));
        assertEquals(12, (byte) SimpleValueConverter.convert(12, byte.class));
        assertEquals('c', (char) SimpleValueConverter.convert('c', char.class));
        assertEquals(12, (int) SimpleValueConverter.convert(12, int.class));
        assertEquals(12, (short) SimpleValueConverter.convert(12, short.class));
        assertEquals(12l, (long) SimpleValueConverter.convert(12, long.class));;
        assertEquals(12f, SimpleValueConverter.convert(12f, float.class), 0e-32f);
        assertEquals(12d, SimpleValueConverter.convert(12d, double.class), 0e-32f);

        assertEquals(true, SimpleValueConverter.convert(true, Boolean.class));
        assertEquals(12, (byte) SimpleValueConverter.convert(12, Byte.class));
        assertEquals('c', (char) SimpleValueConverter.convert('c', Character.class));
        assertEquals(12, (int) SimpleValueConverter.convert(12, Integer.class));
        assertEquals(12, (short) SimpleValueConverter.convert(12, Short.class));
        assertEquals(12l, (long) SimpleValueConverter.convert(12, Long.class));;
        assertEquals(12f, SimpleValueConverter.convert(12f, Float.class), 0e-32f);
        assertEquals(12d, SimpleValueConverter.convert(12d, Double.class), 0e-32f);

        assertEquals(true, SimpleValueConverter.convert(new StringValue("true"), boolean.class));
        assertEquals(12, (byte) SimpleValueConverter.convert(new StringValue("12"), byte.class));
        assertEquals('c', (char) SimpleValueConverter.convert(new StringValue("c"), char.class));
        assertEquals(12, (int) SimpleValueConverter.convert(new StringValue("12"), int.class));
        assertEquals(12, (short) SimpleValueConverter.convert(new StringValue("12"), short.class));
        assertEquals(12l, (long) SimpleValueConverter.convert(new StringValue("12"), long.class));;
        assertEquals(12f, SimpleValueConverter.convert(new StringValue("12"), float.class), 0e-32f);
        assertEquals(12d, SimpleValueConverter.convert(new StringValue("12"), double.class), 0e-32f);
        assertEquals("Test", SimpleValueConverter.convert(new StringValue("Test"), String.class));
        assertEquals("Test", SimpleValueConverter.convert("Test", Value.class).asString());

        assertEquals(true, SimpleValueConverter.convert(new StringValue("true"), Boolean.class));
        assertEquals(12, (byte) SimpleValueConverter.convert(new StringValue("12"), Byte.class));
        assertEquals('c', (char) SimpleValueConverter.convert(new StringValue("c"), Character.class));
        assertEquals(12, (int) SimpleValueConverter.convert(new StringValue("12"), Integer.class));
        assertEquals(12, (short) SimpleValueConverter.convert(new StringValue("12"), Short.class));
        assertEquals(12l, (long) SimpleValueConverter.convert(new StringValue("12"), Long.class));;
        assertEquals(12f, SimpleValueConverter.convert(new StringValue("12"), Float.class), 0e-32f);
        assertEquals(12d, SimpleValueConverter.convert(new StringValue("12"), Double.class), 0e-32f);
    }
}
