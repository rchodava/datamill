package foundation.stack.datamill.db.test;

import foundation.stack.datamill.db.DatabaseClient;
import foundation.stack.datamill.db.Row;
import foundation.stack.datamill.db.UpdateQueryExecution;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class TestDatabaseClient extends DatabaseClient {
    private final Database database;

    public TestDatabaseClient(Database database) {
        super((String) null);

        this.database = database;
    }

    @Override
    public void clean() {
        database.clean();
    }

    @Override
    public String getVersion() {
        return database.getVersion();
    }

    @Override
    public String getURL() {
        return database.getURL();
    }

    @Override
    public void migrate() {
        database.migrate();
    }

    @Override
    public Observable<Row> query(String sql) {
        return database.query(sql);
    }

    @Override
    public Observable<Row> query(String sql, Object... parameters) {
        return database.query(sql, parameters);
    }

    @Override
    public DatabaseClient changeCatalog(String catalog) {
        database.changeCatalog(catalog);
        return this;
    }

    @Override
    public UpdateQueryExecution update(String sql, Object... parameters) {
        return new UpdateQueryExecution() {
            @Override
            public Observable<Integer> count() {
                return database.updateAndGetAffectedCount(sql, parameters);
            }

            @Override
            public Observable<Long> getIds() {
                return database.updateAndGetIds(sql, parameters);
            }
        };
    }
}
