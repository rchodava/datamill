package foundation.stack.datamill.db.impl;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class H2DatabaseTypeAdapter implements DatabaseTypeAdapter {
    @Override
    public ConnectionPreparer createConnectionPreparer() {
        return new H2ConnectionPreparer();
    }

    @Override
    public UrlTransformer createUrlTransformer() {
        return new H2UrlTransformer();
    }

    static class H2ConnectionPreparer implements ConnectionPreparer {
        @Override
        public void prepare(Connection connection) throws SQLException {
            connection.prepareStatement("CREATE SCHEMA IF NOT EXISTS \"public\"").execute();
            FunctionsMySQL.register(connection);
        }
    }

    static class H2UrlTransformer implements UrlTransformer {
        private static boolean isH2Url(URI uri) {
            String scheme = uri.getScheme();
            if (scheme != null) {
                if (scheme.contains("h2")) {
                    return true;
                } else if (scheme.contains("jdbc")) {
                    String schemeSpecificPart = uri.getSchemeSpecificPart();
                    return schemeSpecificPart != null && schemeSpecificPart.startsWith("h2");
                }
            }

            return false;
        }

        private static boolean isMySqlModeSpecified(String[] options) {
            for (String option : options) {
                int keySeparator = option.indexOf('=');
                if (keySeparator > 0) {
                    String key = option.substring(0, keySeparator);
                    if ("mode".equalsIgnoreCase(key.trim())) {
                        String value = option.substring(keySeparator + 1);
                        if ("mysql".equalsIgnoreCase(value.trim())) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        @Override
        public String transform(String uri) {
            if (uri != null) {
                try {
                    URI parsed = URI.create(uri);
                    if (isH2Url(parsed)) {
                        String schemeSpecificPart = parsed.getSchemeSpecificPart();
                        String[] options = schemeSpecificPart.split(";");
                        if (options != null) {
                            if (!isMySqlModeSpecified(options)) {
                                return uri + ";MODE=MySQL";
                            }
                        }
                    }

                } catch (IllegalArgumentException e) {
                }
            }

            return uri;
        }
    }
}
