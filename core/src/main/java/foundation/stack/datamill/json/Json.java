package foundation.stack.datamill.json;

import foundation.stack.datamill.serialization.DeserializationStrategy;
import foundation.stack.datamill.serialization.Deserializer;
import foundation.stack.datamill.serialization.SerializationStrategy;
import foundation.stack.datamill.serialization.Serializer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Json {
    static <T> Deserializer<JsonObject, T> deserializer(DeserializationStrategy<T> strategy) {
        return json -> strategy.deserialize(json);
    }

    static <T> Serializer<T, JsonObject> serializer(SerializationStrategy<T> strategy) {
        return o -> {
            JsonObject json = new JsonObject();
            strategy.serialize(json, o);
            return json;
        };
    }
}
