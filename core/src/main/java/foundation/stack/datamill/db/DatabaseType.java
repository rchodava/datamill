package foundation.stack.datamill.db;

import java.net.URI;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public enum DatabaseType {
    MYSQL,
    H2;

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

    private static boolean isH2Url(String uri) {
        if (uri != null) {
            try {
                URI parsed = URI.create(uri);
                return isH2Url(parsed);
            } catch (IllegalArgumentException e) {
            }
        }

        return false;
    }

    public static DatabaseType guess(String uri) {
        if (isH2Url(uri)) {
            return H2;
        }

        return MYSQL;
    }
}
