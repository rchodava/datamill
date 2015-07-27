package org.chodavarapu.datamill.json.impl;

import org.chodavarapu.datamill.json.JsonElement;
import org.chodavarapu.datamill.json.JsonValueType;
import org.chodavarapu.datamill.reflection.Bean;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class JsonElementImpl extends JsonValueImpl implements JsonElement {
    protected JsonElementImpl(JsonValueType type) {
        super(type);
    }

    @Override
    public String asString() {
        return null;
    }

    @Override
    public List<JsonElement> children() {
        return null;
    }

    @Override
    public <T> T mapToObject(T object, BiConsumer<JsonElement, Bean<T>> mapper) {
        mapper.accept(this, new Bean<>(object));
        return object;
    }

    @Override
    public <T> T map(Function<JsonElement, T> mapper) {
        return mapper.apply(this);
    }
}
