package org.chodavarapu.datamill.values;

import java.time.LocalDateTime;
import java.util.UnknownFormatConversionException;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class StringValue implements ReflectableValue {
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
        if (type == boolean.class) {
            return value != null ? asBoolean() : null;
        } else if (type == Boolean.class) {
            return value != null ? asBoolean() : null;
        } else if (type == byte.class) {
            return value != null ? asByte() : null;
        } else if (type == Byte.class) {
            return value != null ? asByte() : null;
        } else if (type == char.class) {
            return value != null ? asCharacter() : null;
        } else if (type == Character.class) {
            return value != null ? asCharacter() : null;
        } else if (type == short.class) {
            return value != null ? asShort() : null;
        } else if (type == Short.class) {
            return value != null ? asShort() : null;
        } else if (type == int.class) {
            return value != null ? asInteger() : null;
        } else if (type == Integer.class) {
            return value != null ? asInteger() : null;
        } else if (type == long.class) {
            return value != null ? asLong() : null;
        } else if (type == Long.class) {
            return value != null ? asLong() : null;
        } else if (type == float.class) {
            return value != null ? asFloat() : null;
        } else if (type == Float.class) {
            return value != null ? asFloat() : null;
        } else if (type == double.class) {
            return value != null ? asDouble() : null;
        } else if (type == Double.class) {
            return value != null ? asDouble() : null;
        } else if (type == LocalDateTime.class) {
            return value != null ? asLocalDateTime() : null;
        } else if (type == byte[].class) {
            return value != null ? asByteArray() : null;
        } else {
            return asString();
        }
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
