package org.chodavarapu.datamill.db;

import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface UpdateQueryExecution {
    Observable<Integer> count();
    Observable<Long> getIds();
}
