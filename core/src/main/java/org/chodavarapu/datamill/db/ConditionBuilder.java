package org.chodavarapu.datamill.db;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ConditionBuilder<R> {
    <T> R eq(String column, T value);
    <T> R eq(String table, String column, T value);
}
