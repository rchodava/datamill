package org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl;

import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonElement;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonObjectImpl extends JsonElementImpl {

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isBoolean() {
        return false;
    }

    @Override
    public boolean isField() {
        return false;
    }

    @Override
    public boolean isNumeric() {
        return false;
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public JsonElement get(String path) {
        return null;
    }
}
