package org.chodavarapu.datamill.db.impl;

import org.chodavarapu.datamill.db.RowBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RowBuilderImpl implements RowBuilder {
    private final Map<String, Object> values = new HashMap<>();

    @Override
    public Map<String, ?> build() {
        return values;
    }

    @Override
    public <T> RowBuilder put(String name, T value) {
        values.put(name, value);
        return this;
    }
}
