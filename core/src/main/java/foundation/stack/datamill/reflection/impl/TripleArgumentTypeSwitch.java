package foundation.stack.datamill.reflection.impl;

import rx.functions.Func3;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class TripleArgumentTypeSwitch<T1, T2, T3, R> {
    private final Map<Class<?>, Func3<T1, T2, T3, R>> cases = new ConcurrentHashMap<>();

    public TripleArgumentTypeSwitch() {
        cases.put(boolean.class, (v1, v2, v3) -> caseBoolean(v1, v2, v3));
        cases.put(Boolean.class, (v1, v2, v3) -> caseBooleanWrapper(v1, v2, v3));
        cases.put(byte.class, (v1, v2, v3) -> caseByte(v1, v2, v3));
        cases.put(Byte.class, (v1, v2, v3) -> caseByteWrapper(v1, v2, v3));
        cases.put(char.class, (v1, v2, v3) -> caseCharacter(v1, v2, v3));
        cases.put(Character.class, (v1, v2, v3) -> caseCharacterWrapper(v1, v2, v3));
        cases.put(short.class, (v1, v2, v3) -> caseShort(v1, v2, v3));
        cases.put(Short.class, (v1, v2, v3) -> caseShortWrapper(v1, v2, v3));
        cases.put(int.class, (v1, v2, v3) -> caseInteger(v1, v2, v3));
        cases.put(Integer.class, (v1, v2, v3) -> caseIntegerWrapper(v1, v2, v3));
        cases.put(long.class, (v1, v2, v3) -> caseLong(v1, v2, v3));
        cases.put(Long.class, (v1, v2, v3) -> caseLongWrapper(v1, v2, v3));
        cases.put(float.class, (v1, v2, v3) -> caseFloat(v1, v2, v3));
        cases.put(Float.class, (v1, v2, v3) -> caseFloatWrapper(v1, v2, v3));
        cases.put(double.class, (v1, v2, v3) -> caseDouble(v1, v2, v3));
        cases.put(Double.class, (v1, v2, v3) -> caseDoubleWrapper(v1, v2, v3));
        cases.put(LocalDateTime.class, (v1, v2, v3) -> caseLocalDateTime(v1, v2, v3));
        cases.put(byte[].class, (v1, v2, v3) -> caseByteArray(v1, v2, v3));
    }

    protected abstract R caseBoolean(T1 value1, T2 value2, T3 value3);
    protected R caseBooleanWrapper(T1 value1, T2 value2, T3 value3) {
        return caseBoolean(value1, value2, value3);
    }

    protected abstract R caseByte(T1 value1, T2 value2, T3 value3);
    protected R caseByteWrapper(T1 value1, T2 value2, T3 value3) {
        return caseByte(value1, value2, value3);
    }

    protected abstract R caseCharacter(T1 value1, T2 value2, T3 value3);
    protected R caseCharacterWrapper(T1 value1, T2 value2, T3 value3) {
        return caseCharacter(value1, value2, value3);
    }

    protected abstract R caseShort(T1 value1, T2 value2, T3 value3);
    protected R caseShortWrapper(T1 value1, T2 value2, T3 value3) {
        return caseShort(value1, value2, value3);
    }

    protected abstract R caseInteger(T1 value1, T2 value2, T3 value3);
    protected R caseIntegerWrapper(T1 value1, T2 value2, T3 value3) {
        return caseInteger(value1, value2, value3);
    }

    protected abstract R caseLong(T1 value1, T2 value2, T3 value3);
    protected R caseLongWrapper(T1 value1, T2 value2, T3 value3) {
        return caseLong(value1, value2, value3);
    }

    protected abstract R caseFloat(T1 value1, T2 value2, T3 value3);
    protected R caseFloatWrapper(T1 value1, T2 value2, T3 value3) {
        return caseFloat(value1, value2, value3);
    }

    protected abstract R caseDouble(T1 value1, T2 value2, T3 value3);
    protected R caseDoubleWrapper(T1 value1, T2 value2, T3 value3) {
        return caseDouble(value1, value2, value3);
    }

    protected abstract R caseLocalDateTime(T1 value1, T2 value2, T3 value3);
    protected abstract R caseByteArray(T1 value1, T2 value2, T3 value3);

    protected abstract R defaultCase(T1 value1, T2 value2, T3 value3);

    public final R doSwitch(Class<?> type, T1 value1, T2 value2, T3 value3) {
        Func3<T1, T2, T3, R> typeCase = cases.get(type);
        if (typeCase != null) {
            return typeCase.call(value1, value2, value3);
        } else {
            return defaultCase(value1, value2, value3);
        }
    }
}
