package org.chodavarapu.datamill.json;

import org.chodavarapu.datamill.reflection.Member;
import org.chodavarapu.datamill.values.ReflectableValue;
import org.chodavarapu.datamill.values.Value;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObject implements Json, ReflectableValue {
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
    public Object asObject(Class<?> type) {
        if (type == String.class) {
            return asString();
        }

        return this;
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

    public JsonObject put(String key, JsonObject object) {
        this.object.put(key, object.object);
        return this;
    }

    public JsonObject put(String key, JsonArray array) {
        object.put(key, array.array);
        return this;
    }

    public <T> JsonObject put(Member member, T value) {
        return put(member.name(), value);
    }

    public Set<String> propertyNames() {
        return object.keySet();
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
            try {
                JSONArray array = object.getJSONArray(name);
                if (array != null) {
                    byte[] bytes = new byte[array.length()];
                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = (byte) array.getInt(i);
                    }

                    return bytes;
                }
            } catch (JSONException e) {
                String value = asString();
                if (value != null) {
                    return value.getBytes();
                }
            }

            return null;
        }

        @Override
        public char asCharacter() {
            try {
                return (char) (int) object.getInt(name);
            } catch (JSONException e) {
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
        public Object asObject(Class<?> type) {
            if (type == boolean.class) {
                return object.has(name) ? asBoolean() : null;
            } else if (type == Boolean.class) {
                return object.has(name) ? asBoolean() : null;
            } else if (type == byte.class) {
                return object.has(name) ? asByte() : null;
            } else if (type == Byte.class) {
                return object.has(name) ? asByte() : null;
            } else if (type == char.class) {
                return object.has(name) ? asCharacter() : null;
            } else if (type == Character.class) {
                return object.has(name) ? asCharacter() : null;
            } else if (type == short.class) {
                return object.has(name) ? asShort() : null;
            } else if (type == Short.class) {
                return object.has(name) ? asShort() : null;
            } else if (type == int.class) {
                return object.has(name) ? asInteger() : null;
            } else if (type == Integer.class) {
                return object.has(name) ? asInteger() : null;
            } else if (type == long.class) {
                return object.has(name) ? asLong() : null;
            } else if (type == Long.class) {
                return object.has(name) ? asLong() : null;
            } else if (type == float.class) {
                return object.has(name) ? asFloat() : null;
            } else if (type == Float.class) {
                return object.has(name) ? asFloat() : null;
            } else if (type == double.class) {
                return object.has(name) ? asDouble() : null;
            } else if (type == Double.class) {
                return object.has(name) ? asDouble() : null;
            } else if (type == LocalDateTime.class) {
                return object.has(name) ? asLocalDateTime() : null;
            } else if (type == byte[].class) {
                return object.has(name) ? asByteArray() : null;
            } else {
                return asJson();
            }
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
