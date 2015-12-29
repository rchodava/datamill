package org.chodavarapu.datamill.reflection.impl;

import com.google.common.base.CaseFormat;
import com.google.common.base.Defaults;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import org.atteo.evo.inflector.English;
import org.chodavarapu.datamill.reflection.*;
import org.chodavarapu.datamill.values.Value;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private static Method OBJECT_GET_CLASS_METHOD;
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
    private Collection<org.chodavarapu.datamill.reflection.Method> methods;
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

    private Collection<org.chodavarapu.datamill.reflection.Method> getMethods() {
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
                methods.add(new org.chodavarapu.datamill.reflection.Method(method));
            }
        } catch (SecurityException e) {
            throw new ReflectionException(e);
        }
    }

    @Override
    public Collection<org.chodavarapu.datamill.reflection.Method> methods() {
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
        public <R, A1> R invoke(org.chodavarapu.datamill.reflection.Method method, A1 argument) {
            return method.invoke(instance, argument);
        }

        @Override
        public <R, A1, A2> R invoke(org.chodavarapu.datamill.reflection.Method method, A1 argument1, A2 argument2) {
            return method.invoke(instance, argument1, argument2);
        }

        @Override
        public <R> R invoke(org.chodavarapu.datamill.reflection.Method method, Object... arguments) {
            return method.invoke(instance, arguments);
        }

        @Override
        public Outline<T> outline() {
            return (Outline<T>) OutlineImpl.this;
        }

        @Override
        public Bean<T> set(Consumer<T> propertyInvoker, Value value) {
            Property descriptor = OutlineImpl.this.property(propertyInvoker);
            Class<?> type = descriptor.type();
            if (type == boolean.class || type == Boolean.class) {
                descriptor.set(instance, value.asBoolean());
            } else if (type == byte.class || type == Byte.class) {
                descriptor.set(instance, value.asByte());
            } else if (type == char.class || type == Character.class) {
                descriptor.set(instance, value.asCharacter());
            } else if (type == short.class || type == Short.class) {
                descriptor.set(instance, value.asShort());
            } else if (type == int.class || type == Integer.class) {
                descriptor.set(instance, value.asInteger());
            } else if (type == long.class || type == Long.class) {
                descriptor.set(instance, value.asLong());
            } else if (type == float.class || type == Float.class) {
                descriptor.set(instance, value.asFloat());
            } else if (type == double.class || type == Double.class) {
                descriptor.set(instance, value.asDouble());
            } else {
                descriptor.set(instance, value.asString());
            }
            return this;
        }

        @Override
        public <P> Bean<T> set(Consumer<T> propertyInvoker, P value) {
            OutlineImpl.this.property(propertyInvoker).set(instance, value);
            return this;
        }

        @Override
        public T unwrap() {
            return instance;
        }
    }

    private class PropertyImpl<T> extends MemberImpl implements Property<T> {
        private final PropertyDescriptor descriptor;

        public PropertyImpl(PropertyDescriptor descriptor) {
            super(descriptor.getName());

            this.descriptor = descriptor;
        }

        public boolean isReadOnly() {
            return descriptor.getWriteMethod() == null;
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
            java.lang.reflect.Method writeMethod = descriptor.getWriteMethod();
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
