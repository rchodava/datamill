package org.chodavarapu.datamill.org.chodavarapu.datamill.json;

import org.chodavarapu.datamill.reflection.Bean;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JsonElement extends JsonValue {
    boolean isArray();
    boolean isField();
    boolean isObject();

    List<JsonElement> children();
    JsonElement get(String path);
    <T> T mapToObject(T object, BiConsumer<JsonElement, Bean<T>> mapper);
    <T> T map(Function<JsonElement, T> mapper);
}
