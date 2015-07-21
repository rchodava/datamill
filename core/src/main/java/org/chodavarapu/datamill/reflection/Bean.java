package org.chodavarapu.datamill.reflection;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Bean<T> {
    private final T instance;

    public Bean(T instance) {
        this.instance = instance;
    }

    public Optional<Property> property(String name) {
        return Optional.empty();
    }

    public Collection<Property> properties() {
        return Collections.emptyList();
    }
}
