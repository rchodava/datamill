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
