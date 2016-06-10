package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Outline;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface WhereBuilder<R> {
    R all();
    R execute();
    ConditionBuilder<R> where();
    ConditionBuilder<R> and();
    JoinBuilder<R> leftJoin(String table);
    JoinBuilder<R> leftJoin(Outline<?> outline);
}
