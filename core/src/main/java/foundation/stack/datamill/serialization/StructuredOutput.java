package foundation.stack.datamill.serialization;

import foundation.stack.datamill.reflection.Member;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface StructuredOutput<T extends StructuredOutput<?>> {
    <ValueType> T put(String name, ValueType value);

    <ValueType> T put(String name, ValueType value, SerializationStrategy<ValueType> strategy);

    <ValueType> T put(String name, Iterable<ValueType> values, SerializationStrategy<ValueType> strategy);

    T put(String name, Object[] value);

    T put(String name, Map<String, ?> value);

    default <ValueType> T put(Member member, ValueType value) {
        return put(member.name(), value);
    }

    default <ValueType> T put(Member member, ValueType value, SerializationStrategy<ValueType> strategy) {
        return put(member.name(), value, strategy);
    }

    default <ValueType> T put(Member member, Iterable<ValueType> values, SerializationStrategy<ValueType> strategy) {
        return put(member.name(), values, strategy);
    }

    default T put(Member member, Object[] value) {
        return put(member.name(), value);
    }
}
