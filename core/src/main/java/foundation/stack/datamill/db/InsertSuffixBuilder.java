package foundation.stack.datamill.db;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface InsertSuffixBuilder extends UpdateQueryExecution {
    UpdateQueryExecution onDuplicateKeyUpdate(Map<String, ?> values);
    UpdateQueryExecution onDuplicateKeyUpdate(Function<RowBuilder, Map<String, ?>> rowConstructor);
}
