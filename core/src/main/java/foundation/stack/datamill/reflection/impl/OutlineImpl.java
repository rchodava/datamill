package foundation.stack.datamill.reflection.impl;

import com.google.common.base.CaseFormat;
import com.google.common.base.Defaults;
import com.sun.beans.TypeResolver;
import foundation.stack.datamill.reflection.*;
import foundation.stack.datamill.values.*;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import org.atteo.evo.inflector.English;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class OutlineImpl<T> implements Outline<T> {
    private final TripleArgumentTypeSwitch<Property, T, Value, Void> propertySetterSwitch =
            new TripleArgumentTypeSwitch<Property, T, Value, Void>() {
                @Override
                protected Void caseBoolean(Property value1, T value2, Value value3) {
                    value1.set(value2, value3.asBoolean());
                    return null;
                }

                @Override
                protected Void caseBooleanWrapper(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? (Boolean) value3.asObject(Boolean.class) : null);
                    return null;
                }

                @Override
                protected Void caseByte(Property value1, T value2, Value value3) {
                    value1.set(value2, value3.asByte());
                    return null;
                }

                @Override
                protected Void caseByteWrapper(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? (Byte) value3.asObject(Byte.class) : null);
                    return null;
                }

                @Override
                protected Void caseCharacter(Property value1, T value2, Value value3) {
                    value1.set(value2, value3.asCharacter());
                    return null;
                }

                @Override
                protected Void caseCharacterWrapper(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? (Character) value3.asObject(Character.class) : null);
                    return null;
                }

                @Override
                protected Void caseShort(Property value1, T value2, Value value3) {
                    value1.set(value2, value3.asShort());
                    return null;
                }

                @Override
                protected Void caseShortWrapper(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? (Short) value3.asObject(Short.class) : null);
                    return null;
                }

                @Override
                protected Void caseInteger(Property value1, T value2, Value value3) {
                    value1.set(value2, value3.asInteger());
                    return null;
                }

                @Override
                protected Void caseIntegerWrapper(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? (Integer) value3.asObject(Integer.class) : null);
                    return null;
                }

                @Override
                protected Void caseLong(Property value1, T value2, Value value3) {
                    value1.set(value2, value3.asLong());
                    return null;
                }

                @Override
                protected Void caseLongWrapper(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? (Long) value3.asObject(Long.class) : null);
                    return null;
                }

                @Override
                protected Void caseFloat(Property value1, T value2, Value value3) {
                    value1.set(value2, value3.asFloat());
                    return null;
                }

                @Override
                protected Void caseFloatWrapper(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? (Float) value3.asObject(Float.class) : null);
                    return null;
                }

                @Override
                protected Void caseDouble(Property value1, T value2, Value value3) {
                    value1.set(value2, value3.asDouble());
                    return null;
                }

                @Override
                protected Void caseDoubleWrapper(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? (Double) value3.asObject(Double.class) : null);
                    return null;
                }

                @Override
                protected Void caseLocalDateTime(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? value3.asLocalDateTime() : null);
                    return null;
                }

                @Override
                protected Void caseByteArray(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? value3.asByteArray() : null);
                    return null;
                }

                @Override
                protected Void defaultCase(Property value1, T value2, Value value3) {
                    value1.set(value2, value3 != null ? value3.asString() : null);
                    return null;
                }
            };

    private static Method OBJECT_GET_CLASS_METHOD;

    private static String capitalize(String string) {
        if (string != null && !string.isEmpty()) {
            return Character.toUpperCase(string.charAt(0)) + string.substring(1);
        }

        return string;
    }

    private static Method findMethod(Class<?> start, String methodName, int numberOfArguments, Class<?> arguments[]) {
        for (Class<?> clazz = start; clazz != null; clazz = clazz.getSuperclass()) {
            Method methods[] = clazz.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];

                if (method == null || !Modifier.isPublic(method.getModifiers())) {
                    continue;
                }

                if (method.getName().equals(methodName)) {
                    Type[] parameters = method.getGenericParameterTypes();
                    if (parameters.length == numberOfArguments) {
                        if (arguments != null) {
                            boolean differentParameterType = false;
                            if (numberOfArguments > 0) {
                                for (int j = 0; j < numberOfArguments; j++) {
                                    if (TypeResolver.erase(TypeResolver.resolveInClass(start, parameters[j])) !=
                                            arguments[j]) {
                                        differentParameterType = true;
                                        continue;
                                    }
                                }

                                if (differentParameterType) {
                                    continue;
                                }
                            }
                        }

                        return method;
                    }
                }
            }
        }

        Class interfaces[] = start.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Method method = findMethod(interfaces[i], methodName, numberOfArguments, null);
            if (method != null) {
                return method;
            }
        }

        return null;
    }

    private static Method getObjectGetClassMethod() {
        if (OBJECT_GET_CLASS_METHOD == null) {
            try {
                OBJECT_GET_CLASS_METHOD = Object.class.getMethod("getClass");
            } catch (Exception e) {
                OBJECT_GET_CLASS_METHOD = null;
            }
        }

        return OBJECT_GET_CLASS_METHOD;
    }

    private final boolean camelCased;
    private final ThreadLocal<String> lastInvokedMethod = new ThreadLocal<>();
    private final T members;
    private Collection<foundation.stack.datamill.reflection.Method> methods;
    private Map<String, Property> properties;

    public OutlineImpl(T members, boolean camelCased) {
        this.members = members;
        ((Proxy) members).setHandler(new OutlineMethodHandler());

        this.camelCased = camelCased;
    }

    @Override
    public String camelCasedName() {
        return typeName();
    }

    @Override
    public String camelCasedPluralName() {
        return English.plural(camelCasedName());
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return getOutlinedClass().getAnnotation(annotationClass);
    }

    private Class<?> getOutlinedClass() {
        return members.getClass().getSuperclass();
    }

    private Map<String, Property> getProperties() {
        if (properties == null) {
            introspectProperties();
        }

        return properties;
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return getOutlinedClass().getAnnotation(annotationClass) != null;
    }

    private void introspectProperties() {
        properties = new HashMap<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(getOutlinedClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                if (!getObjectGetClassMethod().equals(descriptor.getReadMethod())) {
                    properties.put(camelCased ?
                                    descriptor.getName() :
                                    CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, descriptor.getName()),
                            new PropertyImpl<>(descriptor));
                }
            }
        } catch (IntrospectionException e) {
            throw new ReflectionException(e);
        }
    }

    private Collection<foundation.stack.datamill.reflection.Method> getMethods() {
        if (methods == null) {
            introspectMethods();
        }

        return methods;
    }

    private void introspectMethods() {
        methods = new ArrayList<>();

        try {
            Method[] classMethods = getOutlinedClass().getMethods();
            for (Method method : classMethods) {
                methods.add(new foundation.stack.datamill.reflection.Method(method));
            }
        } catch (SecurityException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public Collection<foundation.stack.datamill.reflection.Method> methods() {
        return getMethods();
    }

    private String lastInvokedMemberName() {
        String method = lastInvokedMethod.get();
        if (method != null) {
            if (method.startsWith("get") || method.startsWith("set")) {
                return method.substring(3);
            } else if (method.startsWith("is")) {
                return method.substring(2);
            }
        }

        return method;
    }

    @Override
    public Member member(Consumer<T> memberInvoker) {
        memberInvoker.accept(members);
        return new MemberImpl(lastInvokedMemberName());
    }

    @Override
    public String name() {
        return camelCased ? camelCasedName() : snakeCasedName();
    }

    @Override
    public String pluralName() {
        return English.plural(name());
    }

    @Override
    public Property property(Consumer<T> memberInvoker) {
        return getProperties().get(member(memberInvoker).name());
    }

    @Override
    public Collection<String> propertyNames() {
        return getProperties().keySet();
    }

    @Override
    public Collection<Property> properties() {
        return getProperties().values();
    }

    @Override
    public String snakeCasedName() {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, typeName());
    }

    @Override
    public String snakeCasedPluralName() {
        return English.plural(snakeCasedName());
    }

    private String typeName() {
        Class<?> type = getOutlinedClass();
        if (type != null) {
            return type.getSimpleName();
        }

        return null;
    }

    @Override
    public Bean<T> wrap(T instance) {
        return new BeanImpl(instance);
    }

    private class BeanImpl implements Bean<T> {
        private final T instance;

        public BeanImpl(T instance) {
            this.instance = instance;
        }

        @Override
        public <P> P get(Consumer<T> propertyInvoker) {
            return (P) OutlineImpl.this.property(propertyInvoker).get(instance);
        }

        @Override
        public <R, A1> R invoke(foundation.stack.datamill.reflection.Method method, A1 argument) {
            return method.invoke(instance, argument);
        }

        @Override
        public <R, A1, A2> R invoke(foundation.stack.datamill.reflection.Method method, A1 argument1, A2 argument2) {
            return method.invoke(instance, argument1, argument2);
        }

        @Override
        public <R> R invoke(foundation.stack.datamill.reflection.Method method, Object... arguments) {
            return method.invoke(instance, arguments);
        }

        @Override
        public Member member(Consumer<T> memberInvoker) {
            return outline().member(memberInvoker);
        }

        @Override
        public Outline<T> outline() {
            return (Outline<T>) OutlineImpl.this;
        }

        @Override
        public Bean<T> set(Consumer<T> propertyInvoker, Value value) {
            Property descriptor = OutlineImpl.this.property(propertyInvoker);
            Class<?> type = descriptor.type();
            propertySetterSwitch.doSwitch(type, descriptor, instance, value);
            return this;
        }

        @Override
        public <P> Bean<T> set(Consumer<T> propertyInvoker, P value) {
            OutlineImpl.this.property(propertyInvoker).set(instance, value);
            return this;
        }

        @Override
        public T get() {
            return instance;
        }
    }

    private class PropertyImpl<T> extends MemberImpl implements Property<T> {
        private final PropertyDescriptor descriptor;
        private final Method writeMethod;

        public PropertyImpl(PropertyDescriptor descriptor) {
            super(descriptor.getName());

            this.descriptor = descriptor;
            this.writeMethod = introspectWriteMethod(descriptor);
        }

        private Method introspectWriteMethod(PropertyDescriptor descriptor) {
            Method method = descriptor.getWriteMethod();
            if (method == null) {
                Class<?> cls = descriptor.getReadMethod().getDeclaringClass();

                Class<?> type = descriptor.getPropertyType();

                String writeMethodName = "set" + capitalize(descriptor.getName());

                Class<?>[] args = (type == null) ? null : new Class<?>[]{type};
                method = findMethod(cls, writeMethodName, 1, args);
            }

            return method;
        }

        public boolean isReadOnly() {
            return writeMethod == null;
        }

        public boolean isSimple() {
            Class<?> propertyType = descriptor.getPropertyType();
            return propertyType.isPrimitive() || propertyType.isEnum() || propertyType == String.class;
        }

        @Override
        public Class<?> type() {
            return descriptor.getPropertyType();
        }

        private <T> T performSecureGet(Callable<T> runnable) {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged((PrivilegedAction<T>) () -> {
                    try {
                        return runnable.call();
                    } catch (Exception e) {
                        throw new ReflectionException(e);
                    }
                });
            } else {
                try {
                    return runnable.call();
                } catch (Exception e) {
                    throw new ReflectionException(e);
                }
            }
        }

        private void performSecure(Runnable runnable) {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged((PrivilegedAction<?>) () -> {
                    runnable.run();
                    return null;
                });
            } else {
                runnable.run();
            }
        }

        public <P> P get(T instance) {
            java.lang.reflect.Method readMethod = descriptor.getReadMethod();
            if (readMethod != null) {
                if (!readMethod.isAccessible()) {
                    performSecure(() -> readMethod.setAccessible(true));
                }

                return performSecureGet(() -> {
                    try {
                        return (P) readMethod.invoke(instance);
                    } catch (InvocationTargetException e) {
                        throw new ReflectionException(e);
                    } catch (IllegalAccessException e) {
                        throw new ReflectionException(e);
                    }
                });
            } else {
                throw new ReflectionException("Property does not have a getter!");
            }
        }

        public <P> void set(T instance, P value) {
            if (writeMethod != null) {
                if (!writeMethod.isAccessible()) {
                    performSecure(() -> writeMethod.setAccessible(true));
                }

                performSecure(() -> {
                    try {
                        writeMethod.invoke(instance, value);
                    } catch (InvocationTargetException e) {
                        throw new ReflectionException(e);
                    } catch (IllegalAccessException e) {
                        throw new ReflectionException(e);
                    }
                });
            }
        }
    }

    private class MemberImpl implements Member {
        private String memberName;

        public MemberImpl(String memberName) {
            this.memberName = memberName;
        }

        @Override
        public String camelCasedName() {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, memberName);
        }

        @Override
        public String snakeCasedName() {
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, memberName);
        }

        @Override
        public String name() {
            return camelCased ? camelCasedName() : snakeCasedName();
        }

        @Override
        public Outline<?> outline() {
            return OutlineImpl.this;
        }
    }

    private class OutlineMethodHandler implements MethodHandler {
        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
            OutlineImpl.this.lastInvokedMethod.set(thisMethod.getName());
            return Defaults.defaultValue(thisMethod.getReturnType());
        }
    }
}
