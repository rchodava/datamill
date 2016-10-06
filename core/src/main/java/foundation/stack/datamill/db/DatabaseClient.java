package foundation.stack.datamill.db;

import com.github.davidmoten.rx.jdbc.*;
import foundation.stack.datamill.configuration.Named;
import foundation.stack.datamill.db.impl.QueryBuilderImpl;
import foundation.stack.datamill.db.impl.RowImpl;
import foundation.stack.datamill.db.impl.UnsubscribeOnNextOperator;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Func1;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DatabaseClient extends QueryBuilderImpl implements QueryRunner {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseClient.class);

    private DelegatingConnectionProvider connectionProvider;
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

    public DatabaseClient(String url) {
        this(url, null, null);
    }

    public DatabaseClient(@Named("url") String url, @Named("username") String username, @Named("password") String password) {
        this.dataSource = null;

        this.url = url;
        this.username = username;
        this.password = password;
    }

    private void setupConnectionProvider() {
        if (dataSource != null) {
            connectionProvider = new DelegatingConnectionProvider(new ConnectionProviderFromDataSource(dataSource));
            database = Database.from(connectionProvider);
        } else if (url != null) {
            connectionProvider = new DelegatingConnectionProvider(new ConnectionProviderPooled(url, username, password, 0, 10));
            database = Database.from(connectionProvider);
        }
    }

    private DelegatingConnectionProvider getConnectionProvider() {
        if (connectionProvider == null) {
            setupConnectionProvider();
        }

        return connectionProvider;
    }

    private Database getDatabase() {
        if (database == null) {
            setupConnectionProvider();
        }

        return database;
    }

    public String getVersion() {
        try (Connection connection = getDatabase().getConnectionProvider().get()) {
            StringBuilder vendor = new StringBuilder();
            vendor.append(connection.getMetaData().getDatabaseProductName());
            vendor.append(' ');
            vendor.append(connection.getMetaData().getDatabaseProductVersion());
            return vendor.toString();
        } catch (SQLException e) {
            logger.debug("Error retrieving database version information", e);
            return null;
        }
    }

    public String getURL() {
        try (Connection connection = getDatabase().getConnectionProvider().get()) {
            return connection.getMetaData().getURL();
        } catch (SQLException e) {
            logger.debug("Error retrieving database connection URL", e);
            return null;
        }
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
    public ResultBuilder query(String sql) {
        return new Results(getDatabase().select(sql).get(resultSet -> new RowImpl(resultSet)));
    }

    @Override
    public ResultBuilder query(String sql, Object... parameters) {
        return new Results(getDatabase().select(sql).parameters(parameters).get(resultSet -> new RowImpl(resultSet)));
    }

    @Override
    public UpdateQueryExecution update(String sql, Object... parameters) {
        return new UpdateQueryExecutionImpl(getDatabase().update(sql).parameters(Observable.from(parameters)));
    }

    public DatabaseClient changeCatalog(String catalog) {
        getConnectionProvider().setCatalog(catalog);
        return this;
    }

    private static class UpdateQueryExecutionImpl implements UpdateQueryExecution {
        private static final Logger logger = LoggerFactory.getLogger(UpdateQueryExecutionImpl.class);

        private QueryUpdate.Builder updateBuilder;

        public UpdateQueryExecutionImpl(QueryUpdate.Builder updateBuilder) {
            this.updateBuilder = updateBuilder;
        }

        @Override
        public Observable<Integer> count() {
            return updateBuilder.count()
                    .doOnError(t -> logger.error("Error executing update statement!", t));
        }

        @Override
        public Observable<Long> getIds() {
            return updateBuilder.returnGeneratedKeys().getAs(Long.class)
                    .doOnError(t -> logger.error("Error executing update statement!", t));
        }
    }

    private static class DelegatingConnectionProvider implements ConnectionProvider {
        private final ConnectionProvider wrapped;
        private String catalog;

        public DelegatingConnectionProvider(ConnectionProvider wrapped) {
            this.wrapped = wrapped;
        }

        public void setCatalog(String catalog) {
            this.catalog = catalog;
        }

        @Override
        public Connection get() {
            if (catalog != null) {
                Connection connection = wrapped.get();
                try {
                    connection.setCatalog(catalog);
                } catch (SQLException e) {
                    logger.debug("Failed to set catalog to {} on SQL connection", catalog);
                }
                return connection;
            } else {
                return wrapped.get();
            }
        }

        @Override
        public void close() {
            wrapped.close();
        }
    }

    private static class Results implements ResultBuilder {
        private final Observable<Row> results;

        public Results(Observable<Row> results) {
            this.results = results;
        }

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
            return results;
        }
    }
}
