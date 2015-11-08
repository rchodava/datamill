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
    private static final UpdateQueryExecution EMPTY_UPDATE_EXECUTION = new EmptyUpdateQueryExecution();

    private static final String SQL_ASSIGNMENT = " = ";
    private static final String SQL_DELETE_FROM = "DELETE FROM ";
    private static final String SQL_EQ = " = ";
    private static final String SQL_FROM = " FROM ";
    private static final String SQL_INSERT_INTO = "INSERT INTO ";
    private static final String SQL_NULL = "NULL";
    private static final String SQL_PARAMETER_PLACEHOLDER = "?";
    private static final String SQL_SELECT = "SELECT ";
    private static final String SQL_SET = " SET ";
    private static final String SQL_WHERE = " WHERE ";
    private static final String SQL_UPDATE = "UPDATE ";

    private class UpdateQuery implements UpdateBuilder {
        private final List<Object> parameters = new ArrayList<>();
        private final StringBuilder query = new StringBuilder();

        public UpdateQuery(String table) {
            query.append(SQL_UPDATE);
            query.append(table);
            query.append(SQL_SET);
        }

        @Override
        public WhereBuilder<UpdateQueryExecution> set(Map<String, ?> values) {
            if (values.size() < 1) {
                return new UpdateWhereClause(query, parameters);
            }

            List<String> setters = new ArrayList<>(values.size());

            for (Map.Entry<String, ?> column : values.entrySet()) {
                StringBuilder setter = new StringBuilder(column.getKey());
                setter.append(SQL_ASSIGNMENT);

                Object value = column.getValue();
                if (value == null) {
                    setter.append(SQL_NULL);
                } else {
                    setter.append(SQL_PARAMETER_PLACEHOLDER);
                    parameters.add(value);
                }

                setters.add(setter.toString());
            }

            Joiner.on(", ").appendTo(query, setters);

            return new UpdateWhereClause(query, parameters);
        }

        @Override
        public WhereBuilder<UpdateQueryExecution> set(Function<RowBuilder, Map<String, ?>> rowConstructor) {
            return set(rowConstructor.apply(new RowBuilderImpl()));
        }
    }

    private class InsertQuery implements InsertBuilder {
        private final StringBuilder query = new StringBuilder();

        public InsertQuery(String table) {
            query.append(SQL_INSERT_INTO);
            query.append(table);
        }

        @Override
        public UpdateQueryExecution row(Function<RowBuilder, Map<String, ?>> constructor) {
            return values(constructor.apply(new RowBuilderImpl()));
        }

        @Override
        public UpdateQueryExecution values(Map<String, ?>... rows) {
            if (rows.length < 1) {
                return EMPTY_UPDATE_EXECUTION;
            }

            Set<String> columns = new LinkedHashSet<>();
            for (Map<String, ?> row : rows) {
                columns.addAll(row.keySet());
            }

            int numColumns = columns.size();
            if (numColumns < 1) {
                return EMPTY_UPDATE_EXECUTION;
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

            return QueryBuilderImpl.this.update(query.toString(), parameters.toArray(new Object[parameters.size()]));
        }
    }

    private class UpdateWhereClause extends WhereClause<UpdateQueryExecution> {
        public UpdateWhereClause(StringBuilder query) {
            super(query);
        }

        public UpdateWhereClause(StringBuilder query, List<Object> parameters) {
            super(query, parameters);
        }

        @Override
        public UpdateQueryExecution all() {
            return QueryBuilderImpl.this.update(query.toString(), parameters.toArray(new Object[parameters.size()]));
        }

        @Override
        public <T> UpdateQueryExecution eq(String column, T value) {
            addEqualityClause(column, value);
            return QueryBuilderImpl.this.update(query.toString(), parameters.toArray(new Object[parameters.size()]));
        }
    }

    private class SelectWhereClause extends WhereClause<Observable<Row>> {
        public SelectWhereClause(StringBuilder query) {
            super(query);
        }

        public SelectWhereClause(StringBuilder query, List<Object> parameters) {
            super(query, parameters);
        }

        @Override
        public Observable<Row> all() {
            if (parameters.size() > 0) {
                return QueryBuilderImpl.this.query(query.toString(), parameters.toArray(new Object[parameters.size()]));
            } else {
                return QueryBuilderImpl.this.query(query.toString());
            }
        }

        @Override
        public <T> Observable<Row> eq(String column, T value) {
            addEqualityClause(column, value);
            return QueryBuilderImpl.this.query(query.toString(), parameters.toArray(new Object[parameters.size()]));
        }
    }

    private abstract class WhereClause<R> implements WhereBuilder<R>, ConditionBuilder<R> {
        protected final StringBuilder query;
        protected final List<Object> parameters;

        public WhereClause(StringBuilder query) {
            this(query, new ArrayList<>());
        }

        public WhereClause(StringBuilder query, List<Object> parameters) {
            this.query = query;
            this.parameters = parameters;
        }

        protected <T> void addEqualityClause(String column, T value) {
            query.append(column);
            query.append(SQL_EQ);
            query.append(SQL_PARAMETER_PLACEHOLDER);

            parameters.add(value);
        }

        @Override
        public ConditionBuilder<R> where() {
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
        public WhereBuilder<Observable<Row>> from(String table) {
            query.append(SQL_FROM);
            query.append(table);
            return new SelectWhereClause(query);
        }
    }

    @Override
    public WhereBuilder<UpdateQueryExecution> deleteFrom(String table) {
        return new UpdateWhereClause(new StringBuilder(SQL_DELETE_FROM).append(table));
    }

    @Override
    public InsertBuilder insertInto(String table) {
        return new InsertQuery(table);
    }

    protected abstract Observable<Row> query(String query);
    protected abstract Observable<Row> query(String query, Object... parameters);
    protected abstract UpdateQueryExecution update(String query, Object... parameters);

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

    @Override
    public UpdateBuilder update(String table) {
        return new UpdateQuery(table);
    }

    private static class EmptyUpdateQueryExecution implements UpdateQueryExecution {
        @Override
        public Observable<Integer> count() {
            return Observable.empty();
        }

        @Override
        public Observable<Long> getIds() {
            return Observable.empty();
        }
    }
}
