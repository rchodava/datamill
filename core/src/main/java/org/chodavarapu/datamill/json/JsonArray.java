package org.chodavarapu.datamill.json;

import org.chodavarapu.datamill.values.ReflectableValue;
import org.chodavarapu.datamill.values.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonArray implements ReflectableValue {
    final io.vertx.core.json.JsonArray array;

    public JsonArray() {
        array = new io.vertx.core.json.JsonArray();
    }

    public JsonArray(String json) {
        array = new io.vertx.core.json.JsonArray(json);
    }

    JsonArray(io.vertx.core.json.JsonArray array) {
        this.array = array;
    }

    public JsonArray(List<JsonObject> values) {
        ArrayList<io.vertx.core.json.JsonObject> objects = new ArrayList<>();
        for (JsonObject value : values) {
            objects.add(value.object);
        }
        array = new io.vertx.core.json.JsonArray(objects);
    }

    public JsonArray(String[] values) {
        array = new io.vertx.core.json.JsonArray(Arrays.asList(values));
    }

    public JsonArray add(JsonObject value) {
        array.add(value.object);
        return this;
    }

    @Override
    public boolean asBoolean() {
        throw new JsonException("A JSON array cannot be converted to a boolean!");
    }

    @Override
    public byte asByte() {
        throw new JsonException("A JSON array cannot be converted to a byte!");
    }

    @Override
    public byte[] asByteArray() {
        return asString().getBytes();
    }

    @Override
    public char asCharacter() {
        throw new JsonException("A JSON array cannot be converted to a character!");
    }

    @Override
    public float asFloat() {
        throw new JsonException("A JSON array cannot be converted to a float!");
    }

    @Override
    public int asInteger() {
        throw new JsonException("A JSON array cannot be converted to an integer!");
    }

    @Override
    public double asDouble() {
        throw new JsonException("A JSON array cannot be converted to a double!");
    }

    @Override
    public LocalDateTime asLocalDateTime() {
        throw new JsonException("A JSON array cannot be converted to a LocalDateTime!");
    }

    @Override
    public long asLong() {
        throw new JsonException("A JSON array cannot be converted to a long!");
    }

    @Override
    public short asShort() {
        throw new JsonException("A JSON array cannot be converted to a short!");
    }

    @Override
    public String asString() {
        return array.encode();
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

    @Override
    public String toString() {
        return asString();
    }
}
