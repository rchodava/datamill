package foundation.stack.datamill.db.impl;

import foundation.stack.datamill.db.*;
import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.reflection.Outline;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
abstract class WhereBuilderImpl<R> implements SelectWhereBuilder<R>, ConditionBuilder, ConjunctionBuilder,
        JoinBuilder<R>, SelectLimitBuilder<R>, OrderBuilder<R>, ConjoinedOrderBuilder<R> {
    protected final StringBuilder query;
    protected final List<Object> parameters;

    public WhereBuilderImpl(StringBuilder query) {
        this(query, new ArrayList<>());
    }

    public WhereBuilderImpl(StringBuilder query, List<Object> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    protected <T> ConjunctionBuilder addBinaryClause(String operator, String column, T value) {
        query.append(column);
        query.append(operator);

        if (value != null) {
            query.append(SqlSyntax.SQL_PARAMETER_PLACEHOLDER);
            parameters.add(value);
        } else {
            query.append(SqlSyntax.SQL_NULL);
        }

        return this;
    }

    protected <T> void addInClause(String column, Collection<T> values) {
        if (!values.isEmpty()) {
            query.append(column);
            query.append(SqlSyntax.SQL_IN);

            query.append(SqlSyntax.OPEN_PARENTHESIS);
            Iterator<T> iterator = values.iterator();
            while (iterator.hasNext()) {
                query.append(SqlSyntax.SQL_PARAMETER_PLACEHOLDER);
                iterator.next();
                if (iterator.hasNext()) {
                    query.append(SqlSyntax.COMMA);
                }
            }
            query.append(SqlSyntax.CLOSE_PARENTHESIS);

            parameters.addAll(values);
        }
    }

    @Override
    public R all() {
        return execute();
    }

    @Override
    public TerminalCondition and(
            Func1<ConditionBuilder, TerminalCondition> left,
            Func1<ConditionBuilder, TerminalCondition> right) {
        query.append(SqlSyntax.OPEN_PARENTHESIS);
        left.call(this);
        query.append(SqlSyntax.CLOSE_PARENTHESIS);
        query.append(SqlSyntax.SQL_AND);
        query.append(SqlSyntax.OPEN_PARENTHESIS);
        right.call(this);
        query.append(SqlSyntax.CLOSE_PARENTHESIS);

        return this;
    }

    @Override
    public ConditionBuilder and() {
        query.append(SqlSyntax.SQL_AND);
        return this;
    }

    @Override
    public OrderBuilder<R> andOrderBy(String fragment) {
        query.append(SqlSyntax.COMMA);
        query.append(' ');
        query.append(fragment);
        return this;
    }

    @Override
    public OrderBuilder<R> andOrderBy(Member member) {
        return andOrderBy(SqlSyntax.qualifiedName(member.outline().pluralName(), member.name()));
    }

    @Override
    public ConjoinedOrderBuilder<R> asc() {
        query.append(SqlSyntax.SQL_ASC);
        return this;
    }

    @Override
    public ConjoinedOrderBuilder<R> desc() {
        query.append(SqlSyntax.SQL_DESC);
        return this;
    }

    @Override
    public TerminalCondition or(
            Func1<ConditionBuilder, TerminalCondition> left,
            Func1<ConditionBuilder, TerminalCondition> right) {
        query.append(SqlSyntax.OPEN_PARENTHESIS);
        left.call(this);
        query.append(SqlSyntax.CLOSE_PARENTHESIS);
        query.append(SqlSyntax.SQL_OR);
        query.append(SqlSyntax.OPEN_PARENTHESIS);
        right.call(this);
        query.append(SqlSyntax.CLOSE_PARENTHESIS);

        return this;
    }

    @Override
    public ConditionBuilder or() {
        query.append(SqlSyntax.SQL_OR);
        return this;
    }

    @Override
    public <T> ConjunctionBuilder eq(String column, T value) {
        return addBinaryClause(SqlSyntax.SQL_EQ, column, value);
    }

    @Override
    public <T> ConjunctionBuilder eq(Member member, T value) {
        return eq(member.outline().pluralName(), member.name(), value);
    }

    @Override
    public <T> ConjunctionBuilder eq(String table, String column, T value) {
        return eq(SqlSyntax.qualifiedName(table, column), value);
    }

    protected abstract R execute();

    @Override
    public <T> ConjunctionBuilder gt(String column, T value) {
        return addBinaryClause(SqlSyntax.SQL_GREATER_THAN, column, value);
    }

    @Override
    public <T> ConjunctionBuilder gt(Member member, T value) {
        return gt(member.outline().pluralName(), member.name(), value);
    }

    @Override
    public <T> ConjunctionBuilder gt(String table, String column, T value) {
        return gt(SqlSyntax.qualifiedName(table, column), value);
    }

    @Override
    public <T> ConjunctionBuilder is(String column, T value) {
        return addBinaryClause(SqlSyntax.SQL_IS, column, value);
    }

    @Override
    public <T> ConjunctionBuilder is(String table, String column, T value) {
        return is(SqlSyntax.qualifiedName(table, column), value);
    }

    @Override
    public <T> ConjunctionBuilder is(Member member, T value) {
        return is(member.outline().pluralName(), member.name(), value);
    }

    @Override
    public <T> ConjunctionBuilder in(String table, String column, Collection<T> values) {
        return in(SqlSyntax.qualifiedName(table, column), values);
    }

    @Override
    public <T> ConjunctionBuilder in(Member member, Collection<T> values) {
        return in(member.outline().pluralName(), member.name(), values);
    }

    @Override
    public <T> ConjunctionBuilder in(String column, Collection<T> values) {
        addInClause(column, values);
        return this;
    }

    @Override
    public JoinBuilder<R> leftJoin(String table) {
        query.append(SqlSyntax.SQL_LEFT_JOIN);
        query.append(table);

        return this;
    }

    @Override
    public R limit(int count) {
        query.append(SqlSyntax.SQL_LIMIT);
        query.append(count);

        return execute();
    }

    @Override
    public R limit(int offset, int count) {
        query.append(SqlSyntax.SQL_LIMIT);
        query.append(offset);
        query.append(SqlSyntax.COMMA);
        query.append(' ');
        query.append(count);

        return execute();
    }

    @Override
    public JoinBuilder<R> leftJoin(Outline<?> outline) {
        return leftJoin(outline.pluralName());
    }

    @Override
    public <T> ConjunctionBuilder lt(String column, T value) {
        return addBinaryClause(SqlSyntax.SQL_LESS_THAN, column, value);
    }

    @Override
    public <T> ConjunctionBuilder lt(Member member, T value) {
        return lt(member.outline().pluralName(), member.name(), value);
    }

    @Override
    public <T> ConjunctionBuilder lt(String table, String column, T value) {
        return lt(SqlSyntax.qualifiedName(table, column), value);
    }

    @Override
    public SelectWhereBuilder<R> onEq(String column1, String column2) {
        query.append(SqlSyntax.SQL_ON);
        query.append(column1);
        query.append(SqlSyntax.SQL_EQ);
        query.append(column2);

        return this;
    }

    @Override
    public SelectWhereBuilder<R> onEq(String table1, String column1, String table2, String column2) {
        return onEq(SqlSyntax.qualifiedName(table1, column1), SqlSyntax.qualifiedName(table2, column2));
    }

    @Override
    public SelectWhereBuilder<R> onEq(Member member1, Member member2) {
        return onEq(member1.outline().pluralName(), member1.name(),
                member2.outline().pluralName(), member2.name());
    }

    @Override
    public SelectWhereBuilder<R> onEq(String table1, String column1, Member member2) {
        return onEq(table1, column1, member2.outline().pluralName(), member2.name());
    }

    @Override
    public OrderBuilder<R> orderBy(String fragment) {
        query.append(SqlSyntax.SQL_ORDER_BY);
        query.append(fragment);
        return this;
    }

    @Override
    public OrderBuilder<R> orderBy(Member member) {
        return orderBy(SqlSyntax.qualifiedName(member.outline().pluralName(), member.name()));
    }

    @Override
    public SelectLimitBuilder<R> where(Func1<ConditionBuilder, TerminalCondition> conditionBuilder) {
        query.append(SqlSyntax.SQL_WHERE);
        conditionBuilder.call(this);

        return this;
    }
}
