package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.Named;
import foundation.stack.datamill.configuration.PropertySources;
import foundation.stack.datamill.configuration.Wiring;
import foundation.stack.datamill.configuration.WiringException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ConcreteClassFactoryTest {
    private static final Logger logger = LoggerFactory.getLogger(ConcreteClassFactoryTest.class);

    @Test
    public void construction() {
        assertTrue(new Wiring().singleton(ConcreteClass.class) instanceof ConcreteClass);

        assertTrue(new Wiring().singleton(ConcreteClass2.class) instanceof ConcreteClass2);
        assertTrue(new Wiring().singleton(ConcreteClass2.class).concreteClass instanceof ConcreteClass);

        assertTrue(new Wiring().singleton(ConcreteClass3.class) instanceof ConcreteClass3);
        assertTrue(new Wiring().singleton(ConcreteClass3.class).concreteClass instanceof ConcreteClass);
        assertTrue(new Wiring().singleton(ConcreteClass3.class).concreteClass2 instanceof ConcreteClass2);

        assertTrue(new Wiring(PropertySources.fromImmediate(s -> s.put("named", "12")))
                .singleton(ConcreteClassWithNamedParameters.class).concreteClass instanceof ConcreteClass);
        assertEquals(12, new Wiring(PropertySources.fromImmediate(s -> s.put("named", "12")))
                .singleton(ConcreteClassWithNamedParameters.class).named);
    }

    @Test
    public void failures() {
        try {
            new Wiring().singleton(FailureNoPublicConstructors.class);
            fail("Should have failed to construct class with no public constructors!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }

        try {
            new Wiring().singleton(FailureDependencyOnClassWithNoPublicConstructors.class);
            fail("Should have failed to construct class which depends on another class with no public constructors!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }

        try {
            new Wiring().singleton(FailureDependenciesOnClassesWithNoPublicConstructors.class);
            fail("Should have failed to construct class which depends on other classes with no public constructors!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }

        try {
            new Wiring().singleton(FailureUnsatisfiedDependencies.class);
            fail("Should have failed to construct class which has unsatisfied dependencies!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }

        try {
            new Wiring().singleton(FailureDependencyOnClassWithUnsatisfiedDependencies.class);
            fail("Should have failed to construct class which depends on another class with unsatisfied dependencies!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }

        try {
            new Wiring().singleton(FailureUnsatisfiedNamedParameters.class);
            fail("Should have failed to construct class which has unsatisfied named parameters!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }

        try {
            new Wiring().singleton(FailureDependencyOnClassWithUnsatisfiedNamedParameters.class);
            fail("Should have failed to construct class which depends on another class with unsatisfied named parameters!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }

        try {
            new Wiring(PropertySources.fromImmediate(s -> s.put("named", "abcd")))
                    .singleton(FailureInvalidNamedParameters.class);
            fail("Should have failed to construct class which has an invalid named parameter value!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }

        try {
            new Wiring().singleton(FailureThrowingConstructor.class);
            fail("Should have failed to construct class with a throwing constructor!");
        } catch (WiringException e) {
            logger.debug("\n" + e.toString());
        }
    }

    private static class ConcreteClass {
        public ConcreteClass() {
        }
    }

    private static class ConcreteClass2 {
        private ConcreteClass concreteClass;

        public ConcreteClass2(ConcreteClass concreteClass) {
            this.concreteClass = concreteClass;
        }
    }

    private static class ConcreteClass3 {
        private ConcreteClass concreteClass;
        private ConcreteClass2 concreteClass2;

        public ConcreteClass3(ConcreteClass concreteClass, ConcreteClass2 concreteClass2) {
            this.concreteClass = concreteClass;
            this.concreteClass2 = concreteClass2;
        }
    }

    private static class ConcreteClassWithNamedParameters {
        private ConcreteClass concreteClass;
        private int named;

        public ConcreteClassWithNamedParameters(ConcreteClass concreteClass, @Named("named") int named) {
            this.concreteClass = concreteClass;
            this.named = named;
        }
    }

    private static class FailureNoPublicConstructors {
        private FailureNoPublicConstructors() {
        }

        protected FailureNoPublicConstructors(int __) {
        }
    }

    private static class FailureDependencyOnClassWithNoPublicConstructors {
        public FailureDependencyOnClassWithNoPublicConstructors(FailureNoPublicConstructors __) {
        }
    }

    private static class FailureDependenciesOnClassesWithNoPublicConstructors {
        public FailureDependenciesOnClassesWithNoPublicConstructors(FailureNoPublicConstructors __) {
        }

        public FailureDependenciesOnClassesWithNoPublicConstructors(FailureDependencyOnClassWithNoPublicConstructors __) {
        }
    }

    private static class FailureUnsatisfiedDependencies {
        public FailureUnsatisfiedDependencies(List<?> __) {
        }

        public FailureUnsatisfiedDependencies(String __, boolean ___) {

        }
    }

    private static class FailureDependencyOnClassWithUnsatisfiedDependencies {
        public FailureDependencyOnClassWithUnsatisfiedDependencies(FailureUnsatisfiedDependencies __) {
        }
    }

    private static class FailureUnsatisfiedNamedParameters {
        public FailureUnsatisfiedNamedParameters(@Named("named1") String __) {

        }

        public FailureUnsatisfiedNamedParameters(@Named("named2") boolean __) {

        }
    }

    private static class FailureDependencyOnClassWithUnsatisfiedNamedParameters {
        public FailureDependencyOnClassWithUnsatisfiedNamedParameters(FailureUnsatisfiedNamedParameters __) {
        }
    }

    private static class FailureInvalidNamedParameters {
        public FailureInvalidNamedParameters(@Named("named") int __) {

        }
    }

    private static class FailureThrowingConstructor {
        public FailureThrowingConstructor() {
            throw new IllegalArgumentException("Error!");
        }
    }
}
