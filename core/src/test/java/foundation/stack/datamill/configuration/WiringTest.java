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
}
