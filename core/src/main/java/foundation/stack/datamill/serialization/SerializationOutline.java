package foundation.stack.datamill.serialization;

import foundation.stack.datamill.reflection.Outline;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SerializationOutline<T> extends Outline<T> {
    interface OutlineBoundDeserializationStrategy<T> {
        SerializationOutline<T> outline();

        DeserializationStrategy<T> deserializationStrategy();
    }

    interface OutlineBoundSerializationStrategy<T> {
        SerializationOutline<T> outline();

        SerializationStrategy<T> serializationStrategy();
    }

    <P> OutlineBoundDeserializationStrategy<P> deserialization(Class<P> outlineClass, DeserializationStrategy<P> strategy);

    <P> OutlineBoundSerializationStrategy<P> serialization(Class<P> outlineClass, SerializationStrategy<P> strategy);
}
