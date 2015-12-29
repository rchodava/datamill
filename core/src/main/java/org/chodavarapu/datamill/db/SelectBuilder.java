package org.chodavarapu.datamill.db;

import org.chodavarapu.datamill.reflection.Outline;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SelectBuilder {
    WhereBuilder<Observable<Row>> from(String table);
    WhereBuilder<Observable<Row>> from(Outline<?> outline);
}
