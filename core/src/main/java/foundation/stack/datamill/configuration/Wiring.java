package foundation.stack.datamill.configuration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import foundation.stack.datamill.configuration.impl.Classes;
import foundation.stack.datamill.reflection.impl.TypeSwitch;
import foundation.stack.datamill.values.Value;
import rx.functions.Action1;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.*;

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
 * separate Wiring instance is self-contained, and when the {@link #construct(Class)} method is called, only
 * the objects (including named objects) added to the Wiring are considered as candidates when injecting.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Wiring {
    private static final TypeSwitch<Value, Void, Object> valueCast = new TypeSwitch<Value, Void, Object>() {
        @Override
        protected Object caseBoolean(Value value, Void __) {
            return value.asBoolean();
        }

        @Override
        protected Object caseByte(Value value, Void __) {
            return value.asByte();
        }

        @Override
        protected Object caseCharacter(Value value, Void __) {
            return value.asCharacter();
        }

        @Override
        protected Object caseShort(Value value, Void __) {
            return value.asShort();
        }

        @Override
        protected Object caseInteger(Value value, Void __) {
            return value.asInteger();
        }

        @Override
        protected Object caseLong(Value value, Void __) {
            return value.asLong();
        }

        @Override
        protected Object caseFloat(Value value, Void __) {
            return value.asFloat();
        }

        @Override
        protected Object caseDouble(Value value, Void __) {
            return value.asDouble();
        }

        @Override
        protected Object caseLocalDateTime(Value value, Void __) {
            return value.asLocalDateTime();
        }

        @Override
        protected Object caseByteArray(Value value, Void __) {
            return value.asByteArray();
        }

        @Override
        protected Object caseString(Value value1, Void value2) {
            return value1.asString();
        }

        @Override
        protected Object defaultCase(Value value, Void __) {
            return value;
        }
    };

    private final Multimap<Class<?>, Object> members = HashMultimap.create();
    private final Map<String, Object> named = new HashMap<>();

    private void add(Class<?> clazz, Object addition) {
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
        if (additions == null) {
            throw new IllegalArgumentException("Cannot add null to graph");
        }

        for (Object addition : additions) {
            if (addition == null) {
                throw new IllegalArgumentException("Cannot add null to graph");
            }

            if (addition instanceof Optional) {
                add(((Optional) addition).orElse(null));
            } else {
                add(addition.getClass(), addition);
            }
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
        if (addition == null) {
            throw new IllegalArgumentException("Cannot add null to graph");
        }

        if (addition instanceof Optional) {
            addition = ((Optional) addition).orElse(null);
            return addNamed(name, addition);
        }

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
        Object[] casted = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i] instanceof Value) {
                casted[i] = ((Value) arguments[i]).asString();
            } else {
                casted[i] = arguments[i];
            }
        }

        addNamed(name, MessageFormat.format(format, casted));
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
     * @param clazz          Class we want to create an instance of.
     * @param <T>            Type of instance.
     * @return Instance that was constructed.
     * @throws IllegalArgumentException If the class is an interface, abstract class or has no public constructors.
     * @throws IllegalStateException    If all dependencies for constructing an instance cannot be satisfied.
     */
    public <T> T construct(Class<T> clazz) {
        Constructor<?>[] constructors = getPublicConstructors(clazz);

        for (Constructor<?> constructor : constructors) {
            T values = constructWithConstructor(clazz, constructor);
            if (values != null) {
                return values;
            }
        }

        throw new IllegalStateException("Unable to satisfy all dependencies needed to construct instance of " + clazz.getName());
    }

    /**
     * Check if the wiring already contains an instance of the provided class. If it exists, return it, otherwise, delegate
     * to {@link #construct(Class)} in order to construct it.
     *
     * @param clazz          Class we want to create an instance of.
     * @param <T>            Type of instance.
     * @return Instance that was constructed.
     * @throws IllegalArgumentException If the class is an interface, abstract class or has no public constructors.
     * @throws IllegalStateException    If all dependencies for constructing an instance cannot be satisfied.
     */
    public <T> T constructIfMissing(Class<T> clazz) {
        T target = get(clazz);
        if (target != null) {
            return target;
        }
        return construct(clazz);
    }

    /**
     * Construct an instance of the specified class using a public constructors that has parameters of the specified
     * types.
     *
     * @see #construct(Class)
     */
    public <T> T constructWith(Class<T> clazz, Class<?>... parameterTypes) {
        try {
            Constructor<T> constructor = clazz.getConstructor(parameterTypes);
            return constructWithConstructor(clazz, constructor);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to find a constructor with specified parameters on " + clazz.getName());
        }
    }

    /**
     * Check if the wiring already contains an instance of the provided class. If it exists, return it, otherwise, delegate
     * to {@link #constructWith(Class, Class[])} in order to construct it.
     *
     * @param clazz          Class we want to create an instance of.
     * @param parameterTypes Parameter types to be matched in constructor to be used.
     * @return Instance that was constructed.
     * @throws IllegalArgumentException If the class is an interface, abstract class or has no public constructors.
     * @throws IllegalStateException    If all dependencies for constructing an instance cannot be satisfied.
     */
    public <T> T constructWithIfMissing(Class<T> clazz, Class<?>... parameterTypes) {
        T target = get(clazz);
        if (target != null) {
            return target;
        }
        return constructWith(clazz, parameterTypes);
    }

    private <T> T constructWithConstructor(Class<T> clazz, Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] values = new Object[parameters.length];

        boolean unsatisfied = false;
        for (int i = 0; i < parameters.length; i++) {
            values[i] = getValueForParameter(parameters[i]);
            if (values[i] == null) {
                unsatisfied = true;
            }
        }

        if (unsatisfied) {
            return null;
        }

        return instantiate(clazz, constructor, values);
    }

    public <T> T instantiate(Class<T> clazz, Constructor<?> constructor, Object[] arguments) {
        try {
            T constructed = (T) constructor.newInstance(arguments);
            add(constructed);
            return constructed;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Unable to construct instance of " + clazz.getName(), e);
        }
    }

    private <T> Constructor<?>[] getPublicConstructors(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors == null || constructors.length == 0) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " has no public constructors!");
        }

        return constructors;
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
        return get(type);
    }

    /**
     * Get an object of the specified type that was previously added to or constructed by this wiring, or null if no
     * value of the specified type was added or constructed.
     */
    public <T> T get(Class<T> type) {
        Object value = getObjectOfType(type);
        if (value != null) {
            return (T) value;
        }

        if (type.isPrimitive()) {
            Class<?> wrapper = Classes.primitiveToWrapper(type);
            value = getObjectOfType(wrapper);
            if (value != null) {
                return (T) value;
            }
        }

        value = getValueOfType(type);
        if (value != null) {
            return (T) value;
        }

        return null;
    }

    /**
     * Get the object added by using {@link Wiring#addNamed(String, Object)}, identified by the provided name.
     */
    public <T> T getNamed(String name) {
        return (T) named.get(name);
    }

    private Object getObjectOfType(Class<?> type) {
        Collection<?> values = members.get(type);
        if (values != null) {
            if (values.size() == 0) {
                return null;
            }

            if (values.size() == 1) {
                return values.iterator().next();
            }

            throw new IllegalStateException("Multiple objects in graph match type " + type.getName());
        }

        return null;
    }

    private Object getValueOfType(Class<?> type) {
        Collection<?> values = members.get(Value.class);
        if (values != null) {
            if (values.size() == 0) {
                return null;
            }

            ArrayList<Object> casted = new ArrayList<>();
            for (Object value : values) {
                Object castedValue = castValueToTypeIfPossible((Value) value, type);
                if (castedValue != null) {
                    casted.add(castedValue);
                }
            }

            if (casted.size() == 1) {
                return casted.iterator().next();
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
        if (value != null) {
            Class<?> type = parameter.getType();
            if (type.isInstance(value)) {
                return value;
            }

            if (type.isPrimitive()) {
                Class<?> wrapper = Classes.primitiveToWrapper(type);
                if (wrapper.isInstance(value)) {
                    return value;
                }
            }

            if (Value.class.isAssignableFrom(value.getClass())) {
                return castValueToTypeIfPossible((Value) value, type);
            }
        }

        return null;
    }


    private Object castValueToTypeIfPossible(Value value, Class<?> type) {
        Object castedValue = valueCast.doSwitch(type, value, null);
        if (castedValue != null && Classes.isAssignable(type, castedValue.getClass())) {
            return castedValue;
        }

        return null;
    }

    /**
     * Similar convenience mechanism to {@link #with(Action1)} but the action is invoked if condition is true. If it is
     * not true, an else clause can be chained, as in:
     * <p>
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
