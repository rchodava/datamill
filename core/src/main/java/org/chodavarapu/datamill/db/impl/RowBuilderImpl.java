package org.chodavarapu.datamill.db.impl;

import org.chodavarapu.datamill.db.RowBuilder;
import org.chodavarapu.datamill.reflection.Member;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RowBuilderImpl implements RowBuilder {
    private final Map<String, Object> values = new LinkedHashMap<>();

    @Override
    public Map<String, ?> build() {
        return values;
    }

    @Override
    public <T> RowBuilder put(String name, T value) {
        values.put(name, value);
        return this;
    }

    @Override
    public <T> RowBuilder put(Member member, T value) {
        return put(member.name(), value);
    }
}
