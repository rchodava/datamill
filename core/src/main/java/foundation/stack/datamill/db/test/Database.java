package foundation.stack.datamill.db.test;

import foundation.stack.datamill.db.Row;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Database {
    void changeCatalog(String catalog);
    void clean();
    String getURL();
    String getVersion();
    void migrate();
    Observable<Row> query(String sql, Object... parameters);
    Observable<Integer> updateAndGetAffectedCount(String sql, Object... parameters);
    Observable<Long> updateAndGetIds(String sql, Object... parameters);
}
