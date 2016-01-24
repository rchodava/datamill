package org.chodavarapu.datamill.db;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface InsertBuilder {
    <T> UpdateQueryExecution values(Collection<T> values, BiFunction<RowBuilder, T, Map<String, ?>> constructor);
    UpdateQueryExecution values(Map<String, ?>... values);
    UpdateQueryExecution row(Function<RowBuilder, Map<String, ?>> constructor);
}
