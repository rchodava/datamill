package foundation.stack.datamill.configuration;

import org.junit.Test;
import rx.functions.Func0;
import rx.functions.Func1;

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
}
