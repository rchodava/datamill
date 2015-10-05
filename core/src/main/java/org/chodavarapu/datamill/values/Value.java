package org.chodavarapu.datamill.values;

import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Value {
    boolean asBoolean();
    byte asByte();
    char asCharacter();
    double asDouble();
    float asFloat();
    int asInteger();
    long asLong();
    short asShort();
    String asString();

    <T> T map(Function<Value, T> mapper);
}
