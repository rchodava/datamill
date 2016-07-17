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
 * Wirings form the basis of a lightweight dependency injection (DI) mechanism. Wirings support DI through public
 * constructor injection.
 * <p/>
 * For example, consider:
 * <pre>
 * public class UserRepository {
 *     public UserRepository(DatabaseClient databaseClient, OutlineBuilder outlineBuilder) {
 *     }
 * }
 * </pre>
 * <p/>
 * You can use a Wiring to construct a UserRepository:
 * <pre>
 * UserRepository repository = new Wiring()
 *     .add(new OutlineBuilder(), new DatabaseClient(...))
 *     .construct(UserRepository.class);
 * </pre>
 * <p/>
 * This constructs a new UserRepository using the public constructor, injecting the provided instances of DatabaseClient
 * and OutlineBuilder. Note that the wiring does not care about the ordering of the constructor parameters.
 * <p/>
 * When dealing with a constructor which has multiple parameters of the same type, Wirings support using a name as a
 * qualifier for constructor injection. For example, consider:
 * <pre>
 * public class DatabaseClient {
 *     public DatabaseClient(@Named("url") String url, @Named("username") String username, @Named("password") String password) {
 *     }
 * }
 * </pre>
 * <p/>
 * You can use a Wiring to construct a DatabaseClient using the named parameters:
 * <pre>
 * DatabaseClient client = new Wiring()
 *     .addFormatted("url", "jdbc:mysql://{0}:{1}/{2}", "localhost", 3306, "database")
 *     .addNamed("username", "dbuser")
 *     .addNamed("password", "dbpass")
 *     .construct(DatabaseClient.class);
 * </pre>
 * <p/>
 * This constructs a new DatabaseClient using the constructor shown, injecting the provided named Strings as parameters.
 * <p/>
 * Wirings are very light-weight containers for objects and properties that are meant to be wired together. Each
 * separate Wiring instance is self-contained, and when the {@link #construct(Class)} method is called, only the objects
 * (including named objects) added to the Wiring are considered as candidates when injecting.
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

    /**
     * Add one or more objects to the Wiring. These objects are then available for constructor injection when any
     * matching constructor parameters are found.
     *
     * @param additions Objects to add.
     */
    public Wiring add(Object... additions) {
        for (Object addition : additions) {
            add(addition.getClass(), addition);
        }

        return this;
    }

    /**
     * Add an object to the Wiring under the specified name. These objects are only injected when a constuctor
     * parameter has a {@link Named} annotation with the specified name.
     *
     * @param name     Name for the object.
     * @param addition Object to add.
     */
    public Wiring addNamed(String name, Object addition) {
        if (named.containsKey(name)) {
            throw new IllegalArgumentException("Set already contains an object with name " + name);
        }

        named.put(name, addition);
        return this;
    }

    /**
     * Add a new formatted string to the Wiring under the specified name. These strings are only injected when a constuctor
     * parameter has a {@link Named} annotation with the specified name.
     *
     * @param name      Name for the string.
     * @param format    Format template to use for the string.
     * @param arguments Arguments to be used with the template to construct a formatted string.
     */
    public Wiring addFormatted(String name, String format, Object... arguments) {
        addNamed(name, MessageFormat.format(format, arguments));
        return this;
    }

    /**
     * Construct an instance of the specified class using one of it's public constructors. This method will use the first
     * constructor for which it can provide all parameters. The Wiring will use all the objects that it currently knows
     * about (i.e., all objects that have been added or constructed by this Wiring at the time the construct method is
     * called) to perform the injection. After the instance is constructed, the instance is added to the Wiring as one
     * of the objects it knows about for injection into other constructors. Note that unlike other dependency injection
     * frameworks, the order of construct calls is important.
     *
     * @param clazz Class we want to create an instance of.
     * @param <T>   Type of instance.
     * @return Instance that was constructed.
     * @throws IllegalArgumentException If the class is an interface, abstract class or has no public constructors.
     * @throws IllegalStateException    If all dependencies for constructing an instance cannot be satisfied.
     */
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

        throw new IllegalStateException("Unable to satisfy all dependencies needed to construct instance of " + clazz.getName());
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
        Object values1 = getObjectOfType(type);
        if (values1 != null) return values1;

        return null;
    }

    private Object getObjectOfType(Class<?> type) {
        Collection<?> values = members.get(type);
        if (values != null) {
            if (values.size() == 1) {
                return values.iterator().next();
            }

            throw new IllegalStateException("Multiple objects in graph match type " + type.getName());
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

    /**
     * Similar convenience mechanism to {@link #with(Action1)} but the action is invoked if condition is true. If it is
     * not true, an else clause can be chained, as in:
     *
     * <pre>
     * new Wiring().add(...)
     *     .ifCondition(..., w -> {
     *         w.construct(...);
     *     })
     *     .orElse(...);
     * </pre>
     */
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

    /**
     * A convenience mechanism to quickly construct multiple objects - for example:
     * <pre>
     * new Wiring().add(databaseClient, outlineBuilder).with(w -> {
     *     w.construct(UserRepository.class);
     *     w.construct(WidgetRepository.class);
     *
     *     w.construct(UserController.class);
     *     w.construct(WidgetController.class);
     * });
     * </pre>
     */
    public Wiring with(Action1<Wiring> consumer) {
        consumer.call(this);
        return this;
    }

    public interface ElseBuilder {
        Wiring orElseDoNothing();

        Wiring orElse(Action1<Wiring> consumer);
    }
}
