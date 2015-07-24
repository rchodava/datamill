package org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl;

import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonValue;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonValueType;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class JsonValueImpl implements JsonValue {
    private final JsonValueType type;

    protected JsonValueImpl(JsonValueType type) {
        this.type = type;
    }

    @Override
    public JsonValueType getType() {
        return type;
    }

    @Override
    public boolean isBoolean() {
        return type == JsonValueType.FALSE || type == JsonValueType.TRUE;
    }

    @Override
    public boolean isNumeric() {
        return type == JsonValueType.NUMBER;
    }

    @Override
    public boolean isString() {
        return type == JsonValueType.STRING;
    }
}
