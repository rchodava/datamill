package org.chodavarapu.datamill.reflection;

import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Outline<T> {
    <P> String camelCasedName(P property);
    String camelCasedName(Consumer<T> propertyInvoker);
    String camelCasedName();
    <P> String name(P property);
    String name(Consumer<T> propertyInvoker);
    String name();
    <P> String snakeCasedName(P property);
    String snakeCasedName(Consumer<T> propertyInvoker);
    String snakeCasedName();
    T members();
}
