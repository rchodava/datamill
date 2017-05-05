package foundation.stack.datamill.db;

import com.github.davidmoten.rx.jdbc.*;
import com.zaxxer.hikari.HikariDataSource;
import foundation.stack.datamill.configuration.Named;
import foundation.stack.datamill.db.impl.*;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.callback.FlywayCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.functions.Action1;
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

    private static String adaptUrl(DatabaseTypeAdapter typeAdapter, String url) {
        if (typeAdapter != null) {
            DatabaseTypeAdapter.UrlTransformer urlTransformer = typeAdapter.createUrlTransformer();
            if (urlTransformer != null) {
                url = urlTransformer.transform(url);
            }
        }

        return url;
    }

    private DelegatingConnectionProvider connectionProvider;
    private DataSource dataSource;
    private Database database;
    private DatabaseTypeAdapter typeAdapter;

    public DatabaseClient(DatabaseType type, DataSource dataSource) {
        this.typeAdapter = type == DatabaseType.H2 ? new H2DatabaseTypeAdapter() : null;
        this.dataSource = dataSource;
    }

    public DatabaseClient(DatabaseType type, String url) {
        this(type, url, null, null);
    }

    public DatabaseClient(
            DatabaseType type,
            @Named("url") String url,
            @Named("username") String username,
            @Named("password") String password) {
        this.typeAdapter = type == DatabaseType.H2 ? new H2DatabaseTypeAdapter() : null;

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(adaptUrl(typeAdapter, url));

        this.dataSource = dataSource;
    }

    private void setupConnectionProvider() {
        connectionProvider = new DelegatingConnectionProvider(new ConnectionProviderFromDataSource(dataSource));
        database = Database.from(connectionProvider);
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
        flyway.setDataSource(dataSource);
        return flyway;
    }

    public void clean() {
        getFlyway().clean();
    }

    public void migrate(Action1<Connection> migrationPreparation) {
        Flyway flyway = getFlyway();
        flyway.setCallbacks(new MigrationCallback(
                typeAdapter != null ? typeAdapter.createConnectionPreparer() : null,
                migrationPreparation));

        flyway.migrate();
    }

    public void migrate() {
        migrate(null);
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

    private static class MigrationCallback implements FlywayCallback {
        private final DatabaseTypeAdapter.ConnectionPreparer connectionPreparer;
        private final Action1<Connection> migrationAction;

        public MigrationCallback(DatabaseTypeAdapter.ConnectionPreparer connectionPreparer, Action1<Connection> migrationAction) {
            this.connectionPreparer = connectionPreparer;
            this.migrationAction = migrationAction;
        }

        @Override
        public void beforeClean(Connection connection) {

        }

        @Override
        public void afterClean(Connection connection) {

        }

        @Override
        public void beforeMigrate(Connection connection) {
            if (connectionPreparer != null) {
                try {
                    connectionPreparer.prepare(connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            if (migrationAction != null) {
                migrationAction.call(connection);
            }
        }

        @Override
        public void afterMigrate(Connection connection) {

        }

        @Override
        public void beforeEachMigrate(Connection connection, MigrationInfo info) {

        }

        @Override
        public void afterEachMigrate(Connection connection, MigrationInfo info) {

        }

        @Override
        public void beforeValidate(Connection connection) {

        }

        @Override
        public void afterValidate(Connection connection) {

        }

        @Override
        public void beforeBaseline(Connection connection) {

        }

        @Override
        public void afterBaseline(Connection connection) {

        }

        @Override
        public void beforeInit(Connection connection) {

        }

        @Override
        public void afterInit(Connection connection) {

        }

        @Override
        public void beforeRepair(Connection connection) {

        }

        @Override
        public void afterRepair(Connection connection) {

        }

        @Override
        public void beforeInfo(Connection connection) {

        }

        @Override
        public void afterInfo(Connection connection) {

        }
    }
}
