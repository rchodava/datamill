package foundation.stack.datamill.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class WiringTest {
    private static class ConcreteClass {
        public ConcreteClass() {
        }
    }

    private static class ConcreteClass2 {
        private final ConcreteClass concreteClass;

        public ConcreteClass2(ConcreteClass concreteClass) {
            this.concreteClass = concreteClass;
        }
    }

    private static class ConcreteClass3 {
        private final ConcreteClass concreteClass;
        private final ConcreteClass2 concreteClass2;

        public ConcreteClass3(ConcreteClass concreteClass, ConcreteClass2 concreteClass2) {
            this.concreteClass = concreteClass;
            this.concreteClass2 = concreteClass2;
        }
    }

    @Test
    public void autoConstruction() {
        ConcreteClass concreteClass = new ConcreteClass();
        ConcreteClass3 concreteClass3 = new Wiring(FactoryChains.forType(ConcreteClass.class, (w, c) -> concreteClass)
                .thenForAnyConcreteClass())
                .newInstance(ConcreteClass3.class);

        assertEquals(concreteClass, concreteClass3.concreteClass);
        assertEquals(concreteClass, concreteClass3.concreteClass2.concreteClass);
    }

    @Test
    public void namedParameters() {
        assertEquals("value", new Wiring(PropertySources.fromImmediate(s -> s.put("name", "value")))
                .getNamed("name").map(v -> v.asString()).get());
        assertEquals("value", new Wiring(PropertySources.fromImmediate(s -> s.put("name", "value")))
                .getRequiredNamed("name").asString());
        assertEquals(12, (int) new Wiring(PropertySources.fromImmediate(s -> s.put("name", "12")))
                .getNamed("name").map(v -> v.asInteger()).get());

        try {
            assertEquals("value", new Wiring(PropertySources.fromImmediate(s -> s.put("name", "value")))
                    .getRequiredNamed("name2").asString());
            fail("Expected retrieving a non-existent required named property to fail!");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void conveniences() {
        ConcreteClass instance = new ConcreteClass();
        ConcreteClass instance2 = new ConcreteClass();

        assertEquals("value", new Wiring(PropertySources.fromImmediate(s -> s.put("name", "value")))
                .with(w -> w.getNamed("name").map(v -> v.asString()).get()));

        assertEquals("value2", new Wiring(PropertySources.fromImmediate(s -> s.put("name", "value")))
                .with(PropertySources.fromImmediate(s -> s.put("name", "value2")),
                        w -> w.getNamed("name").map(v -> v.asString()).get()));

        // Test that sub-wirings created using 'with' share the same scopes
        Wiring original = new Wiring(FactoryChains.forType(ConcreteClass.class, w -> instance));
        original.singleton(ConcreteClass.class);
        assertEquals(instance, original.with(FactoryChains.forType(ConcreteClass.class, w -> instance2),
                w -> w.singleton(ConcreteClass.class)));
    }

    @Test
    public void scopes() {
        Wiring wiring = new Wiring(FactoryChains.forAnyConcreteClass());
        ConcreteClass concreteClass = wiring.newInstance(ConcreteClass.class);
        assertTrue(concreteClass != wiring.newInstance(ConcreteClass.class));

        wiring = new Wiring(FactoryChains.forAnyConcreteClass());
        concreteClass = wiring.singleton(ConcreteClass.class);
        assertEquals(concreteClass, wiring.singleton(ConcreteClass.class));

        wiring = new Wiring(FactoryChains.forAnyConcreteClass());
        concreteClass = wiring.singleton(ConcreteClass.class, "qualifier");
        assertEquals(concreteClass, wiring.singleton(ConcreteClass.class, "qualifier"));
        assertTrue(concreteClass != wiring.singleton(ConcreteClass.class, "qualifier2"));

        wiring = new Wiring(FactoryChains.forAnyConcreteClass());
        concreteClass = wiring.singleton(ConcreteClass.class, "qualifier1", "qualifier2");
        assertEquals(concreteClass, wiring.singleton(ConcreteClass.class, "qualifier1", "qualifier2"));
        assertTrue(concreteClass != wiring.singleton(ConcreteClass.class, "qualifier2", "qualifier3"));
    }
}
