package foundation.stack.datamill.configuration;

import foundation.stack.datamill.values.StringValue;
import org.junit.Test;
import rx.functions.Func0;
import rx.functions.Func1;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        private final Func0<String> stringSupplier;

        public Test3(Base base, Func0<String> stringSupplier) {
            this.base = base;
            this.stringSupplier = stringSupplier;
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
        private final LocalDateTime localDateTimeProperty;
        private final byte[] byteArrayProperty;

        public Test6(LocalDateTime localDateTimeProperty,
                     byte[] byteArrayProperty) {
            this.localDateTimeProperty = localDateTimeProperty;
            this.byteArrayProperty = byteArrayProperty;
        }
    }

    private static class Test7 {
        private final Func0<String> stringSupplier;

        public Test7() {
            stringSupplier = () -> "default";
        }

        public Test7(Func0<String> stringSupplier) {
            this.stringSupplier = stringSupplier;
        }
    }

    private static class Test8 {
        private final Test7 test7;

        public Test8(Test7 test7) {
            this.test7 = test7;
        }
    }

    private static class Test9 {
        private final Test7 test7;
        private final Test8 test8;

        public Test9(Test7 test7, Test8 test8) {
            this.test7 = test7;
            this.test8 = test8;
        }
    }

    @Test
    public void autoConstruction() {
        Test7 test7 = new Test7();
        Test9 test9 = new Wiring().add(test7).construct(Test9.class);

        assertEquals(test7, test9.test7);
        assertEquals(test7, test9.test8.test7);
    }

    @Test
    public void builders() {
        Test9 test9 = new Wiring().addFactory(Test7.class, w -> new Test7(() -> "custom")).construct(Test9.class);

        assertEquals("custom", test9.test7.stringSupplier.call());
        assertEquals("custom", test9.test8.test7.stringSupplier.call());
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
                .add(new Derived(), (Func0) () -> "testString")
                .construct(Test3.class);

        assertEquals("derived", instance.base.get());
        assertEquals("testString", instance.stringSupplier.call());
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

        instance = new Wiring().performIf(true, w -> w.addNamed("arg1", "true1").addNamed("arg2", "true2"))
                .orElse(w -> w.addNamed("arg1", "false1").addNamed("arg2", "false2"))
                .construct(Test1.class);

        assertEquals("true1", instance.arg1);
        assertEquals("true2", instance.arg2);

        instance = new Wiring().performIf(false, w -> w.addNamed("arg1", "true1").addNamed("arg2", "true2"))
                .orElse(w -> w.addNamed("arg1", "false1").addNamed("arg2", "false2"))
                .construct(Test1.class);

        assertEquals("false1", instance.arg1);
        assertEquals("false2", instance.arg2);

    }

    @Test
    public void namedValuesFromPropertySource() {
        Wiring wiring = new Wiring()
                .setNamedPropertySource(PropertySourceChain.ofSystem().orImmediate(d -> d
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
                        .put("byteArray", "array")));
        Test5 instance = wiring.construct(Test5.class);

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

        assertEquals(true, wiring.getNamed("boolean").asBoolean());
        assertEquals(true, wiring.getNamed("booleanWrapper").asBoolean());
        assertEquals(1, wiring.getNamed("byte").asByte());
        assertEquals(1, wiring.getNamed("byteWrapper").asByte());
        assertEquals(1.1f, wiring.getNamedAs("floatWrapper", Float.class), 0.001f);
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
                .add(new StringValue("2007-12-03T10:15:30"))
                .add("array".getBytes());

        Test6 instance = wiring.construct(Test6.class);

        assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), instance.localDateTimeProperty);
        assertArrayEquals("array".getBytes(), instance.byteArrayProperty);

        assertNull(wiring.get(boolean.class));
        assertNull(wiring.get(Boolean.class));
        assertNull(wiring.get(byte.class));
        assertNull(wiring.get(Byte.class));
        assertNull(wiring.get(char.class));
        assertNull(wiring.get(Character.class));
        assertNull(wiring.get(short.class));
        assertNull(wiring.get(Short.class));
        assertNull(wiring.get(int.class));
        assertNull(wiring.get(Integer.class));
        assertNull(wiring.get(long.class));
        assertNull(wiring.get(Long.class));
        assertNull(wiring.get(float.class));
        assertNull(wiring.get(Float.class));
        assertNull(wiring.get(double.class));
        assertNull(wiring.get(Double.class));
        assertNull(wiring.get(String.class));
        assertEquals(LocalDateTime.parse("2007-12-03T10:15:30"), wiring.get(LocalDateTime.class));
        assertArrayEquals("array".getBytes(), wiring.get(byte[].class));
    }

    @Test
    public void constructWith() {
        Test7 instance = new Wiring()
                .add((Func0) () -> "value")
                .constructWith(Test7.class);
        assertEquals("default", instance.stringSupplier.call());

        instance = new Wiring()
                .add((Func0) () -> "value")
                .constructWith(Test7.class, Func0.class);
        assertEquals("value", instance.stringSupplier.call());
    }
}
