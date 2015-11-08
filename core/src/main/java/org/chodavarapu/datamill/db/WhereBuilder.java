package org.chodavarapu.datamill.db;
/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface WhereBuilder<R> {
    R all();
    ConditionBuilder<R> where();
}
