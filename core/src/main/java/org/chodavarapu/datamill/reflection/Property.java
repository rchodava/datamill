package org.chodavarapu.datamill.reflection;

import com.google.common.base.CaseFormat;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Callable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Property<T> {
    private final boolean camelCased;
    private final PropertyDescriptor descriptor;

    public Property(PropertyDescriptor descriptor, boolean camelCased) {
        this.descriptor = descriptor;
        this.camelCased = camelCased;
    }

    public boolean isReadOnly() {
        return descriptor.getWriteMethod() == null;
    }

    public boolean isSimple() {
        Class<?> propertyType = descriptor.getPropertyType();
        return propertyType.isPrimitive() || propertyType.isEnum() || propertyType == String.class;
    }

    public String getName() {
        return camelCased ?
                descriptor.getName() : CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, descriptor.getName());
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
