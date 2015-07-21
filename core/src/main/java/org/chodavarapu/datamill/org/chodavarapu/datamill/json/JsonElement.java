package org.chodavarapu.datamill.org.chodavarapu.datamill.json;

import org.chodavarapu.datamill.reflection.Bean;

import java.util.function.BiConsumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JsonElement {
    boolean isArray();
    boolean isBoolean();
    boolean isField();
    boolean isNumeric();
    boolean isObject();

    JsonElement get(String path);
    <T> T mapToObject(T object, BiConsumer<JsonElement, Bean<T>> mapper);
}
