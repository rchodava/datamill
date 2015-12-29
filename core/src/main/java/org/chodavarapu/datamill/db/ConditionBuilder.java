package org.chodavarapu.datamill.db;

import org.chodavarapu.datamill.reflection.Member;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ConditionBuilder<R> {
    <T> R eq(String column, T value);
    <T> R eq(String table, String column, T value);
    <T> R eq(Member member, T value);
}
