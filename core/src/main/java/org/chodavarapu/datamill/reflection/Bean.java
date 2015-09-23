package org.chodavarapu.datamill.reflection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Bean<T> {
    private final Class<T> beanClass;
    private final T instance;
    private Collection<Method> methods;
    private Map<String, Property> properties;

    public Bean(Class<T> beanClass) {
        this.beanClass = beanClass;
        this.instance = null;
    }

    public Bean(T instance) {
        this.beanClass = null;
        this.instance = instance;
    }

    private Collection<Method> getMethods() {
        if (methods == null) {
            introspectMethods();
        }

        return methods;
    }

    private Map<String, Property> getProperties() {
        if (properties == null) {
            introspectProperties();
        }

        return properties;
    }

    private Class<T> getBeanClass() {
        if (beanClass != null) {
            return beanClass;
        } else {
            return (Class<T>) instance.getClass();
        }
    }

    private void introspectMethods() {
        methods = new ArrayList<>();

        try {
            java.lang.reflect.Method[] classMethods = getBeanClass().getMethods();
            for (java.lang.reflect.Method method : classMethods) {
                methods.add(new Method(method));
            }
        } catch (SecurityException e) {
            throw new ReflectionException(e);
        }
    }

    private void introspectProperties() {
        properties = new HashMap<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(getBeanClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                properties.put(descriptor.getName(), new Property(descriptor));
            }
        } catch (IntrospectionException e) {
            throw new ReflectionException(e);
        }
    }

    public Collection<Method> methods() {
        return getMethods();
    }

    public Optional<Property> property(String name) {
        return Optional.ofNullable(getProperties().get(name));
    }

    public Collection<String> propertyNames() {
        return getProperties().keySet();
    }

    public Collection<Property> properties() {
        return getProperties().values();
    }
}
