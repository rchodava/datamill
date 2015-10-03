package org.chodavarapu.datamill.db;

import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ConditionBuilder {
    <T> Observable<Row> eq(String column, T value);
}
