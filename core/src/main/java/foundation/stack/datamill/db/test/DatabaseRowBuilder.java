package foundation.stack.datamill.db.test;

import foundation.stack.datamill.db.Row;
import foundation.stack.datamill.db.RowBuilder;
import foundation.stack.datamill.db.impl.SqlSyntax;
import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.reflection.OutlineBuilder;
import foundation.stack.datamill.values.StringValue;
import foundation.stack.datamill.values.Value;
import rx.functions.Func2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DatabaseRowBuilder<T> {
    private final Outline<T> outline;

    public DatabaseRowBuilder(Class<T> clazz) {
        this.outline = new OutlineBuilder().build(clazz);
    }

    public Row build(Func2<RowBuilder, Outline<T>, Map<String, ?>> rowBuilder) {
        Map<String, ?> values = rowBuilder.call(new DatabaseRowValuesBuilder(), outline);
        return new DatabaseRow(values);
    }

    private static class DatabaseRowValuesBuilder implements RowBuilder {
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
            return put(SqlSyntax.qualifiedName(member.outline().pluralName(), member.name()), value);
        }
    }


    private static class DatabaseRow implements Row {
        private final Map<String, ?> values;
        private final List<String> columnNamesByIndex = new ArrayList<>();

        public DatabaseRow(Map<String, ?> values) {
            this.values = values;

            for (String key : values.keySet()) {
                columnNamesByIndex.add(key);
            }
        }

        @Override
        public Value get(int index) {
            return get(columnNamesByIndex.get(index));
        }

        @Override
        public Value get(Member member) {
            return get(member.outline().pluralName(), member.name());
        }

        @Override
        public Value get(String name) {
            Object value = values.get(name);
            if (value != null) {
                return new StringValue(value.toString());
            }

            return null;
        }

        @Override
        public Value get(String table, String name) {
            return get(SqlSyntax.qualifiedName(table, name));
        }

        @Override
        public int size() {
            return values.size();
        }
    }
}
