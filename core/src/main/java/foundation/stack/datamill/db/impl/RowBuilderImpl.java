package foundation.stack.datamill.db.impl;

import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.db.RowBuilder;

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
