package foundation.stack.datamill.serialization;

import foundation.stack.datamill.values.Value;
import rx.functions.Action1;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface DeepStructuredInput extends StructuredInput {
    <T> T get(String name, DeserializationStrategy<T> strategy);

    DeepStructuredInput forEach(String name, Action1<Value> action);

    <T> DeepStructuredInput forEach(String name, DeserializationStrategy<T> strategy, Action1<T> action);
}
