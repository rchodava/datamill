package org.chodavarapu.datamill.db;

import com.github.davidmoten.rx.jdbc.Database;
import org.chodavarapu.datamill.db.impl.QueryBuilderImpl;
import org.chodavarapu.datamill.db.impl.RowImpl;
import rx.Observable;

import javax.sql.DataSource;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Client extends QueryBuilderImpl implements QueryRunner {
    private final Database database;

    public Client(DataSource dataSource) {
        database = Database.fromDataSource(dataSource);
    }

    public Client(String url, String username, String password) {
        database = Database.from(url, username, password);
    }

    @Override
    public Observable<Row> query(String sql) {
        return database.select(sql).get(resultSet -> new RowImpl(resultSet));
    }

    @Override
    public Observable<Row> query(String sql, Object... parameters) {
        return database.select(sql).parameters(parameters).get(resultSet -> new RowImpl(resultSet));
    }
}
