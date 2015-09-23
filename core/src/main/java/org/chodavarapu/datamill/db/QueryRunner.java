package org.chodavarapu.datamill.db;

import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface QueryRunner {
    Observable<Row> query(String sql);
    Observable<Row> query(String sql, Object... parameters);
}
