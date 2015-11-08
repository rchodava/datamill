package org.chodavarapu.datamill.db;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface InsertBuilder {
    UpdateQueryExecution values(Map<String, ?>... values);
    UpdateQueryExecution row(Function<RowBuilder, Map<String, ?>> constructor);
}
