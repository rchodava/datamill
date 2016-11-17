package foundation.stack.datamill.values;

import foundation.stack.datamill.reflection.Bean;

/**
 * A {@link SerializationStrategy} defines a function which is used in serializing an object. It should insert values
 * into the given {@link MutableStructuredValue} taken from the object so that the {@link MutableStructuredValue} can
 * then be serialized.
 * <p>
 * For example, serializing User objects may look like this:
 * <pre>
 * public static final SerializationStrategy&lt;User&gt; PUBLIC =
 *     (target, user) -> target
 *          .put(user.member(m -> m.getId()), user.get().getId())
 *          .put(user.member(m -> m.getName()), user.get().getName());
 * </pre>
 * <p>
 * User objects can then be serialized to JSON by calling:
 * <pre>
 * STANDARD.serialize(new JsonObject(), userOutline.wrap(user));
 * </pre>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface SerializationStrategy<T> {
    MutableStructuredValue serialize(MutableStructuredValue target, Bean<T> source);
}
