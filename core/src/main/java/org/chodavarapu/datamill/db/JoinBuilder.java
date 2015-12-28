package org.chodavarapu.datamill.db;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JoinBuilder<R> {
    WhereBuilder<R> onEq(String column1, String column2);
}
