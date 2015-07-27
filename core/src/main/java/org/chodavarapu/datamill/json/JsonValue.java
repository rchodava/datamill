package org.chodavarapu.datamill.json;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JsonValue {
    boolean asBoolean();
    double asDouble();
    float asFloat();
    int asInteger();
    long asLong();
    String asString();

    JsonValueType getType();

    boolean isBoolean();
    boolean isIntegral();
    boolean isNumeric();
    boolean isString();
}
