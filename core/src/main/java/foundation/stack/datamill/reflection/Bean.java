package foundation.stack.datamill.reflection;

import foundation.stack.datamill.values.Value;

import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Bean<T> {
    <R, A1> R invoke(Method method, A1 argument);
    <R, A1, A2> R invoke(Method method, A1 argument1, A2 argument2);
    <R> R invoke(Method method, Object... arguments);
    Bean<T> set(Consumer<T> propertyInvoker, Value value);
    <P> Bean<T> set(Consumer<T> propertyInvoker, P value);
    <P> P get(Consumer<T> propertyInvoker);
    Outline<T> outline();
    T unwrap();
}
