package foundation.stack.datamill.configuration;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class FactoryChainsTest {
    @Test
    public void chains() {
        ConcreteClass instance = new ConcreteClass();
        DerivedClass derived = new DerivedClass();

        assertEquals(instance,
                new Wiring(FactoryChains.forType(ConcreteClass.class, w -> instance).thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class));

        assertEquals(derived,
                new Wiring(FactoryChains.forSuperOf(DerivedClass.class, w -> derived).thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class));

        assertEquals(derived,
                new Wiring(FactoryChains.forSuperOf(DerivedClass.class, w -> derived).thenForAnyConcreteClass())
                        .singleton(Interface.class));

        assertEquals(derived,
                new Wiring(FactoryChains.forSuperOf(DerivedClass.class, w -> derived).thenForAnyConcreteClass())
                        .singleton(DerivedClass.class));

        assertEquals(instance,
                new Wiring(FactoryChains.forAny(w -> instance).thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class));

        assertTrue(new Wiring(FactoryChains.forAnyConcreteClass().thenForSuperOf(DerivedClass.class, w -> derived))
                .singleton(ConcreteClass.class) instanceof ConcreteClass);

        assertTrue(new Wiring(FactoryChains.forAnyConcreteClass().thenForSuperOf(DerivedClass.class, w -> derived))
                .singleton(ConcreteClass.class) != derived);

        assertTrue(new Wiring(FactoryChains.forAnyConcreteClass().thenForType(DerivedClass.class, w -> derived))
                .singleton(DerivedClass.class) != derived);
    }

    @Test
    public void typedFactories() {
        ConcreteClass instance = new ConcreteClass();
        DerivedClass derived = new DerivedClass();

        assertEquals(instance,
                new Wiring(FactoryChains.forType(ConcreteClass.class, (w, c) -> c == ConcreteClass.class ? instance : null)
                        .thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class));

        assertEquals(derived,
                new Wiring(FactoryChains.forSuperOf(DerivedClass.class, (w, c) -> c == (Class) ConcreteClass.class ? derived : null)
                        .thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class));

        assertEquals(instance,
                new Wiring(FactoryChains.forAny((w, c) -> c == ConcreteClass.class ? instance : null)
                        .thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class));
    }

    @Test
    public void qualifiedFactories() {
        ConcreteClass instance = new ConcreteClass();
        DerivedClass derived = new DerivedClass();

        assertTrue(instance !=
                new Wiring(FactoryChains.forType(ConcreteClass.class,
                        (w, c, q) -> q.contains("qualifier") ? instance : null)
                        .thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class));

        assertEquals(instance,
                new Wiring(FactoryChains.forType(ConcreteClass.class,
                        (w, c, q) -> q.contains("qualifier") ? instance : null)
                        .thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class, "qualifier"));

        assertTrue(derived !=
                new Wiring(FactoryChains.forSuperOf(DerivedClass.class,
                        (w, c, q) -> q.contains("qualifier") ? derived : null)
                        .thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class));

        assertEquals(instance,
                new Wiring(FactoryChains.forType(ConcreteClass.class,
                        (w, c, q) -> q.contains("qualifier") ? instance : null)
                        .thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class, "qualifier"));

        assertEquals(instance,
                new Wiring(FactoryChains.forAny((w, c, q) -> q.contains("qualifier") ? instance : null)
                        .thenForAnyConcreteClass())
                        .singleton(ConcreteClass.class, "qualifier"));
    }

    private interface Interface {
    }

    private static class ConcreteClass {
        public ConcreteClass() {
        }
    }

    private static class DerivedClass extends ConcreteClass implements Interface {
        public DerivedClass() {
        }
    }
}
