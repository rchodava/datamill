package org.chodavarapu.datamill.json;

import org.chodavarapu.datamill.reflection.Member;
import org.chodavarapu.datamill.values.ReflectableValue;
import org.chodavarapu.datamill.values.Value;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObject implements ReflectableValue {
    final JSONObject object;

    private JsonObject(JSONObject object) {
        this.object = object;
    }

    public JsonObject() {
        object = new JSONObject();
    }

    public JsonObject(String json) {
        object = new JSONObject(json);
    }

    public JsonObject(Map<String, Object> values) {
        object = new JSONObject(values);
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
    public byte[] asByteArray() {
        return asString().getBytes();
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
    public LocalDateTime asLocalDateTime() {
        throw new JsonException("A JSON object cannot be converted to a LocalDateTime!");
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
        return object.toString();
    }

    public JsonProperty get(String property) {
        return new JsonProperty(property);
    }

    public JsonProperty get(Member member) {
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

    public JsonObject put(String key, JsonArray array) {
        object.put(key, array.array);
        return this;
    }

    public <T> JsonObject put(Member member, T value) {
        return put(member.name(), value);
    }

    @Override
    public String toString() {
        return asString();
    }

    public class JsonProperty implements Value {
        private String name;

        private JsonProperty(String name) {
            this.name = name;
        }

        @Override
        public boolean asBoolean() {
            return object.getBoolean(name);
        }

        @Override
        public byte asByte() {
            return (byte) (int) object.getInt(name);
        }

        @Override
        public byte[] asByteArray() {
            JSONArray array = object.getJSONArray(name);
            if (array != null) {
                byte[] bytes = new byte[array.length()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = (byte) array.getInt(i);
                }

                return bytes;
            }

            return null;
        }

        @Override
        public char asCharacter() {
            try {
                return (char) (int) object.getInt(name);
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
            return object.getBigDecimal(name).floatValue();
        }

        @Override
        public int asInteger() {
            return object.getInt(name);
        }

        public JsonArray asJsonArray() {
            JSONArray array = object.optJSONArray(name);
            if (array != null) {
                return new JsonArray(array);
            }

            return null;
        }

        public JsonObject asJson() {
            JSONObject json = object.optJSONObject(name);
            if (json != null) {
                return new JsonObject(json);
            }

            return null;
        }

        @Override
        public LocalDateTime asLocalDateTime() {
            String value = object.optString(name);
            if (value != null) {
                return LocalDateTime.parse(value);
            }

            return null;
        }

        @Override
        public long asLong() {
            return object.getLong(name);
        }

        @Override
        public short asShort() {
            return (short) (int) object.getInt(name);
        }

        @Override
        public String asString() {
            return object.optString(name);
        }

        @Override
        public <T> T map(Function<Value, T> mapper) {
            return null;
        }
    }
}
