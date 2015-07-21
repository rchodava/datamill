package org.chodavarapu.datamill.org.chodavarapu.datamill.json.impl;

import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonElement;
import org.chodavarapu.datamill.reflection.Bean;

import java.util.function.BiConsumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class JsonElementImpl implements JsonElement {
    @Override
    public <T> T mapToObject(T object, BiConsumer<JsonElement, Bean<T>> mapper) {
        mapper.accept(this, new Bean<>(object));
        return object;
    }
}
