package foundation.stack.datamill.json;

import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.values.SerializationStrategy;
import rx.functions.Func1;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Json {
    static <T> Func1<T, JsonObject> serializer(Outline<T> outline, SerializationStrategy<T> strategy) {
        return o -> {
            JsonObject json = new JsonObject();
            strategy.serialize(json, outline.wrap(o));
            return json;
        };
    }
}
