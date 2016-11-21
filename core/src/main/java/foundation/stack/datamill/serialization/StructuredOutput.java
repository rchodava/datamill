package foundation.stack.datamill.serialization;

import foundation.stack.datamill.reflection.Member;

import java.util.Collection;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface StructuredOutput<T extends StructuredOutput> {
    <ValueType> T put(String name, ValueType value);

    T put(String name, Object[] value);

    T put(String name, Map<String, ?> value);

    <ValueType> T put(
            String name,
            Collection<ValueType> values,
            SerializationOutline.OutlineBoundSerializationStrategy<ValueType> serializationStrategy);

    default <ValueType> T put(Member member, ValueType value) {
        return put(member.name(), value);
    }

    default T put(Member member, Object[] value) {
        return put(member.name(), value);
    }

    default <ValueType> T put(
            Member member,
            Collection<ValueType> values,
            SerializationOutline.OutlineBoundSerializationStrategy<ValueType> serializationStrategy) {
        return put(member.name(), values, serializationStrategy);
    }
}
