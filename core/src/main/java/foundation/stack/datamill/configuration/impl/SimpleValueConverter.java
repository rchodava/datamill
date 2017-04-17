package foundation.stack.datamill.configuration.impl;

import foundation.stack.datamill.reflection.impl.TypeSwitch;
import foundation.stack.datamill.values.StringValue;
import foundation.stack.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SimpleValueConverter {
    private static final TypeSwitch<Object, Void, Object> downCast = new TypeSwitch<Object, Void, Object>() {
        @Override
        protected Object caseBoolean(Object value, Void __) {
            return (boolean) value;
        }

        @Override
        protected Object caseByte(Object value, Void __) {
            return (byte) (long) value;
        }

        @Override
        protected Object caseCharacter(Object value, Void __) {
            return (char) (long) value;
        }

        @Override
        protected Object caseShort(Object value, Void __) {
            return (short) (long) value;
        }

        @Override
        protected Object caseInteger(Object value, Void __) {
            return (int) (long) value;
        }

        @Override
        protected Object caseLong(Object value, Void __) {
            return (long) value;
        }

        @Override
        protected Object caseFloat(Object value, Void __) {
            return (float) (double) value;
        }

        @Override
        protected Object caseDouble(Object value, Void __) {
            return (double) value;
        }

        @Override
        protected Object caseLocalDateTime(Object value, Void __) {
            return null;
        }

        @Override
        protected Object caseByteArray(Object value, Void __) {
            return null;
        }

        @Override
        protected Object caseString(Object value1, Void value2) {
            return null;
        }

        @Override
        protected Object defaultCase(Object value, Void __) {
            return null;
        }
    };

    private static final TypeSwitch<Object, Void, Object> upCast = new TypeSwitch<Object, Void, Object>() {
        @Override
        protected Object caseBoolean(Object value, Void __) {
            return (boolean) value;
        }

        @Override
        protected Object caseByte(Object value, Void __) {
            return (long) (byte) value;
        }

        @Override
        protected Object caseCharacter(Object value, Void __) {
            return (long) (char) value;
        }

        @Override
        protected Object caseShort(Object value, Void __) {
            return (long) (short) value;
        }

        @Override
        protected Object caseInteger(Object value, Void __) {
            return (long) (int) value;
        }

        @Override
        protected Object caseLong(Object value, Void __) {
            return (long) value;
        }

        @Override
        protected Object caseFloat(Object value, Void __) {
            return (double) (float) value;
        }

        @Override
        protected Object caseDouble(Object value, Void __) {
            return (double) value;
        }

        @Override
        protected Object caseLocalDateTime(Object value, Void __) {
            return null;
        }

        @Override
        protected Object caseByteArray(Object value, Void __) {
            return null;
        }

        @Override
        protected Object caseString(Object value1, Void value2) {
            return null;
        }

        @Override
        protected Object defaultCase(Object value, Void __) {
            return null;
        }
    };

    private static final TypeSwitch<Value, Void, Object> valueCast = new TypeSwitch<Value, Void, Object>() {
        @Override
        protected Object caseBoolean(Value value, Void __) {
            return value.asBoolean();
        }

        @Override
        protected Object caseByte(Value value, Void __) {
            return value.asByte();
        }

        @Override
        protected Object caseCharacter(Value value, Void __) {
            return value.asCharacter();
        }

        @Override
        protected Object caseShort(Value value, Void __) {
            return value.asShort();
        }

        @Override
        protected Object caseInteger(Value value, Void __) {
            return value.asInteger();
        }

        @Override
        protected Object caseLong(Value value, Void __) {
            return value.asLong();
        }

        @Override
        protected Object caseFloat(Value value, Void __) {
            return value.asFloat();
        }

        @Override
        protected Object caseDouble(Value value, Void __) {
            return value.asDouble();
        }

        @Override
        protected Object caseLocalDateTime(Value value, Void __) {
            return value.asLocalDateTime();
        }

        @Override
        protected Object caseByteArray(Value value, Void __) {
            return value.asByteArray();
        }

        @Override
        protected Object caseString(Value value1, Void value2) {
            return value1.asString();
        }

        @Override
        protected Object defaultCase(Value value, Void __) {
            return value;
        }
    };

    private static Object castValueToTypeIfPossible(Value value, Class<?> type) {
        Object castedValue = valueCast.doSwitch(type, value, null);
        if (castedValue != null && Classes.isAssignable(type, castedValue.getClass())) {
            return castedValue;
        }

        return null;
    }

    public static <T> T convert(Object value, Class<T> type) {
        if (value != null) {
            if (type.isInstance(value)) {
                return (T) value;
            }

            if (type.isPrimitive()) {
                Class<?> wrapper = Classes.primitiveToWrapper(type);
                if (wrapper.isInstance(value.getClass())) {
                    return (T) value;
                }

                Class<?> valuePrimitive = Classes.wrapperToPrimitive(value.getClass());
                if (Classes.isAssignable(valuePrimitive, type) || Classes.isAssignable(type, valuePrimitive)) {
                    return (T) downCast.doSwitch(type, upCast.doSwitch(value.getClass(), value, null), null);
                }
            } else if (Classes.isPrimitiveWrapper(type)) {
                Class<?> typePrimitive = Classes.wrapperToPrimitive(type);
                Class<?> valuePrimitive = Classes.wrapperToPrimitive(value.getClass());

                if (Classes.isAssignable(valuePrimitive, typePrimitive) || Classes.isAssignable(typePrimitive, valuePrimitive)) {
                    return (T) downCast.doSwitch(typePrimitive, upCast.doSwitch(value.getClass(), value, null), null);
                }
            }

            if (Value.class.isAssignableFrom(value.getClass())) {
                if (type == Value.class) {
                    return (T) value;
                }

                return (T) castValueToTypeIfPossible((Value) value, type);
            }

            if (type == Value.class) {
                return (T) new StringValue(value.toString());
            }
        }

        return null;
    }
}
