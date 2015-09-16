package org.chodavarapu.datamill.reflection;

import java.lang.annotation.Annotation;

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

    public boolean hasAnnotation(Class<? extends Annotation> annotationClass) {
        return method.getAnnotation(annotationClass) != null;
    }
}
