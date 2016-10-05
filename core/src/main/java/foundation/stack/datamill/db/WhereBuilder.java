package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Outline;
import rx.functions.Func1;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface WhereBuilder<R, L extends LimitBuilder<R>> {
    R all();
    R limit(int count);
    JoinBuilder<R> leftJoin(String table);
    JoinBuilder<R> leftJoin(Outline<?> outline);
    L where(Func1<ConditionBuilder, TerminalCondition> conditionBuilder);
}
