package foundation.stack.datamill.serialization;

import rx.functions.Func1;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface Serializer<SourceType, SerializedType extends StructuredOutput>
        extends Func1<SourceType, SerializedType> {
}
