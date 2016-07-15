package foundation.stack.datamill.configuration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import rx.functions.Action1;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Wirings are graphs of objects that are meant to serve as a sort of extremely light-weight dependency injection
 * container.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Wiring {
    private final Multimap<Class<?>, Object> members = HashMultimap.create();
    private final Map<String, Object> named = new HashMap<>();

    private void add(Class<?> clazz, Object addition) {
        if (addition == null) {
            throw new IllegalArgumentException("Cannot add null to graph");
        }

        members.put(clazz, addition);

        registerUnderParentClass(clazz, addition);
        registerUnderInterfaces(clazz, addition);
    }

    private void registerUnderInterfaces(Class<?> clazz, Object addition) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null) {
            for (Class<?> interfaceClass : interfaces) {
                add(interfaceClass, addition);
            }
        }
    }

    private void registerUnderParentClass(Class<?> clazz, Object addition) {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            add(superClass, addition);
        }
    }

    public Wiring add(Object... additions) {
        for (Object addition : additions) {
            add(addition.getClass(), addition);
        }

        return this;
    }

    public Wiring addNamed(String name, Object addition) {
        if (named.containsKey(name)) {
            throw new IllegalArgumentException("Set already contains an object with name " + name);
        }

        named.put(name, addition);
        return this;
    }

    public Wiring addFormatted(String name, String format, Object... arguments) {
        addNamed(name, MessageFormat.format(format, arguments));
        return this;
    }

    public <T> T construct(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors == null || constructors.length == 0) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " has no public constructors!");
        }

        for (Constructor<?> constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();
            Object[] values = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                values[i] = getValueForParameter(parameters[i]);
            }

            try {
                T constructed = (T) constructor.newInstance(values);
                add(constructed);
                return constructed;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException("Unable to construct instance of " + clazz.getName(), e);
            }
        }

        throw new IllegalArgumentException("Unable to satisfy all dependencies needed to construct instance of " + clazz.getName());
    }

    private Object getValueForParameter(Parameter parameter) {
        Named[] names = parameter.getAnnotationsByType(Named.class);
        Object namedValue = getValueForNamedParameter(parameter, names);
        if (namedValue != null) {
            return namedValue;
        }

        Object value = getValueForParameterByType(parameter);
        if (value != null) {
            return value;
        }

        return null;
    }

    private Object getValueForParameterByType(Parameter parameter) {
        Class<?> type = parameter.getType();
        Collection<?> values = members.get(type);
        if (values != null) {
            if (values.size() == 1) {
                return values.iterator().next();
            }

            throw new IllegalStateException("Multiple objects in graph match parameter of type " + type.getName());
        }

        return null;
    }

    private Object getValueForNamedParameter(Parameter parameter, Named[] names) {
        if (names != null && names.length > 0) {
            for (Named name : names) {
                Object value = getValueForNamedParameter(parameter, name);
                if (value != null) {
                    return value;
                }
            }
        }

        return null;
    }

    private Object getValueForNamedParameter(Parameter parameter, Named name) {
        Object value = named.get(name.value());
        if (value != null && parameter.getType().isInstance(value)) {
            return value;
        }

        return null;
    }

    public ElseBuilder ifCondition(boolean condition, Action1<Wiring> consumer) {
        if (condition) {
            consumer.call(this);
        }

        return new ElseBuilder() {
            @Override
            public Wiring orElseDoNothing() {
                return Wiring.this;
            }

            @Override
            public Wiring orElse(Action1<Wiring> consumer) {
                if (!condition) {
                    consumer.call(Wiring.this);
                }

                return Wiring.this;
            }
        };
    }

    public Wiring with(Action1<Wiring> consumer) {
        consumer.call(this);
        return this;
    }

    public interface ElseBuilder {
        Wiring orElseDoNothing();
        Wiring orElse(Action1<Wiring> consumer);
    }
}
