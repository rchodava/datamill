package org.chodavarapu.datamill.db;

import com.github.davidmoten.rx.jdbc.*;
import org.chodavarapu.datamill.db.impl.QueryBuilderImpl;
import org.chodavarapu.datamill.db.impl.RowImpl;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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

    public DatabaseClient(String url, String username, String password) {
        this.dataSource = null;

        this.url = url;
        this.username = username;
        this.password = password;
    }

    private Database getDatabase() {
        if (database == null) {
            if (dataSource != null) {
                connectionProvider = new DelegatingConnectionProvider(new ConnectionProviderFromDataSource(dataSource));
                database = Database.from(connectionProvider);
            } else if (url != null) {
                connectionProvider = new DelegatingConnectionProvider(new ConnectionProviderPooled(url, username, password, 0, 10));
                database = Database.from(connectionProvider);
            }
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
    public Observable<Row> query(String sql) {
        return getDatabase().select(sql).get(resultSet -> new RowImpl(resultSet));
    }

    @Override
    public Observable<Row> query(String sql, Object... parameters) {
        return getDatabase().select(sql).parameters(parameters).get(resultSet -> new RowImpl(resultSet));
    }

    @Override
    public UpdateQueryExecution update(String sql, Object... parameters) {
        return new UpdateQueryExecutionImpl(getDatabase().update(sql).parameters(Observable.from(parameters)));
    }

    public DatabaseClient changeCatalog(String catalog) {
        connectionProvider.setCatalog(catalog);
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
}
