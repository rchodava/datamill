package org.chodavarapu.datamill;

import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Value {
    boolean asBoolean();
    double asDouble();
    float asFloat();
    int asInteger();
    long asLong();
    String asString();

    boolean isBoolean();
    boolean isFloat();
    boolean isInteger();
    boolean isIntegral();
    boolean isLong();
    boolean isNumeric();
    boolean isString();

    <T> T map(Function<? extends Value, T> mapper);
}
