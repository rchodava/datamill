package foundation.stack.datamill.db;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface UpdateBuilder {
    WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> set(
            Map<String, ?> values);
    WhereBuilder<UpdateQueryExecution, ? extends LimitBuilder<? extends UpdateQueryExecution>> set(
            Function<RowBuilder, Map<String, ?>> rowConstructor);
}
