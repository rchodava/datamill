package org.chodavarapu.datamill.db;

import org.chodavarapu.datamill.reflection.Member;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ConditionBuilder<R> {
    <T> WhereBuilder<R> eq(String column, T value);
    <T> WhereBuilder<R> eq(String table, String column, T value);
    <T> WhereBuilder<R> eq(Member member, T value);

    <T> WhereBuilder<R> is(String column, T value);
    <T> WhereBuilder<R> is(String table, String column, T value);
    <T> WhereBuilder<R> is(Member member, T value);
}
