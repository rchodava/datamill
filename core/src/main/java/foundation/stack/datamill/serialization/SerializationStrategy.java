package foundation.stack.datamill.serialization;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface SerializationStrategy<SourceType> {
    StructuredOutput<?> serialize(StructuredOutput<?> target, SourceType source);
}
