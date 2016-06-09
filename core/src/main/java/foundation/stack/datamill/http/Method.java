package foundation.stack.datamill.http;

import java.util.EnumSet;
import java.util.Set;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public enum Method {
    OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, PATCH, UNKNOWN;

    private static final Set<Method> methods =
            EnumSet.allOf(Method.class);

    public static Set<Method> allMethods() {
        return methods;
    }
}
