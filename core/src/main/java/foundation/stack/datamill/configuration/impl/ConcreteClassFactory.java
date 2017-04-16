package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.configuration.QualifyingFactory;
import foundation.stack.datamill.configuration.Wiring;
import foundation.stack.datamill.configuration.WiringException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ConcreteClassFactory<T, R extends T> implements QualifyingFactory<T, R> {
    private static final ConcreteClassFactory<?, ?> INSTANCE = new ConcreteClassFactory<>();

    private static <T> Constructor<?>[] getPublicConstructors(Class<T> type) {
        Constructor<?>[] constructors = type.getConstructors();
        if (constructors == null || constructors.length == 0) {
            throw new WiringException("Class [" + type.getName() + "] has no public constructors!");
        }

        return constructors;
    }

    public static final <T, R extends T> ConcreteClassFactory<?, ?> instance() {
        return INSTANCE;
    }

    private static boolean canAutoConstruct(Class<?> type) {
        return !type.isInterface() &&
                !Modifier.isAbstract(type.getModifiers()) &&
                type != String.class &&
                !type.isPrimitive() &&
                !Classes.isPrimitiveWrapper(type);
    }

    private static void performSecure(Runnable runnable) {
        if (System.getSecurityManager() != null) {
            AccessController.doPrivileged((PrivilegedAction<?>) () -> {
                runnable.run();
                return null;
            });
        } else {
            runnable.run();
        }
    }

    private static void throwFailedConstructionException(Class<?> type, List<WiringException> componentExceptions) {
        StringBuilder message = new StringBuilder("Could not construct [");
        message.append(type.getName());
        message.append("] using any constructor!");
        throw new WiringException(message.toString(), Collections.unmodifiableList(componentExceptions));
    }

    private static void throwFailedInstantiationException(Constructor<?> constructor, ReflectiveOperationException e) {
        StringBuilder message = new StringBuilder("Failed to instantiate using constructor [");
        message.append(constructor.getName());
        message.append("] - ");
        message.append(e.getMessage());
        throw new WiringException(message.toString());
    }

    private static void throwInvalidParameterValue(Parameter parameter, IllegalArgumentException e) {
        StringBuilder message = new StringBuilder("Value retrieved for parameter [");
        message.append(parameter.getName());
        message.append("] was invalid: ");
        message.append(e.getClass().getName());
        message.append(", ");
        message.append(e.getMessage());

        throw new WiringException(message.toString());
    }

    private static void throwUnsatisfiedParameter(
            Constructor<?> constructor,
            Parameter parameter,
            WiringException component) {
        StringBuilder message = new StringBuilder("Failed to satisfy parameter [");
        message.append(parameter.getName());
        message.append(" (");
        message.append(parameter.getType().getName());
        message.append(")] in [");
        message.append(constructor.getName());
        message.append("]");

        throw new WiringException(message.toString(), component != null ? Collections.singletonList(component) : null);
    }

    private ConcreteClassFactory() {
    }

    private R construct(Wiring wiring, Class<? extends T> type) {
        Constructor<?>[] constructors = getPublicConstructors(type);
        ArrayList<WiringException> componentExceptions = new ArrayList<>();

        for (Constructor<?> constructor : constructors) {
            try {
                R instance = constructWithConstructor(wiring, constructor);
                if (instance != null) {
                    return instance;
                }
            } catch (WiringException e) {
                componentExceptions.add(e);
            }
        }

        throwFailedConstructionException(type, componentExceptions);
        return null;
    }

    private R constructWithConstructor(
            Wiring wiring,
            Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameterType = parameters[i].getType();
            try {
                values[i] = wiring.defaultScoped(parameterType);
                if (values[i] == null) {
                    try {
                        values[i] = NamedParameterValueRetriever.retrieveValueIfNamedParameter(wiring, parameters[i]);
                    } catch (IllegalArgumentException e) {
                        throwInvalidParameterValue(parameters[i], e);
                    }
                }
            } catch (WiringException e) {
                throwUnsatisfiedParameter(constructor, parameters[i], e);
            }

            if (values[i] == null) {
                throwUnsatisfiedParameter(constructor, parameters[i], null);
            }
        }

        return instantiate(constructor, values);
    }

    private R instantiate(Constructor<?> constructor, Object[] arguments) {
        try {
            performSecure(() -> constructor.setAccessible(true));
            return (R) constructor.newInstance(arguments);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throwFailedInstantiationException(constructor, e);
            return null;
        }
    }

    @Override
    public R call(Wiring wiring, Class<? extends T> type, Collection<String> qualifiers) {
        if (canAutoConstruct(type)) {
            return construct(wiring, type);
        }

        return null;
    }
}
