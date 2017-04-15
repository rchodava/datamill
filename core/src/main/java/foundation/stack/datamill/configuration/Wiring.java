package foundation.stack.datamill.configuration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import foundation.stack.datamill.Pair;
import foundation.stack.datamill.configuration.impl.Classes;
import foundation.stack.datamill.reflection.impl.TypeSwitch;
import foundation.stack.datamill.values.StringValue;
import foundation.stack.datamill.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.functions.Action1;
import rx.functions.Func1;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.*;

/**
 * <p>
 * Wirings form the basis of a lightweight dependency injection (DI) mechanism. Wirings support DI through public
 * constructor injection - they are in fact a version of the factory pattern.
 * </p>
 * <p>
 * For example, consider:
 * <pre>
 * public class UserRepository {
 *     public UserRepository(DatabaseClient databaseClient, OutlineBuilder outlineBuilder) {
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * You can use a Wiring to construct a UserRepository:
 * <pre>
 * UserRepository repository = new Wiring()
 *     .add(new OutlineBuilder(), new DatabaseClient(...))
 *     .construct(UserRepository.class);
 * </pre>
 * </p>
 * <p>
 * This constructs a new UserRepository using the public constructor, injecting the provided instances of DatabaseClient
 * and OutlineBuilder. Note that the wiring does not care about the ordering of the constructor parameters.
 * </p>
 * <p>
 * Factory methods can be registered with the Wiring using {@link #addFactory(Class, Func1)} and the Wiring will use the
 * factory methods whenever it cannot satisfy dependencies with explicitly added and constructed instances. Note that
 * once a factory method is invoked to construct an instance for a type, the built instance will be added to the Wiring.
 * </p>
 * <p>
 * When the constructor's parameter types are concrete classes, Wirings will attempt to recursively construct instances
 * of those concrete types. Most of the time, this is the intended behavior, and it reduces the amount of explicit calls
 * that are required. Note that the Wiring will first attempt to use explicitly added and constructed instances when
 * trying to satisfy dependencies before constructing them automatically.
 * </p>
 * <p>
 * When dealing with a constructor which has multiple parameters of the same type, Wirings support using a name as a
 * qualifier for constructor injection. For example, consider:
 * <pre>
 * public class DatabaseClient {
 *     public DatabaseClient(@Named("url") String url, @Named("username") String username, @Named("password") String password) {
 *     }
 * }
 * </pre>
 * </p>
 * <p>
 * You can use a Wiring to construct a DatabaseClient using the named parameters:
 * <pre>
 * DatabaseClient client = new Wiring()
 *     .addFormatted("url", "jdbc:mysql://{0}:{1}/{2}", "localhost", 3306, "database")
 *     .addNamed("username", "dbuser")
 *     .addNamed("password", "dbpass")
 *     .construct(DatabaseClient.class);
 * </pre>
 * </p>
 * <p>
 * This constructs a new DatabaseClient using the constructor shown, injecting the provided named Strings as parameters.
 * Note that for Strings and primitives, Wirings only support injection using a name.
 * </p>
 * <p>
 * Wirings are very light-weight containers for objects and properties that are meant to be wired together. Each
 * separate Wiring instance is self-contained, and when the {@link #construct(Class)} method is called, only
 * the objects (including named objects) added to the Wiring are considered as candidates when injecting.
 * </p>
 * <p>
 * You can associate a {@link PropertySource} or {@link PropertySourceChain} with a Wiring using
 * {@link #setNamedPropertySource} so that it can resolve any named parameters by looking them up in the
 * {@link PropertySource}. Note that any named values explicitly added to the Wiring using
 * {@link #addNamed(String, Object)} will take precedence over the property source.
 * </p>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Wiring {
    private static final Logger logger = LoggerFactory.getLogger(Wiring.class);

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

    private static boolean canAutoConstruct(Class<?> type) {
        return !type.isInterface() &&
                !Modifier.isAbstract(type.getModifiers()) &&
                type != String.class &&
                !type.isPrimitive() &&
                !Classes.isPrimitiveWrapper(type);
    }

    private final Map<Class<?>, List<Pair<Integer, Func1<Wiring, ?>>>> factories = new HashMap<>();
    private final Multimap<Class<?>, Object> members = HashMultimap.create();
    private final Map<String, Object> named = new HashMap<>();
    private PropertySource propertySource;

    /**
     * Add the specified modules to this wiring.
     *
     * @param modules Modules to add.
     */
    public Wiring include(Module... modules) {
        for (Module module : modules) {
            module.call(this);
        }
        return this;
    }

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
     * matching constructor parameters are found. Adding primitives to a Wiring without a name (i.e., without using
     * {@link #addNamed(String, Object)} is not supported).
     *
     * @param additions Objects to add.
     */
    public Wiring add(Object... additions) {
        if (additions == null) {
            throw new IllegalArgumentException("Cannot add null to a Wiring");
        }

        for (Object addition : additions) {
            if (addition == null) {
                throw new IllegalArgumentException("Cannot add null to a Wiring");
            }

            if (addition instanceof String || addition.getClass().isPrimitive()) {
                throw new IllegalArgumentException("Cannot add Strings and primitives to a Wiring without a name");
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
     * Add a factory method to the Wiring which is invoked to create an instance of the specified class. The factory
     * method is added with a default priority of 0.
     *
     * @param clazz   Class for which we want to add a factory.
     * @param factory Factory method to invoke for the class specified.
     */
    public <T> Wiring addFactory(Class<T> clazz, Func1<Wiring, T> factory) {
        return addFactory(0, clazz, factory);
    }

    /**
     * Add a factory method to the Wiring which is invoked to create an instance of the specified class.
     *
     * @param priority Priority to give this factory - if multiple factory methods exist for a type, the instance
     *                 returned by the method with higher priority will be used.
     * @param clazz    Class for which we want to add a factory.
     * @param factory  Factory method to invoke for the class specified.
     */
    public <T> Wiring addFactory(int priority, Class<T> clazz, Func1<Wiring, T> factory) {
        factories.compute(clazz, (__, existing) -> {
            if (existing == null) {
                existing = new ArrayList<>();
            }

            int insertionPoint = 0;
            for (int i = 0; i < existing.size(); i++) {
                if (priority >= existing.get(i).getFirst()) {
                    insertionPoint = i;
                    break;
                }
            }
            existing.add(insertionPoint, new Pair<>(priority, factory));
            return existing;
        });
        return this;
    }

    /**
     * Add an object to the Wiring under the specified name. These objects are only injected when a constructor
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
            throw new IllegalArgumentException("Wiring already contains an object with name " + name);
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
     * of the objects it knows about for injection into other constructors. If a particular dependency is unsatisfied,
     * if the parameter's type is a concrete class, the Wiring will try to recursively {@link #construct(Class)} an
     * instance of that parameter type.
     *
     * @param clazz Class we want to create an instance of.
     * @param <T>   Type of instance.
     * @return Instance that was constructed.
     * @throws IllegalArgumentException If the class is an interface, abstract class or has no public constructors.
     * @throws IllegalStateException    If all dependencies for constructing an instance cannot be satisfied.
     */
    public <T> T construct(Class<T> clazz) {
        return construct(clazz, new HashSet<>());
    }

    private <T> T construct(Class<T> clazz, Set<Class<?>> autoConstructionCandidates) {
        autoConstructionCandidates.add(clazz);
        Constructor<?>[] constructors = getPublicConstructors(clazz);

        for (Constructor<?> constructor : constructors) {
            T instance = constructWithConstructor(clazz, constructor, autoConstructionCandidates);
            if (instance != null) {
                return instance;
            }
        }

        throw new IllegalStateException("Unable to satisfy all dependencies needed to construct instance of " + clazz.getName());
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
            return constructWithConstructor(clazz, constructor, new HashSet<>());
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalStateException("Unable to find a constructor with specified parameters on " + clazz.getName());
        }
    }

    private <T> T constructWithConstructor(
            Class<T> clazz,
            Constructor<?> constructor,
            Set<Class<?>> autoConstructionCandidates) {
        Parameter[] parameters = constructor.getParameters();
        Object[] values = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            values[i] = getValueForParameter(parameters[i]);
            if (values[i] == null) {
                Class<?> parameterType = parameters[i].getType();
                if (canAutoConstruct(parameterType) && !autoConstructionCandidates.contains(parameterType)) {
                    try {
                        values[i] = construct(parameterType, autoConstructionCandidates);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        logger.error("Could not build class {} as the following type could not be constructed {}", clazz, parameterType, e);
                        return null;
                    }
                } else {
                    StringBuilder parameterNames = new StringBuilder();

                    Named[] annotations = parameters[i].getAnnotationsByType(Named.class);
                    if (annotations != null && annotations.length > 0) {
                        for (Named annotation : annotations) {
                            if (parameterNames.length() > 0) {
                                parameterNames.append(", ");
                            }

                            parameterNames.append(annotation.value());
                        }
                    }

                    if (parameterNames.length() > 0) {
                        logger.error("Could not build class {} as the following named parameter was not found: {}",
                                clazz, parameterNames.toString());
                    } else {
                        logger.error("Could not build class {} as the following type was not found {}",
                                clazz, parameterType);
                    }

                    return null;
                }
            }
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
        if (names != null && names.length > 0) {
            Object namedValue = getValueForNamedParameter(parameter, names);
            if (namedValue != null) {
                return namedValue;
            }
        } else {
            Object value = getValueForParameterByType(parameter);
            if (value != null) {
                return value;
            }
        }


        return null;
    }

    private Object getValueForParameterByType(Parameter parameter) {
        Class<?> type = parameter.getType();
        return get(type);
    }

    /**
     * Get an object of the specified type that was previously added to or constructed by this wiring, or that can be
     * built by a registered factory, or null if no value of the specified type was added or constructed.
     */
    public <T> T get(Class<T> type) {
        if (type.isPrimitive() || type == String.class || Classes.isPrimitiveWrapper(type)) {
            logger.debug("Injection of dependencies that primitives and not named is not supported!");
            return null;
        }

        Object value = getObjectOfType(type);
        if (value != null) {
            return (T) value;
        }

        value = getValueOfType(type);
        if (value != null) {
            return (T) value;
        }

        List<Pair<Integer, Func1<Wiring, ?>>> typeFactories = factories.get(type);
        if (typeFactories != null) {
            for (Pair<Integer, Func1<Wiring, ?>> factory : typeFactories) {
                value = factory.getSecond().call(this);
                if (value != null) {
                    T casted = (T) value;
                    add(casted);
                    return casted;
                }
            }
        }

        return null;
    }

    /**
     * @see #getNamedAs(String, Class)
     */
    public Value getNamed(String name) {
        return getNamedAs(name, Value.class);
    }

    /**
     * Get the object identified by the give name that was added by using {@link Wiring#addNamed(String, Object)},
     * or available from the property source chain set on the wiring.
     */
    public <T> T getNamedAs(String name, Class<T> type) {
        Object value = named.get(name);

        if (value == null && propertySource != null) {
            value = propertySource.get(name).map(string -> new StringValue(string)).orElse(null);
        }

        if (value != null) {
            if (type.isInstance(value)) {
                return (T) value;
            }

            if (type.isPrimitive()) {
                Class<?> wrapper = Classes.primitiveToWrapper(type);
                if (wrapper.isInstance(value)) {
                    return (T) value;
                }
            }

            if (Value.class.isAssignableFrom(value.getClass())) {
                if (type == Value.class) {
                    return (T) value;
                }

                return (T) castValueToTypeIfPossible((Value) value, type);
            }

            if (type == Value.class) {
                return (T) new StringValue(value.toString());
            }
        }

        return null;
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

            if (casted.size() == 0) {
                return null;
            }

            throw new IllegalStateException("Multiple objects in graph match type " + type.getName());
        }

        return null;
    }

    private Object getValueForNamedParameter(Parameter parameter, Named[] names) {
        for (Named name : names) {
            Object value = getValueForNamedParameter(parameter, name);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    private Object getValueForNamedParameter(Parameter parameter, Named name) {
        return getNamedAs(name.value(), parameter.getType());
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
     *     .performIf(..., w -> {
     *         w.construct(...);
     *     })
     *     .orElse(...);
     * </pre>
     */
    public ElseBuilder performIf(boolean condition, Action1<Wiring> consumer) {
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
     * Similar convenience mechanism to {@link #with(Action1)} and {@link #performIf(boolean, Action1)} but the function
     * is invoked if condition is true. If it is not true, an else clause can be chained, as in:
     * <p>
     * <pre>
     * new Wiring().add(...)
     *     .returnIf(..., w -> {
     *         w.construct(...);
     *     })
     *     .orElse(...);
     * </pre>
     */
    public <T> ReturningElseBuilder<T> returnIf(boolean condition, Func1<Wiring, T> function) {
        if (condition) {
            return new ReturningElseBuilder<T>() {
                @Override
                public T orElseReturnNull() {
                    return function.call(Wiring.this);
                }

                @Override
                public T orElse(Func1<Wiring, T> __) {
                    return function.call(Wiring.this);
                }
            };
        } else {
            return new ReturningElseBuilder<T>() {
                @Override
                public T orElseReturnNull() {
                    return null;
                }

                @Override
                public T orElse(Func1<Wiring, T> elseFunction) {
                    return elseFunction.call(Wiring.this);
                }
            };
        }
    }

    public Wiring setNamedPropertySource(PropertySource propertySource) {
        this.propertySource = propertySource;
        return this;
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

    public interface ReturningElseBuilder<T> {
        T orElseReturnNull();

        T orElse(Func1<Wiring, T> function);
    }
}
