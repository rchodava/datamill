package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Outline;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SelectWhereBuilder<R> extends WhereBuilder<R, SelectLimitBuilder<R>> {
    R limit(int offset, int count);
    JoinBuilder<R> leftJoin(String table);
    JoinBuilder<R> leftJoin(Outline<?> outline);
}
