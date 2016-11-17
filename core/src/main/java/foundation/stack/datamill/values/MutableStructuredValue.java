package foundation.stack.datamill.values;

import foundation.stack.datamill.reflection.Member;

import java.util.Collection;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MutableStructuredValue extends StructuredValue {
    <T> MutableStructuredValue put(String name, T value);
    MutableStructuredValue put(String name, Object[] value);
    MutableStructuredValue put(String name, Map<String, ?> value);

    <T> MutableStructuredValue put(
            String name,
            Collection<T> values,
            SerializationStrategy<T> valueSerializationStrategy);

    default <T> MutableStructuredValue put(Member member, T value) {
        return put(member.name(), value);
    }
    default MutableStructuredValue put(Member member, Object[] value) {
        return put(member.name(), value);
    }
    default <T> MutableStructuredValue put(
            Member member,
            Collection<T> values,
            SerializationStrategy<T> valueSerializationStrategy) {
        return put(member.name(), values, valueSerializationStrategy);
    }
}
