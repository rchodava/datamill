package org.chodavarapu.datamill.json;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObject {
    private final io.vertx.core.json.JsonObject object;

    public JsonObject() {
        object = new io.vertx.core.json.JsonObject();
    }

    public JsonObject(String json) {
        object = new io.vertx.core.json.JsonObject(json);
    }

    public JsonObject(Map<String, Object> values) {
        object = new io.vertx.core.json.JsonObject(values);
    }

    public <T> JsonObject put(String key, T value) {
        object.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
