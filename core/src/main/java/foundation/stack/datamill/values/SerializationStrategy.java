package foundation.stack.datamill.values;

import foundation.stack.datamill.reflection.Outline;

/**
 * A {@link SerializationStrategy} defines a function which is used in serializing an object. It should insert values
 * into the given {@link MutableStructuredValue} taken from the object so that the {@link MutableStructuredValue} can
 * then be serialized.
 * <p>
 * For example, serializing User objects may look like this:
 * <pre>
 * public static final SerializationStrategy&lt;User&gt; PUBLIC =
 *     (target, outline, user) -> target
 *          .put(outline.member(m -> m.getId()), user.getId())
 *          .put(outline.member(m -> m.getName()), user.getName());
 * </pre>
 *
 * User objects can then be serialized to JSON by calling:
 * <pre>
 * STANDARD.serialize(new JsonObject(), userOutline, user);
 * </pre>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface SerializationStrategy<T> {
    MutableStructuredValue serialize(MutableStructuredValue target, Outline<T> sourceOutline, T source);
}
