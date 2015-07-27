package org.chodavarapu.datamill.json.impl;

import org.chodavarapu.datamill.json.JsonElement;
import org.chodavarapu.datamill.json.JsonValueType;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonArrayImpl extends JsonElementImpl {
    public JsonArrayImpl() {
        super(JsonValueType.ARRAY);
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public int asInteger() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public String asString() {
        return "";
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public boolean isIntegral() {
        return false;
    }

    @Override
    public JsonElement get(String path) {
        return null;
    }
}
