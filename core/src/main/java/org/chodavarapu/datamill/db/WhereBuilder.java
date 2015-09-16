package org.chodavarapu.datamill.db;

import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface WhereBuilder {
    Observable<Row> all();
}
