package foundation.stack.datamill.reflection.impl;

import rx.functions.Func2;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class TypeSwitch<T1, T2, R> {
    private final Map<Class<?>, Func2<T1, T2, R>> cases = new ConcurrentHashMap<>();

    public TypeSwitch() {
        cases.put(boolean.class, (v1, v2) -> caseBoolean(v1, v2));
        cases.put(Boolean.class, (v1, v2) -> caseBooleanWrapper(v1, v2));
        cases.put(byte.class, (v1, v2) -> caseByte(v1, v2));
        cases.put(Byte.class, (v1, v2) -> caseByteWrapper(v1, v2));
        cases.put(char.class, (v1, v2) -> caseCharacter(v1, v2));
        cases.put(Character.class, (v1, v2) -> caseCharacterWrapper(v1, v2));
        cases.put(short.class, (v1, v2) -> caseShort(v1, v2));
        cases.put(Short.class, (v1, v2) -> caseShortWrapper(v1, v2));
        cases.put(int.class, (v1, v2) -> caseInteger(v1, v2));
        cases.put(Integer.class, (v1, v2) -> caseIntegerWrapper(v1, v2));
        cases.put(long.class, (v1, v2) -> caseLong(v1, v2));
        cases.put(Long.class, (v1, v2) -> caseLongWrapper(v1, v2));
        cases.put(float.class, (v1, v2) -> caseFloat(v1, v2));
        cases.put(Float.class, (v1, v2) -> caseFloatWrapper(v1, v2));
        cases.put(double.class, (v1, v2) -> caseDouble(v1, v2));
        cases.put(Double.class, (v1, v2) -> caseDoubleWrapper(v1, v2));
        cases.put(LocalDateTime.class, (v1, v2) -> caseLocalDateTime(v1, v2));
        cases.put(byte[].class, (v1, v2) -> caseByteArray(v1, v2));
        cases.put(String.class, (v1, v2) -> caseString(v1, v2));
    }

    protected abstract R caseBoolean(T1 value1, T2 value2);
    protected R caseBooleanWrapper(T1 value1, T2 value2) {
        return caseBoolean(value1, value2);
    }

    protected abstract R caseByte(T1 value1, T2 value2);
    protected R caseByteWrapper(T1 value1, T2 value2) {
        return caseByte(value1, value2);
    }

    protected abstract R caseCharacter(T1 value1, T2 value2);
    protected R caseCharacterWrapper(T1 value1, T2 value2) {
        return caseCharacter(value1, value2);
    }

    protected abstract R caseShort(T1 value1, T2 value2);
    protected R caseShortWrapper(T1 value1, T2 value2) {
        return caseShort(value1, value2);
    }

    protected abstract R caseInteger(T1 value1, T2 value2);
    protected R caseIntegerWrapper(T1 value1, T2 value2) {
        return caseInteger(value1, value2);
    }

    protected abstract R caseLong(T1 value1, T2 value2);
    protected R caseLongWrapper(T1 value1, T2 value2) {
        return caseLong(value1, value2);
    }

    protected abstract R caseFloat(T1 value1, T2 value2);
    protected R caseFloatWrapper(T1 value1, T2 value2) {
        return caseFloat(value1, value2);
    }

    protected abstract R caseDouble(T1 value1, T2 value2);
    protected R caseDoubleWrapper(T1 value1, T2 value2) {
        return caseDouble(value1, value2);
    }

    protected abstract R caseLocalDateTime(T1 value1, T2 value2);
    protected abstract R caseByteArray(T1 value1, T2 value2);

    protected abstract R caseString(T1 value1, T2 value2);

    protected abstract R defaultCase(T1 value1, T2 value2);

    public final R doSwitch(Class<?> type, T1 value1, T2 value2) {
        Func2<T1, T2, R> typeCase = cases.get(type);
        if (typeCase != null) {
            return typeCase.call(value1, value2);
        } else {
            return defaultCase(value1, value2);
        }
    }
}
