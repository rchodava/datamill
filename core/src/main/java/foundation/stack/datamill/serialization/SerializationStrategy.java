package foundation.stack.datamill.serialization;

/**
 * A {@link SerializationStrategy} defines a function which is used in serializing an object. It should insert values
 * into the given {@link StructuredOutput} taken from the object so that the {@link StructuredOutput} can
 * then be serialized. It is recommended that {@link SerializationStrategy}s are built using a
 * {@link SerializationStrategyBuilder}.
 * <p>
 * For example, serializing User objects may look like this:
 * <pre>
 * public static final SerializationStrategy&lt;User&gt; PUBLIC =
 *     new SerializationStrategyBuilder()
 *          .withOutline(User.class, (target, source, outline) -> target
 *              .put(outline.member(m -> m.getId()), user.getId())
 *              .put(outline.member(m -> m.getName()), user.getName());
 * </pre>
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface SerializationStrategy<SourceType> {
    StructuredOutput<?> serialize(StructuredOutput<?> target, SourceType source);
}
