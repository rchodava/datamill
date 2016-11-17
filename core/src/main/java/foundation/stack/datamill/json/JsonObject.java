package foundation.stack.datamill.json;

import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.values.MutableStructuredValue;
import foundation.stack.datamill.values.ReflectableValue;
import foundation.stack.datamill.reflection.impl.TripleArgumentTypeSwitch;
import foundation.stack.datamill.values.SerializationStrategy;
import foundation.stack.datamill.values.Value;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObject implements Json, ReflectableValue, MutableStructuredValue {
    private static final TripleArgumentTypeSwitch<JSONObject, String, JsonProperty, Object> propertyAsObjectSwitch =
            new TripleArgumentTypeSwitch<JSONObject, String, JsonProperty, Object>() {
                @Override
                protected Object caseBoolean(JSONObject value1, String value2, JsonProperty value3) {
                    return value1.has(value2) ? value3.asBoolean() : null;
                }

                @Override
                protected Object caseByte(JSONObject value1, String value2, JsonProperty value3) {
                    return value1.has(value2) ? value3.asByte() : null;
                }

                @Override
                protected Object caseCharacter(JSONObject value1, String value2, JsonProperty value3) {
                    return value1.has(value2) ? value3.asCharacter() : null;
                }

                @Override
                protected Object caseShort(JSONObject value1, String value2, JsonProperty value3) {
                    try {
                        return value1.has(value2) ? value3.asShort() : null;
                    } catch (JSONException __) {
                        return null;
                    }
                }

                @Override
                protected Object caseInteger(JSONObject value1, String value2, JsonProperty value3) {
                    try {
                        return value1.has(value2) ? value3.asInteger() : null;
                    } catch (JSONException __) {
                        return null;
                    }
                }

                @Override
                protected Object caseLong(JSONObject value1, String value2, JsonProperty value3) {
                    try {
                        return value1.has(value2) ? value3.asLong() : null;
                    } catch (JSONException __) {
                        return null;
                    }
                }

                @Override
                protected Object caseFloat(JSONObject value1, String value2, JsonProperty value3) {
                    return value1.has(value2) ? value3.asFloat() : null;
                }

                @Override
                protected Object caseDouble(JSONObject value1, String value2, JsonProperty value3) {
                    return value1.has(value2) ? value3.asDouble() : null;
                }

                @Override
                protected Object caseLocalDateTime(JSONObject value1, String value2, JsonProperty value3) {
                    return value1.has(value2) ? value3.asLocalDateTime() : null;
                }

                @Override
                protected Object caseByteArray(JSONObject value1, String value2, JsonProperty value3) {
                    return value1.has(value2) ? value3.asByteArray() : null;
                }

                @Override
                protected Object defaultCase(JSONObject value1, String value2, JsonProperty value3) {
                    return value3.asJson();
                }
            };

    final JSONObject object;

    JsonObject(JSONObject object) {
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

    @Override
    public Value get(String property) {
        return new JsonProperty(property);
    }

    @Override
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

    @Override
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

    @Override
    public MutableStructuredValue put(String name, Object[] value) {
        object.put(name, Arrays.asList(value));
        return this;
    }

    @Override
    public MutableStructuredValue put(String name, Map<String, ?> value) {
        object.put(name, value);
        return this;
    }

    @Override
    public <T> MutableStructuredValue put(
            String name,
            Collection<T> values,
            SerializationStrategy<T> valueSerializationStrategy) {
        if (values != null) {
//            Json.serializer()
            JSONArray array = new JSONArray();
//            for (T value : values) {
//                JsonObject transformed = transformer.call(new JsonObject(), value);
//                if (transformed != null) {
//                    array.put(transformed.object);
//                } else {
//                    array.put((Object) null);
//                }
//            }

            object.put(name, array);
        } else {
            object.put(name, (Object) null);
        }

        return this;
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
            return propertyAsObjectSwitch.doSwitch(type, object, name, this);
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
