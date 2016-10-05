package foundation.stack.datamill.db.impl;

import com.google.common.base.Joiner;
import foundation.stack.datamill.db.LimitBuilder;
import foundation.stack.datamill.db.*;
import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.values.Times;
import rx.Observable;

import java.sql.Timestamp;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class QueryBuilderImpl implements QueryBuilder {
    private static final Object[] EMPTY_PARAMETERS = new Object[0];
    private static final InsertSuffixBuilder EMPTY_UPDATE_BUILDER = new EmptyUpdateSuffixBuilder();

    private static void appendUpdateAssignments(StringBuilder query, List<Object> parameters, Map<String, ?> values) {
        List<String> setters = new ArrayList<>(values.size());

        for (Map.Entry<String, ?> column : values.entrySet()) {
            StringBuilder setter = new StringBuilder(column.getKey());
            setter.append(SqlSyntax.SQL_ASSIGNMENT);

            Object value = column.getValue();
            if (value == null) {
                setter.append(SqlSyntax.SQL_NULL);
            } else {
                setter.append(SqlSyntax.SQL_PARAMETER_PLACEHOLDER);
                parameters.add(value);
            }

            setters.add(setter.toString());
        }

        Joiner.on(", ").appendTo(query, setters);
    }

    private class UpdateQuery implements UpdateBuilder {
        private final List<Object> parameters = new ArrayList<>();
        private final StringBuilder query = new StringBuilder();

        public UpdateQuery(String table) {
            query.append(SqlSyntax.SQL_UPDATE);
            query.append(table);
            query.append(SqlSyntax.SQL_SET);
        }

        @Override
        public WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> set(
                Map<String, ?> values) {
            if (values.size() < 1) {
                return new UpdateWhereClause(query, parameters);
            }

            appendUpdateAssignments(query, parameters, values);

            return new UpdateWhereClause(query, parameters);
        }

        @Override
        public WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> set(
                Function<RowBuilder, Map<String, ?>> rowConstructor) {
            return set(rowConstructor.apply(new RowBuilderImpl()));
        }
    }

    private class InsertQuery implements InsertBuilder {
        private final StringBuilder query = new StringBuilder();

        public InsertQuery(String table) {
            query.append(SqlSyntax.SQL_INSERT_INTO);
            query.append(table);
        }

        @Override
        public InsertSuffixBuilder row(Function<RowBuilder, Map<String, ?>> constructor) {
            return values(constructor.apply(new RowBuilderImpl()));
        }

        @Override
        public <T> InsertSuffixBuilder values(Collection<T> values, BiFunction<RowBuilder, T, Map<String, ?>> constructor) {
            ArrayList<Map<String, ?>> rows = new ArrayList<>(values.size());
            for (T value : values) {
                Map<String, ?> row = constructor.apply(new RowBuilderImpl(), value);
                rows.add(row);
            }

            return values(rows.toArray(new Map[rows.size()]));
        }

        @Override
        public InsertSuffixBuilder values(Map<String, ?>... rows) {
            if (rows.length < 1) {
                return EMPTY_UPDATE_BUILDER;
            }

            Set<String> columns = new LinkedHashSet<>();
            for (Map<String, ?> row : rows) {
                columns.addAll(row.keySet());
            }

            int numColumns = columns.size();
            if (numColumns < 1) {
                return EMPTY_UPDATE_BUILDER;
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
                        values.append(SqlSyntax.SQL_NULL);
                    } else {
                        values.append(SqlSyntax.SQL_PARAMETER_PLACEHOLDER);
                        if (value instanceof Temporal) {
                            value = new Timestamp(Times.toEpochMillis((Temporal) value));
                        }

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

            return new InsertQuerySuffixBuilder(query, parameters);
        }
    }

    private class InsertQuerySuffixBuilder implements InsertSuffixBuilder {
        private final StringBuilder query;
        private final List<Object> parameters;

        public InsertQuerySuffixBuilder(StringBuilder query, List<Object> parameters) {
            this.query = query;
            this.parameters = parameters;
        }

        private UpdateQueryExecution executeQuery() {
            return QueryBuilderImpl.this.update(query.toString(), parameters.toArray(new Object[parameters.size()]));
        }

        @Override
        public Observable<Integer> count() {
            return executeQuery().count();
        }

        @Override
        public Observable<Long> getIds() {
            return executeQuery().getIds();
        }

        @Override
        public UpdateQueryExecution onDuplicateKeyUpdate(Function<RowBuilder, Map<String, ?>> rowConstructor) {
            return onDuplicateKeyUpdate(rowConstructor.apply(new RowBuilderImpl()));
        }

        @Override
        public UpdateQueryExecution onDuplicateKeyUpdate(Map<String, ?> values) {
            if (!values.isEmpty()) {
                query.append(SqlSyntax.SQL_ON_DUPLICATE_KEY_UPDATE);
                appendUpdateAssignments(query, parameters, values);
            }
            return this;
        }
    }

    private class UpdateWhereClause extends WhereBuilderImpl<UpdateQueryExecution> {
        public UpdateWhereClause(StringBuilder query) {
            super(query);
        }

        public UpdateWhereClause(StringBuilder query, List<Object> parameters) {
            super(query, parameters);
        }

        @Override
        public UpdateQueryExecution execute() {
            if (!parameters.isEmpty()) {
                return QueryBuilderImpl.this.update(query.toString(), parameters.toArray(new Object[parameters.size()]));
            } else {
                return QueryBuilderImpl.this.update(query.toString(), EMPTY_PARAMETERS);
            }
        }
    }

    private class SelectWhereClause extends WhereBuilderImpl<ResultBuilder> {
        public SelectWhereClause(StringBuilder query) {
            super(query);
        }

        @Override
        public ResultBuilder execute() {
            if (!parameters.isEmpty()) {
                return QueryBuilderImpl.this.query(query.toString(), parameters.toArray(new Object[parameters.size()]));
            } else {
                return QueryBuilderImpl.this.query(query.toString());
            }
        }
    }

    private class SelectQuery implements SelectBuilder {
        private final StringBuilder query = new StringBuilder();

        public SelectQuery() {
            query.append(SqlSyntax.SQL_SELECT);
            query.append('*');
        }

        public SelectQuery(Iterable<String> columns) {
            query.append(SqlSyntax.SQL_SELECT);
            query.append(Joiner.on(", ").join(columns));
        }

        @Override
        public SelectWhereBuilder<ResultBuilder> from(String table) {
            query.append(SqlSyntax.SQL_FROM);
            query.append(table);
            return new SelectWhereClause(query);
        }

        @Override
        public SelectWhereBuilder<ResultBuilder> from(Outline<?> outline) {
            return from(outline.pluralName());
        }
    }

    @Override
    public WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> deleteFrom(
            String table) {
        return new UpdateWhereClause(new StringBuilder(SqlSyntax.SQL_DELETE_FROM).append(table));
    }

    @Override
    public WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> deleteFrom(
            Outline<?> outline) {
        return deleteFrom(outline.pluralName());
    }

    @Override
    public WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> deleteFromNamed(
            String table) {
        return new UpdateWhereClause(new StringBuilder(SqlSyntax.SQL_DELETE).append(table).append(SqlSyntax.SQL_FROM)
                .append(table));
    }

    @Override
    public WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> deleteFromNamed(
            Outline<?> outline) {
        return deleteFromNamed(outline.pluralName());
    }

    @Override
    public InsertBuilder insertInto(String table) {
        return new InsertQuery(table);
    }

    @Override
    public InsertBuilder insertInto(Outline<?> outline) {
        return insertInto(outline.pluralName());
    }

    protected abstract ResultBuilder query(String query);
    protected abstract ResultBuilder query(String query, Object... parameters);
    protected abstract UpdateQueryExecution update(String query, Object... parameters);

    @Override
    public SelectBuilder select(String column) {
        return select(Collections.singletonList(column));
    }

    @Override
    public SelectBuilder select(Member member) {
        return selectQualified(member.outline().pluralName(), member.name());
    }

    @Override
    public SelectBuilder select(String... columns) {
        return select(Arrays.asList(columns));
    }

    @Override
    public SelectBuilder select(Member... members) {
        ArrayList<String> columns = new ArrayList<>();
        for (Member member : members) {
            columns.add(SqlSyntax.qualifiedName(member.outline().pluralName(), member.name()));
        }

        return select(columns);
    }

    @Override
    public SelectBuilder select(Iterable<String> columns) {
        return new SelectQuery(columns);
    }

    @Override
    public SelectBuilder selectQualified(String table, String column) {
        return select(SqlSyntax.qualifiedName(table, column));
    }

    @Override
    public SelectBuilder selectQualified(String table, String... columns) {
        return select(
                Stream.of(columns)
                        .map(column -> SqlSyntax.qualifiedName(table, column))
                        .collect(Collectors.toList()));
    }

    @Override
    public SelectBuilder selectQualified(String table, Iterable<String> columns) {
        return select(
                StreamSupport.stream(columns.spliterator(), false)
                        .map(column -> SqlSyntax.qualifiedName(table, column))
                        .collect(Collectors.toList()));
    }

    @Override
    public SelectBuilder selectAll() {
        return new SelectQuery();
    }

    @Override
    public SelectBuilder selectAllIn(Outline<?> outline) {
        return selectQualified(outline.pluralName(), outline.propertyNames());
    }

    @Override
    public UpdateBuilder update(String table) {
        return new UpdateQuery(table);
    }

    @Override
    public UpdateBuilder update(Outline<?> outline) {
        return update(outline.pluralName());
    }

}
