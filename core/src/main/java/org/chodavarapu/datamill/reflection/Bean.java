package org.chodavarapu.datamill.reflection;

import org.chodavarapu.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Bean<T> {
    <R, A1> R invoke(Method method, A1 argument);
    <R, A1, A2> R invoke(Method method, A1 argument1, A2 argument2);
    <R> R invoke(Method method, Object... arguments);
    <P> Bean<T> set(P property, Value value);
    <P> Bean<T> set(P property, P value);
    <P> P get(P property);
    Outline<T> outline();
    T unwrap();
}
