package foundation.stack.datamill.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Method {
    private final java.lang.reflect.Method method;

    public Method(java.lang.reflect.Method method) {
        this.method = method;
    }

    public String getName() {
        return method.getName();
    }

    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return method.getAnnotation(annotationClass) != null;
    }

    public <T, R> R invoke(T instance, Object... arguments) {
        try {
            method.setAccessible(true);
            return (R) method.invoke(instance, arguments);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }
}
