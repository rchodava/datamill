package foundation.stack.datamill.reflection;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Outline<T> {
    <A extends Annotation> A getAnnotation(Class<A> annotationClass);

    boolean hasAnnotation(Class<? extends Annotation> annotationClass);

    String camelCasedName();

    String camelCasedPluralName();

    String name();

    String pluralName();

    String snakeCasedName();

    String snakeCasedPluralName();

    Member member(Consumer<T> memberInvoker);

    Collection<Method> methods();

    Collection<Property> properties();

    Property property(Consumer<T> memberInvoker);

    Collection<String> propertyNames();

    Bean<T> wrap(T instance);
}
