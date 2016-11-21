package foundation.stack.datamill.serialization;

/**
 * A {@link DeserializationStrategy} defines a function which is used in deserializing an object. It should take values
 * from the given {@link StructuredInput} and insert them into the object so that the {@link StructuredInput} can
 * be deserialized.
 * <p>
 * For example, deserializing User objects may look like this:
 * <pre>
 * public static final DeserializationStrategy&lt;User&gt; PUBLIC =
 *     (user, source) -> user
 *          .set(m -> m.getId(), source.get(user.member(m -> m.getId())))
 *          .set(m -> m.getName(), source.get(user.member(m -> m.getName())));
 * </pre>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface DeserializationStrategy<T> {
    void deserialize(T target, SerializationOutline<T> targetOutline, StructuredInput source);
}
