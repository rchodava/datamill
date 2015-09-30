package org.chodavarapu.datamill.values;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ReflectableValue extends Value {
    boolean isBoolean();
    boolean isFloat();
    boolean isInteger();
    boolean isIntegral();
    boolean isLong();
    boolean isNumeric();
    boolean isString();
}
