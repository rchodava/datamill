package org.chodavarapu.datamill.db;

import com.github.davidmoten.rx.jdbc.Database;
import com.github.davidmoten.rx.jdbc.QueryUpdate;
import org.chodavarapu.datamill.db.impl.QueryBuilderImpl;
import org.chodavarapu.datamill.db.impl.RowImpl;
import org.flywaydb.core.Flyway;
import rx.Observable;

import javax.sql.DataSource;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DatabaseClient extends QueryBuilderImpl implements QueryRunner {
    private final DataSource dataSource;
    private Database database;
    private final String password;
    private final String url;
    private final String username;

    public DatabaseClient(DataSource dataSource) {
        this.dataSource = dataSource;

        this.url = null;
        this.username = null;
        this.password = null;
    }

    public DatabaseClient(String url, String username, String password) {
        this.dataSource = null;

        this.url = url;
        this.username = username;
        this.password = password;
    }

    private Database getDatabase() {
        if (database == null) {
            if (dataSource != null) {
                database = Database.fromDataSource(dataSource);
            } else if (url != null && username != null && password != null) {
                database = Database.from(url, username, password);
            }
        }

        return database;
    }

    private Flyway getFlyway() {
        Flyway flyway = new Flyway();
        if (dataSource != null) {
            flyway.setDataSource(dataSource);
        } else {
            flyway.setDataSource(url, username, password);
        }
        return flyway;
    }

    public void clean() {
        getFlyway().clean();
    }

    public void migrate() {
        getFlyway().migrate();
    }

    @Override
    public Observable<Row> query(String sql) {
        return getDatabase().select(sql).get(resultSet -> new RowImpl(resultSet));
    }

    @Override
    public Observable<Row> query(String sql, Object... parameters) {
        return getDatabase().select(sql).parameters(parameters).get(resultSet -> new RowImpl(resultSet));
    }

    @Override
    public UpdateQueryExecution update(String sql, Object... parameters) {
        return new UpdateQueryExecutionImpl(getDatabase().update(sql).parameters(parameters));
    }

    private static class UpdateQueryExecutionImpl implements UpdateQueryExecution {
        private QueryUpdate.Builder updateBuilder;

        public UpdateQueryExecutionImpl(QueryUpdate.Builder updateBuilder) {
            this.updateBuilder = updateBuilder;
        }

        @Override
        public Observable<Integer> count() {
            return updateBuilder.count();
        }

        @Override
        public Observable<Long> getIds() {
            return updateBuilder.returnGeneratedKeys().getAs(Long.class);
        }
    }
}
