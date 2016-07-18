package foundation.stack.datamill.configuration;

import foundation.stack.datamill.values.StringValue;
import org.junit.Test;
import rx.functions.Func0;
import rx.functions.Func1;

import java.time.LocalDateTime;

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

    @Test
    public void named() {
        Test1 instance = new Wiring()
                .addNamed("arg1", "value1")
                .addNamed("arg2", "value2")
                .construct(Test1.class);

        assertEquals("value1", instance.arg1);
        assertEquals("value2", instance.arg2);
    }

    @Test
    public void typed() {
        Test2 instance = new Wiring()
                .add((Func0<String>) () -> "func0String",
                        (Func1<String, String>) s -> "func1String" + s)
                .construct(Test2.class);

        assertEquals("func0String", instance.func0String.call());
        assertEquals("func1StringS", instance.func1String.call("S"));
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
        Test4 instance = new Wiring()
                .add(new DerivedInterfaces())
                .construct(Test4.class);

        assertEquals("iface1", instance.iface1.iface1());
        assertEquals("iface2", instance.iface2.iface2());
    }

    @Test
    public void values() {
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
}
