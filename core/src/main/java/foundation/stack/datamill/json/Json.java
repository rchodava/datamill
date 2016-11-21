package foundation.stack.datamill.json;

import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.serialization.SerializationStrategy;
import foundation.stack.datamill.serialization.Serializer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Json {
    static <T> Serializer<T, JsonObject> serializer(Outline<T> outline, SerializationStrategy<T> strategy) {
        return o -> {
            JsonObject json = new JsonObject();
//            strategy.serialize(json, outline, o);
            return json;
        };
    }
}
