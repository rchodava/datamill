package org.chodavarapu.datamill.values;

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

    <T> T map(Function<Value, T> mapper);
}
