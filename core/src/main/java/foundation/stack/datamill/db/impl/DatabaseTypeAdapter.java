package foundation.stack.datamill.db.impl;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface DatabaseTypeAdapter {
    interface ConnectionPreparer {
        void prepare(Connection connection) throws SQLException;
    }

    interface UrlTransformer {
        String transform(String url);
    }

    ConnectionPreparer createConnectionPreparer();
    UrlTransformer createUrlTransformer();
}
