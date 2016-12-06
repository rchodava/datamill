package foundation.stack.datamill.serialization;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface DeserializationStrategy<T> {
    T deserialize(DeepStructuredInput source);
}
