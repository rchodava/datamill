package foundation.stack.datamill.values;

import foundation.stack.datamill.reflection.Bean;

/**
 * A {@link DeserializationStrategy} defines a function which is used in deserializing an object. It should take values
 * from the given {@link StructuredValue} and insert them into the object so that the {@link StructuredValue} can
 * be deserialized.
 * <p>
 * For example, deserializing User objects may look like this:
 * <pre>
 * public static final DeserializationStrategy&lt;User&gt; PUBLIC =
 *     (user, source) -> user
 *          .set(m -> m.getId(), source.get(user.member(m -> m.getId())))
 *          .set(m -> m.getName(), source.get(user.member(m -> m.getName())));
 * </pre>
 * <p>
 * User objects can then be serialized to JSON by calling:
 * <pre>
 * STANDARD.serialize(new JsonObject(), userOutline, user);
 * </pre>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface DeserializationStrategy<T> {
    T deserialize(Bean<T> target, StructuredValue source);
}
