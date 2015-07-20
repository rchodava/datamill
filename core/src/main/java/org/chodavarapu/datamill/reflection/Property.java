package org.chodavarapu.datamill.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Property<T> {
    private final Method getter;
    private final Method setter;
    private final Field field;

    public Property(Field field) {
        this.getter = null;
        this.setter = null;

        this.field = field;
    }

    public Property(Method getter, Method setter) {
        this.getter = getter;
        this.setter = setter;

        this.field = null;
    }

    public T get() {
        return null;
    }

    public void set(T value) {

    }
}
