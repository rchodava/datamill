package org.chodavarapu.datamill.json;

import org.chodavarapu.datamill.values.ReflectableValue;
import org.chodavarapu.datamill.values.Value;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObject implements ReflectableValue {
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

    @Override
    public boolean asBoolean() {
        throw new JsonException("A JSON object cannot be converted to a boolean!");
    }

    @Override
    public byte asByte() {
        throw new JsonException("A JSON object cannot be converted to a byte!");
    }

    @Override
    public char asCharacter() {
        throw new JsonException("A JSON object cannot be converted to a character!");
    }

    @Override
    public float asFloat() {
        throw new JsonException("A JSON object cannot be converted to a float!");
    }

    @Override
    public int asInteger() {
        throw new JsonException("A JSON object cannot be converted to an integer!");
    }

    @Override
    public double asDouble() {
        throw new JsonException("A JSON object cannot be converted to a double!");
    }

    @Override
    public long asLong() {
        throw new JsonException("A JSON object cannot be converted to a long!");
    }

    @Override
    public short asShort() {
        throw new JsonException("A JSON object cannot be converted to a short!");
    }

    @Override
    public String asString() {
        return object.encode();
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isByte() {
        return false;
    }

    @Override
    public boolean isCharacter() {
        return false;
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public boolean isFloat() {
        return false;
    }

    @Override
    public boolean isInteger() {
        return false;
    }

    @Override
    public boolean isLong() {
        return false;
    }

    @Override
    public boolean isNumeric() {
        return false;
    }

    @Override
    public boolean isShort() {
        return false;
    }

    @Override
    public boolean isString() {
        return false;
    }

    @Override
    public <T> T map(Function<Value, T> mapper) {
        return mapper.apply(this);
    }

    public <T> JsonObject put(String key, T value) {
        object.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return asString();
    }
}
