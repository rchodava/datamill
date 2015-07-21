package org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonElement;
import org.chodavarapu.datamill.reflection.Bean;

import java.util.function.BiConsumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonArrayImpl extends JsonElementImpl {

    @Override
    public boolean isArray() {
        return true;
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
        return false;
    }

    @Override
    public JsonElement get(String path) {
        return null;
    }
}
