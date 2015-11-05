package org.chodavarapu.datamill.db;

import rx.Observable;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface InsertBuilder {
    Observable<Row> values(Map<String, ?>... values);
    Observable<Row> row(Function<RowBuilder, Map<String, ?>> constructor);
}
