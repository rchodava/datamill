package org.chodavarapu.datamill.reflection.impl;

import com.google.common.base.CaseFormat;
import com.google.common.base.Defaults;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import org.chodavarapu.datamill.reflection.Bean;
import org.chodavarapu.datamill.reflection.Outline;
import org.chodavarapu.datamill.reflection.Property;
import org.chodavarapu.datamill.reflection.ReflectionException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class OutlineImpl<T> implements Outline<T> {
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
    public String camelCasedName(Consumer<T> memberInvoker) {
        memberInvoker.accept(members());
        return camelCasedName((Boolean) null);
    }

    @Override
    public <P> String camelCasedName(P member) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, lastInvokedMemberName());
    }

    private Class<?> getOutlinedClass() {
        return members().getClass().getSuperclass();
    }

    private Map<String, Property> getProperties() {
        if (properties == null) {
            introspectProperties();
        }

        return properties;
    }

    private void introspectProperties() {
        properties = new HashMap<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(getOutlinedClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                properties.put(descriptor.getName(), new Property(descriptor));
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
    public T members() {
        return members;
    }

    @Override
    public String name() {
        return camelCased ? camelCasedName() : snakeCasedName();
    }

    @Override
    public String name(Consumer<T> memberInvoker) {
        return camelCased ? camelCasedName(memberInvoker) : snakeCasedName(memberInvoker);
    }

    @Override
    public <P> String name(P member) {
        return camelCased ? camelCasedName(member) : snakeCasedName(member);
    }

    @Override
    public <P> Property property(P property) {
        return getProperties().get(camelCasedName(property));
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
    public String snakeCasedName(Consumer<T> memberInvoker) {
        memberInvoker.accept(members());
        return snakeCasedName((Boolean) null);
    }

    @Override
    public <P> String snakeCasedName(P member) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, lastInvokedMemberName());
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
        return new BeanImpl<>(instance);
    }

    private class BeanImpl<T> implements Bean<T> {
        private final T instance;

        public BeanImpl(T instance) {
            this.instance = instance;
        }

        @Override
        public <P> Bean<T> set(P property, P value) {
            OutlineImpl.this.property(property).set(instance, value);
            return this;
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
