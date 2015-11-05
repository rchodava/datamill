package org.chodavarapu.datamill.db;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface UpdateBuilder {
    WhereBuilder set(Map<String, ?> values);
    WhereBuilder set(Function<RowBuilder, Map<String, ?>> rowConstructor);
}
