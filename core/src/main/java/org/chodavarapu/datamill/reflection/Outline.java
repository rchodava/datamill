package org.chodavarapu.datamill.reflection;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Outline<T> {
    <M> String camelCasedName(M member);
    String camelCasedName(Consumer<T> memberInvoker);
    String camelCasedName();
    String camelCasedPluralName();
    <A extends Annotation> A getAnnotation(Class<A> annotationClass);
    boolean hasAnnotation(Class<? extends Annotation> annotationClass);
    T members();
    Collection<Method> methods();
    <M> String name(M member);
    String name(Consumer<T> memberInvoker);
    String name();
    String pluralName();
    Collection<Property> properties();
    <P> Property property(P property);
    Collection<String> propertyNames();
    <M> String snakeCasedName(M member);
    String snakeCasedName(Consumer<T> memberInvoker);
    String snakeCasedName();
    String snakeCasedPluralName();
    Bean<T> wrap(T instance);
}
