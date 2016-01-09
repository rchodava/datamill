package org.chodavarapu.datamill.json;

import org.chodavarapu.datamill.reflection.Member;
import org.chodavarapu.datamill.values.ReflectableValue;
import org.chodavarapu.datamill.values.Value;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObject implements ReflectableValue {
    final io.vertx.core.json.JsonObject object;

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

    public Value get(String property) {
        return new JsonProperty(property);
    }

    public Value get(Member member) {
        return get(member.name());
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

    public <T> JsonObject put(Member member, T value) {
        return put(member.name(), value);
    }

    @Override
    public String toString() {
        return asString();
    }

    private class JsonProperty implements Value {
        private String name;

        public JsonProperty(String name) {
            this.name = name;
        }

        @Override
        public boolean asBoolean() {
            return object.getBoolean(name);
        }

        @Override
        public byte asByte() {
            return (byte) (int) object.getInteger(name);
        }

        @Override
        public char asCharacter() {
            try {
                return (char) (int) object.getInteger(name);
            } catch (ClassCastException e) {
                String value = object.getString(name);
                if (value.length() == 1) {
                    return value.charAt(0);
                }

                throw new JsonException("Property cannot be converted to a character!");
            }
        }

        @Override
        public double asDouble() {
            return object.getDouble(name);
        }

        @Override
        public float asFloat() {
            return object.getFloat(name);
        }

        @Override
        public int asInteger() {
            return object.getInteger(name);
        }

        @Override
        public long asLong() {
            return object.getLong(name);
        }

        @Override
        public short asShort() {
            return (short) (int) object.getInteger(name);
        }

        @Override
        public String asString() {
            return object.getString(name);
        }

        @Override
        public <T> T map(Function<Value, T> mapper) {
            return null;
        }
    }
}
