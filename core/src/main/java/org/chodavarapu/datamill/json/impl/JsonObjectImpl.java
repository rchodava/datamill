package org.chodavarapu.datamill.json.impl;

import org.chodavarapu.datamill.json.JsonElement;
import org.chodavarapu.datamill.json.JsonValueType;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObjectImpl extends JsonElementImpl {
    public JsonObjectImpl() {
        super(JsonValueType.OBJECT);
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public String asString() {
        return super.asString();
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public int asInteger() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public boolean isObject() {
        return true;
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
