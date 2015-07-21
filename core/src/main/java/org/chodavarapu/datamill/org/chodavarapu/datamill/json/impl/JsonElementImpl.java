package org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonElement;
import org.chodavarapu.datamill.reflection.Bean;

import java.util.function.BiConsumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class JsonElementImpl implements JsonElement {
    private final JsonParser parser;

    public JsonElementImpl(JsonParser parser) {
        this.parser = parser;
    }

    @Override
    public boolean isArray() {
        return parser.getCurrentToken() == JsonToken.START_ARRAY;
    }

    @Override
    public boolean isBoolean() {
        return parser.getCurrentToken() != null ? parser.getCurrentToken().isBoolean() : false;
    }

    @Override
    public boolean isField() {
        return parser.getCurrentToken() == JsonToken.FIELD_NAME;
    }

    @Override
    public boolean isNumeric() {
        return parser.getCurrentToken() != null ? parser.getCurrentToken().isNumeric() : false;
    }

    @Override
    public boolean isObject() {
        return parser.getCurrentToken() == JsonToken.START_OBJECT;
    }

    @Override
    public JsonElement get(String path) {
        return null;
    }

    @Override
    public <T> T mapToObject(T object, BiConsumer<JsonElement, Bean<T>> mapper) {
        mapper.accept(this, new Bean<>(object));
        return object;
    }
}
