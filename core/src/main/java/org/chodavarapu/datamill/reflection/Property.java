package org.chodavarapu.datamill.reflection;

import java.beans.PropertyDescriptor;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Property<T> {
    private final PropertyDescriptor descriptor;

    public Property(PropertyDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public T get() {
        return null;
    }

    public void set(T value) {

    }

    public boolean isReadOnly() {
        return descriptor.getWriteMethod() == null;
    }

    public boolean isSimple() {
        Class<?> propertyType = descriptor.getPropertyType();
        return propertyType.isPrimitive() || propertyType.isEnum() || propertyType == String.class;
    }

    public String getName() {
        return descriptor.getName();
    }
}
