package org.chodavarapu.datamill.db.impl;

import com.google.common.base.Joiner;
import org.chodavarapu.datamill.db.*;
import rx.Observable;

import java.util.*;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class QueryBuilderImpl implements QueryBuilder {
    private static final String SQL_DELETE_FROM = "DELETE FROM ";
    private static final String SQL_INSERT_INTO = "INSERT INTO ";
    private static final String SQL_SELECT = "SELECT ";
    private static final String SQL_EQ = " = ";
    private static final String SQL_FROM = " FROM ";
    private static final String SQL_NULL = "NULL";
    private static final String SQL_PARAMETER_PLACEHOLDER = "?";
    private static final String SQL_WHERE = " WHERE ";

    private class InsertQuery implements InsertBuilder {
        private final StringBuilder query = new StringBuilder();

        public InsertQuery(String table) {
            query.append(SQL_INSERT_INTO);
            query.append(table);
        }

        @Override
        public Observable<Row> row(Function<RowBuilder, Map<String, ?>> constructor) {
            return values(constructor.apply(new RowBuilderImpl()));
        }

        @Override
        public Observable<Row> values(Map<String, ?>... rows) {
            Set<String> columns = new LinkedHashSet<>();
            for (Map<String, ?> row : rows) {
                columns.addAll(row.keySet());
            }

            int numColumns = columns.size();
            if (numColumns < 1) {
                throw new IllegalArgumentException("Cannot insert rows that have no columns!");
            }

            query.append(" (");
            Joiner.on(", ").appendTo(query, columns);
            query.append(") VALUES ");

            List<Object> parameters = new ArrayList<>(rows.length * numColumns);
            List<String> valueSets = new ArrayList<>(rows.length);

            for (Map<String, ?> row : rows) {
                StringBuilder values = new StringBuilder("(");

                int columnIndex = 0;
                for (String column : columns) {
                    Object value = row.get(column);
                    if (value == null) {
                        values.append(SQL_NULL);
                    } else {
                        values.append(SQL_PARAMETER_PLACEHOLDER);
                        parameters.add(value);
                    }

                    if (columnIndex < numColumns - 1) {
                        values.append(", ");
                    }

                    columnIndex++;
                }

                values.append(')');
                valueSets.add(values.toString());
            }

            Joiner.on(", ").appendTo(query, valueSets);

            return QueryBuilderImpl.this.query(query.toString(), parameters.toArray(new Object[parameters.size()]));
        }
    }

    private class WhereClause implements WhereBuilder, ConditionBuilder {
        private final StringBuilder query;

        public WhereClause(StringBuilder query) {
            this.query = query;
        }

        @Override
        public Observable<Row> all() {
            return QueryBuilderImpl.this.query(query.toString());
        }

        @Override
        public <T> Observable<Row> eq(String column, T value) {
            query.append(column);
            query.append(SQL_EQ);
            query.append(SQL_PARAMETER_PLACEHOLDER);
            return QueryBuilderImpl.this.query(query.toString(), value);
        }

        @Override
        public ConditionBuilder where() {
            query.append(SQL_WHERE);
            return this;
        }
    }

    private class SelectQuery implements SelectBuilder {
        private final StringBuilder query = new StringBuilder();

        public SelectQuery() {
            query.append(SQL_SELECT);
            query.append('*');
        }

        public SelectQuery(Iterable<String> columns) {
            query.append(SQL_SELECT);
            query.append(Joiner.on(", ").join(columns));
        }

        @Override
        public WhereBuilder from(String table) {
            query.append(SQL_FROM);
            query.append(table);
            return new WhereClause(query);
        }
    }

    @Override
    public WhereBuilder deleteFrom(String table) {
        return new WhereClause(new StringBuilder(SQL_DELETE_FROM).append(table));
    }

    @Override
    public InsertBuilder insertInto(String table) {
        return new InsertQuery(table);
    }

    protected abstract Observable<Row> query(String query);
    protected abstract Observable<Row> query(String query, Object... parameters);

    @Override
    public SelectBuilder select(String column) {
        return select(Arrays.asList(column));
    }

    @Override
    public SelectBuilder select(String... columns) {
        return select(Arrays.asList(columns));
    }

    @Override
    public SelectBuilder select(Iterable<String> columns) {
        return new SelectQuery(columns);
    }

    @Override
    public SelectBuilder selectAll() {
        return new SelectQuery();
    }
}
