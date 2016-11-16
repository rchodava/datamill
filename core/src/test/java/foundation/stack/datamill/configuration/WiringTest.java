package foundation.stack.datamill.configuration;

import foundation.stack.datamill.values.StringValue;
import org.junit.Test;
import rx.functions.Func0;
import rx.functions.Func1;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class WiringTest {
    private static class Test1 {
        private final String arg1;
        private final String arg2;

        public Test1(@Named("arg1") String arg1, @Named("arg2") String arg2) {
            this.arg1 = arg1;
            this.arg2 = arg2;
        }
    }

    private static class Test2 {
        private final Func1<String, String> func1String;
        private final Func0<String> func0String;

        public Test2(Func0<String> func0String, Func1<String, String> func1String) {
            this.func0String = func0String;
            this.func1String = func1String;
        }
    }

    private static class Base {
        protected String get() {
            return "base";
        }
    }

    private static class Derived extends Base {
        @Override
        protected String get() {
            return "derived";
        }
    }

    public interface Iface1 {
        String iface1();
    }

    public interface Iface2 {
        String iface2();
    }

    private static class Interfaces extends Base implements Iface1 {
        @Override
        public String iface1() {
            return "iface1";
        }
    }

    private static class DerivedInterfaces extends Interfaces implements Iface2 {
        @Override
        public String iface2() {
            return "iface2";
        }
    }

    private static class Test3 {
        private final Base base;
        private final String string;

        public Test3(Base base, String string) {
            this.base = base;
            this.string = string;
        }
    }

    private static class Test4 {
        private final Iface1 iface1;
        private final Iface2 iface2;

        public Test4(Iface1 iface1, Iface2 iface2) {
            this.iface1 = iface1;
            this.iface2 = iface2;
        }
    }

    private static class Test5 {
        private final boolean booleanProperty;
        private final Boolean booleanWrapperProperty;
        private final byte byteProperty;
        private final Byte byteWrapperProperty;
        private final char characterProperty;
        private final Character characterWrapperProperty;
        private final short shortProperty;
        private final Short shortWrapperProperty;
        private final int integerProperty;
        private final Integer integerWrapperProperty;
        private final long longProperty;
        private final Long longWrapperProperty;
        private final float floatProperty;
        private final Float floatWrapperProperty;
        private final double doubleProperty;
        private final Double doubleWrapperProperty;
        private final LocalDateTime localDateTimeProperty;
        private final byte[] byteArrayProperty;
        private final String stringProperty;

        public Test5(@Named("boolean") boolean booleanProperty, @Named("booleanWrapper") Boolean booleanWrapperProperty,
                     @Named("byte") byte byteProperty, @Named("byteWrapper") Byte byteWrapperProperty,
                     @Named("char") char characterProperty, @Named("charWrapper") Character characterWrapperProperty,
                     @Named("short") short shortProperty, @Named("shortWrapper") Short shortWrapperProperty,
                     @Named("int") int integerProperty, @Named("intWrapper") Integer integerWrapperProperty,
                     @Named("long") long longProperty, @Named("longWrapper") Long longWrapperProperty,
                     @Named("float") float floatProperty, @Named("floatWrapper") Float floatWrapperProperty,
                     @Named("double") double doubleProperty, @Named("doubleWrapper") Double doubleWrapperProperty,
                     @Named("LocalDateTime") LocalDateTime localDateTimeProperty,
                     @Named("byteArray") byte[] byteArrayProperty,
                     @Named("String") String stringProperty) {
            this.booleanProperty = booleanProperty;
            this.booleanWrapperProperty = booleanWrapperProperty;
            this.byteProperty = byteProperty;
            this.byteWrapperProperty = byteWrapperProperty;
            this.characterProperty = characterProperty;
            this.characterWrapperProperty = characterWrapperProperty;
            this.shortProperty = shortProperty;
            this.shortWrapperProperty = shortWrapperProperty;
            this.integerProperty = integerProperty;
            this.integerWrapperProperty = integerWrapperProperty;
            this.longProperty = longProperty;
            this.longWrapperProperty = longWrapperProperty;
            this.floatProperty = floatProperty;
            this.floatWrapperProperty = floatWrapperProperty;
            this.doubleProperty = doubleProperty;
            this.doubleWrapperProperty = doubleWrapperProperty;
            this.localDateTimeProperty = localDateTimeProperty;
            this.byteArrayProperty = byteArrayProperty;
            this.stringProperty = stringProperty;
        }
    }

    private static class Test6 {
        private final boolean booleanProperty;
        private final byte byteProperty;
        private final char characterProperty;
        private final short shortProperty;
        private final int integerProperty;
        private final long longProperty;
        private final float floatProperty;
        private final double doubleProperty;
        private final LocalDateTime localDateTimeProperty;
        private final byte[] byteArrayProperty;
        private final String stringProperty;

        public Test6(boolean booleanProperty,
                     byte byteProperty,
                     char characterProperty,
                     short shortProperty,
                     int integerProperty,
                     long longProperty,
                     float floatProperty,
                     double doubleProperty,
                     LocalDateTime localDateTimeProperty,
                     byte[] byteArrayProperty,
                     String stringProperty) {
            this.booleanProperty = booleanProperty;
            this.byteProperty = byteProperty;
            this.characterProperty = characterProperty;
            this.shortProperty = shortProperty;
            this.integerProperty = integerProperty;
            this.longProperty = longProperty;
            this.floatProperty = floatProperty;
            this.doubleProperty = doubleProperty;
            this.localDateTimeProperty = localDateTimeProperty;
            this.byteArrayProperty = byteArrayProperty;
            this.stringProperty = stringProperty;
        }
    }

    private static class Test7 {
        private final String string;

        public Test7() {
            string = "default";
        }

        public Test7(String string) {
            this.string = string;
        }
    }

    @Test
    public void named() {
        Test1 instance = new Wiring()
                .addNamed("arg1", "value1")
                .addNamed("arg2", Optional.of("value2"))
                .construct(Test1.class);

        assertEquals("value1", instance.arg1);
        assertEquals("value2", instance.arg2);
    }

    @Test
    public void typed() {
        Wiring wiring = new Wiring()
                .add((Func0<String>) () -> "func0String",
                        (Func1<String, String>) s -> "func1String" + s);
        Test2 instance = wiring.construct(Test2.class);

        assertEquals("func0String", instance.func0String.call());
        assertEquals("func1StringS", instance.func1String.call("S"));

        assertEquals("func0String", wiring.get(Func0.class).call());
        assertEquals("func1StringS", wiring.get(Func1.class).call("S"));
    }

    @Test
    public void parents() {
        Test3 instance = new Wiring()
                .add(new Derived(), "testString")
                .construct(Test3.class);

        assertEquals("derived", instance.base.get());
        assertEquals("testString", instance.string);
    }

    @Test
    public void interfaces() {
        Wiring wiring = new Wiring()
                .add(new DerivedInterfaces());

        Test4 instance = wiring.construct(Test4.class);

        assertEquals("iface1", instance.iface1.iface1());
        assertEquals("iface2", instance.iface2.iface2());

        assertEquals("iface1", wiring.get(Iface1.class).iface1());
        assertEquals("iface2", wiring.get(Iface2.class).iface2());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noNullAdditions() {
        new Wiring().add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noNullOptionals() {
        new Wiring().add(Optional.empty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noNullNamedAdditions() {
        new Wiring().addNamed("name", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noNullNamedOptionals() {
        new Wiring().addNamed("name", Optional.empty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noNamedDuplicates() {
        new Wiring().addNamed("test", "value").addNamed("test", "value2");
    }

    @Test
    public void formattedValues() {
        Test1 instance = new Wiring().addNamed("arg1", "value")
                .addFormatted("arg2", "{0}:{1}", "value1", "value2")
                .construct(Test1.class);

        assertEquals("value1:value2", instance.arg2);
    }

    @Test
    public void conveniences() {
        Test1 instance = new Wiring().with(w -> w.addNamed("arg1", "value1").addNamed("arg2", "value2"))
                .construct(Test1.class);

        assertEquals("value1", instance.arg1);
        assertEquals("value2", instance.arg2);

        instance = new Wiring().ifCondition(true, w -> w.addNamed("arg1", "true1").addNamed("arg2", "true2"))
                .orElse(w -> w.addNamed("arg1", "false1").addNamed("arg2", "false2"))
                .construct(Test1.class);

        assertEquals("true1", instance.arg1);
        assertEquals("true2", instance.arg2);

        instance = new Wiring().ifCondition(false, w -> w.addNamed("arg1", "true1").addNamed("arg2", "true2"))
                .orElse(w -> w.addNamed("arg1", "false1").addNamed("arg2", "false2"))
                .construct(Test1.class);

        assertEquals("false1", instance.arg1);
        assertEquals("false2", instance.arg2);

    }

    @Test
    public void namedValuesFromPropertySource() {
        Test5 instance = new Wiring()
                .setNamedPropertySource(Properties.fromSystem().orDefaults(d -> d
                .put("boolean", "true")
                .put("booleanWrapper", "true")
                .put("byte", "1")
                .put("byteWrapper", "1")
                .put("char", "a")
                .put("charWrapper", "a")
                .put("short", "2")
                .put("shortWrapper", "2")
                .put("int", "3")
                .put("intWrapper", "3")
                .put("long", "4")
                .put("longWrapper", "4")
                .put("float", "1.1")
                .put("floatWrapper", "1.1")
                .put("double", "2.2")
                .put("doubleWrapper", "2.2")
                .put("LocalDateTime", "2007-12-03T10:15:30")
                .put("String", "value")
                .put("byteArray", "array")))
                .construct(Test5.class);

        assertEquals(true, instance.booleanProperty);
        assertEquals(true, instance.booleanWrapperProperty);
        assertEquals(1, instance.byteProperty);
        assertEquals(1, (byte) instance.byteWrapperProperty);
        assertEquals('a', instance.characterProperty);
        assertEquals('a', (char) instance.characterWrapperProperty);
        assertEquals(2, instance.shortProperty);
        assertEquals(2, (short) instance.shortWrapperProperty);
        assertEquals(3, instance.integerProperty);
        assertEquals(3, (int) instance.integerWrapperProperty);
        assertEquals(4, instance.longProperty);
        assertEquals(4, (long) instance.longWrapperProperty);
        assertEquals(1.1f, instance.floatProperty, 0.1f);
        assertEquals(1.1f, instance.floatWrapperProperty, 0.1f);
        assertEquals(2.2d, instance.doubleProperty, 0.1d);
        assertEquals(2.2d, instance.doubleWrapperProperty, 0.1d);
        assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), instance.localDateTimeProperty);
        assertEquals("value", instance.stringProperty);
        assertArrayEquals("array".getBytes(), instance.byteArrayProperty);
    }

    @Test
    public void typedNamedValues() {
        Test5 instance = new Wiring()
                .addNamed("boolean", new StringValue("true"))
                .addNamed("booleanWrapper", new StringValue("true"))
                .addNamed("byte", new StringValue("1"))
                .addNamed("byteWrapper", new StringValue("1"))
                .addNamed("char", new StringValue("a"))
                .addNamed("charWrapper", new StringValue("a"))
                .addNamed("short", new StringValue("2"))
                .addNamed("shortWrapper", new StringValue("2"))
                .addNamed("int", new StringValue("3"))
                .addNamed("intWrapper", new StringValue("3"))
                .addNamed("long", new StringValue("4"))
                .addNamed("longWrapper", new StringValue("4"))
                .addNamed("float", new StringValue("1.1"))
                .addNamed("floatWrapper", new StringValue("1.1"))
                .addNamed("double", new StringValue("2.2"))
                .addNamed("doubleWrapper", new StringValue("2.2"))
                .addNamed("LocalDateTime", new StringValue("2007-12-03T10:15:30"))
                .addNamed("String", new StringValue("value"))
                .addNamed("byteArray", new StringValue("array"))
                .construct(Test5.class);

        assertEquals(true, instance.booleanProperty);
        assertEquals(true, instance.booleanWrapperProperty);
        assertEquals(1, instance.byteProperty);
        assertEquals(1, (byte) instance.byteWrapperProperty);
        assertEquals('a', instance.characterProperty);
        assertEquals('a', (char) instance.characterWrapperProperty);
        assertEquals(2, instance.shortProperty);
        assertEquals(2, (short) instance.shortWrapperProperty);
        assertEquals(3, instance.integerProperty);
        assertEquals(3, (int) instance.integerWrapperProperty);
        assertEquals(4, instance.longProperty);
        assertEquals(4, (long) instance.longWrapperProperty);
        assertEquals(1.1f, instance.floatProperty, 0.1f);
        assertEquals(1.1f, instance.floatWrapperProperty, 0.1f);
        assertEquals(2.2d, instance.doubleProperty, 0.1d);
        assertEquals(2.2d, instance.doubleWrapperProperty, 0.1d);
        assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), instance.localDateTimeProperty);
        assertEquals("value", instance.stringProperty);
        assertArrayEquals("array".getBytes(), instance.byteArrayProperty);
    }

    @Test
    public void values() {
        Wiring wiring = new Wiring()
                .add(true)
                .add(Optional.of((byte) 1))
                .add('a')
                .add((short) 2)
                .add((int) 3)
                .add((long) 4)
                .add(1.1f)
                .add(2.2d)
                .add(new StringValue("2007-12-03T10:15:30"))
                .add("value")
                .add("array".getBytes());

        Test6 instance = wiring.construct(Test6.class);

        assertEquals(true, instance.booleanProperty);
        assertEquals(1, instance.byteProperty);
        assertEquals('a', instance.characterProperty);
        assertEquals(2, instance.shortProperty);
        assertEquals(3, instance.integerProperty);
        assertEquals(4, instance.longProperty);
        assertEquals(1.1f, instance.floatProperty, 0.1f);
        assertEquals(2.2d, instance.doubleProperty, 0.1d);
        assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), instance.localDateTimeProperty);
        assertEquals("value", instance.stringProperty);
        assertArrayEquals("array".getBytes(), instance.byteArrayProperty);

        assertEquals(true, wiring.get(boolean.class));
        assertEquals(true, wiring.get(Boolean.class));
        assertEquals(1, (byte) wiring.get(byte.class));
        assertEquals(1, (byte) wiring.get(Byte.class));
        assertEquals('a', (char) wiring.get(char.class));
        assertEquals('a', (char) wiring.get(Character.class));
        assertEquals(2, (short) wiring.get(short.class));
        assertEquals(2, (short) wiring.get(Short.class));
        assertEquals(3, (int) wiring.get(int.class));
        assertEquals(3, (int) wiring.get(Integer.class));
        assertEquals(4, (long) wiring.get(long.class));
        assertEquals(4, (long) wiring.get(Long.class));
        assertEquals(1.1f, wiring.get(float.class), 0.1f);
        assertEquals(1.1f, wiring.get(Float.class), 0.1f);
        assertEquals(2.2d, wiring.get(double.class), 0.1d);
        assertEquals(2.2d, wiring.get(Double.class), 0.1d);
        assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), wiring.get(LocalDateTime.class));
        assertEquals("value", wiring.get(String.class));
        assertArrayEquals("array".getBytes(), wiring.get(byte[].class));
    }

    @Test
    public void constructWith() {
        Test7 instance = new Wiring()
                .add("value")
                .constructWith(Test7.class);
        assertEquals("default", instance.string);

        instance = new Wiring()
                .add("value")
                .constructWith(Test7.class, String.class);
        assertEquals("value", instance.string);
    }
}
