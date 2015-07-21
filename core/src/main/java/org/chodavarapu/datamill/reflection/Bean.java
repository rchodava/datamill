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
    private final T instance;
    private Map<String, Property> properties;

    public Bean(T instance) {
        this.instance = instance;
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
            BeanInfo beanInfo = Introspector.getBeanInfo(instance.getClass());
            for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
                properties.put(descriptor.getName(), new Property(descriptor));
            }
        } catch (IntrospectionException e) {
            throw new ReflectionException(e);
        }
    }

    public Optional<Property> property(String name) {
        return Optional.ofNullable(getProperties().get(name));
    }

    public Collection<Property> properties() {
        return getProperties().values();
    }
}
