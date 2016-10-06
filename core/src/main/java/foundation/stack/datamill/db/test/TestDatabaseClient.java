package foundation.stack.datamill.db.test;

import foundation.stack.datamill.db.DatabaseClient;
import foundation.stack.datamill.db.ResultBuilder;
import foundation.stack.datamill.db.Row;
import foundation.stack.datamill.db.UpdateQueryExecution;
import foundation.stack.datamill.db.impl.UnsubscribeOnNextOperator;
import rx.Observable;
import rx.functions.Func1;

import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class TestDatabaseClient extends DatabaseClient {
    private static final Object[] EMPTY_ARRAY = new Object[0];

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
    public ResultBuilder query(String sql) {
        return this.query(sql, EMPTY_ARRAY);
    }

    @Override
    public ResultBuilder query(String sql, Object... parameters) {
        return new ResultBuilder() {
            @Override
            public <T> Observable<List<T>> getAs(Func1<Row, T> transformer) {
                return stream().map(transformer).toList();
            }

            @Override
            public <T> Observable<T> firstAs(Func1<Row, T> transformer) {
                return stream().map(transformer).take(1).lift(new UnsubscribeOnNextOperator<>());
            }

            @Override
            public Observable<Row> stream() {
                return database.query(sql, parameters);
            }
        };
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
