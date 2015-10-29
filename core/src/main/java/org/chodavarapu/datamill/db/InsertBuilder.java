package org.chodavarapu.datamill.db;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface InsertBuilder {
    void values(Map<String, ?>... values);
    void row(Function<RowBuilder, Map<String, ?>> constructor);
}
