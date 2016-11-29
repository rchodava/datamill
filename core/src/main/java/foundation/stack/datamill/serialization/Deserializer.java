package foundation.stack.datamill.serialization;

import rx.functions.Func1;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface Deserializer<SerializedType extends StructuredOutput, TargetType>
        extends Func1<SerializedType, TargetType> {
}
