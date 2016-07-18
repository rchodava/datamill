package foundation.stack.datamill.values;

import foundation.stack.datamill.reflection.impl.TypeSwitch;

import java.time.LocalDateTime;
import java.util.UnknownFormatConversionException;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class StringValue implements ReflectableValue {
    private static final TypeSwitch<StringValue, String, Object> stringCastSwitch =
            new TypeSwitch<StringValue, String, Object>() {
                @Override
                protected Object caseBoolean(StringValue value1, String value2) {
                    return value2 != null ? value1.asBoolean() : null;
                }

                @Override
                protected Object caseByte(StringValue value1, String value2) {
                    return value2 != null ? value1.asByte() : null;
                }

                @Override
                protected Object caseCharacter(StringValue value1, String value2) {
                    return value2 != null ? value1.asCharacter() : null;
                }

                @Override
                protected Object caseShort(StringValue value1, String value2) {
                    return value2 != null ? value1.asShort() : null;
                }

                @Override
                protected Object caseInteger(StringValue value1, String value2) {
                    return value2 != null ? value1.asInteger() : null;
                }

                @Override
                protected Object caseLong(StringValue value1, String value2) {
                    return value2 != null ? value1.asLong() : null;
                }

                @Override
                protected Object caseFloat(StringValue value1, String value2) {
                    return value2 != null ? value1.asFloat() : null;
                }

                @Override
                protected Object caseDouble(StringValue value1, String value2) {
                    return value2 != null ? value1.asDouble() : null;
                }

                @Override
                protected Object caseLocalDateTime(StringValue value1, String value2) {
                    return value2 != null ? value1.asLocalDateTime() : null;
                }

                @Override
                protected Object caseByteArray(StringValue value1, String value2) {
                    return value2 != null ? value1.asByteArray() : null;
                }

                @Override
                protected Object caseString(StringValue value1, String value2) {
                    return value2 != null ? value1.asString() : null;
                }

                @Override
                protected Object defaultCase(StringValue value1, String value2) {
                    return value1.asString();
                }
            };

    private String value;

    public StringValue(String value) {
        this.value = value;
    }

    private boolean isFalsy() {
        return "".equals(value) ||
                "false".equals(value) ||
                "null".equals(value) ||
                "0".equals(value) ||
                "undefined".equals(value) ||
                "NaN".equals(value);
    }

    @Override
    public byte asByte() {
        return Byte.parseByte(value);
    }

    @Override
    public byte[] asByteArray() {
        return asString().getBytes();
    }

    @Override
    public boolean asBoolean() {
        return !isFalsy();
    }

    @Override
    public char asCharacter() {
        if (value.length() != 1) {
            throw new UnknownFormatConversionException("Unable to convert string to character!");
        }

        return value.charAt(0);
    }

    @Override
    public double asDouble() {
        return Double.parseDouble(value);
    }

    @Override
    public float asFloat() {
        return Float.parseFloat(value);
    }

    @Override
    public LocalDateTime asLocalDateTime() {
        return LocalDateTime.parse(value);
    }

    @Override
    public long asLong() {
        return Long.parseLong(value);
    }

    @Override
    public int asInteger() {
        return Integer.parseInt(value);
    }

    @Override
    public Object asObject(Class<?> type) {
        return stringCastSwitch.doSwitch(type, this, value);
    }

    @Override
    public short asShort() {
        return Short.parseShort(value);
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public boolean isBoolean() {
        return "true".equals(value) || "false".equals(value) || "1".equals(value) || "0".equals(value);
    }

    @Override
    public boolean isByte() {
        try {
            asByte();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isCharacter() {
        return value.length() == 1;
    }

    @Override
    public boolean isDouble() {
        try {
            asDouble();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isFloat() {
        try {
            asFloat();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isNumeric() {
        return isLong() || isFloat();
    }

    @Override
    public boolean isLong() {
        try {
            asLong();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isInteger() {
        try {
            asInteger();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isShort() {
        try {
            asShort();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isString() {
        return true;
    }

    @Override
    public <T> T map(Function<Value, T> mapper) {
        return mapper.apply(this);
    }
}
