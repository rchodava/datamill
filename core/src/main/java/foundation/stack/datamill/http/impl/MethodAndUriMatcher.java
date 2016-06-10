package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.ServerRequest;
import foundation.stack.datamill.http.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class MethodAndUriMatcher extends RouteMatcher {
    private static final Logger logger = LoggerFactory.getLogger(MethodAndUriMatcher.class);

    private final Method method;
    private final UriTemplate uriTemplate;

    public MethodAndUriMatcher(Method method, String pattern, Route route) {
        super(route);

        this.method = method;

        if (pattern != null) {
            this.uriTemplate = new UriTemplate(pattern);
        } else {
            this.uriTemplate = null;
        }
    }

    @Override
    public boolean matches(ServerRequest request) {
        boolean matches = matchesMethod(request) && matchesUri(request);
        if (matches) {
            logger.debug("Request matched {} {}", method, uriTemplate == null ? "*" : uriTemplate);
        }

        return matches;
    }

    private boolean matchesUri(ServerRequest request) {
        if (uriTemplate != null) {
            Map<String, String> uriParameters = uriTemplate.match(request.uri());
            if (uriParameters != null) {
                if (!uriParameters.isEmpty()) {
                    ((ServerRequestImpl) request).setUriParameters(uriParameters);
                }

                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean matchesMethod(ServerRequest request) {
        return method != null ? request.method() == method : true;
    }

    @Override
    public Set<Method> queryOptions(ServerRequest request) {
        if (matchesUri(request) && request.method() == Method.OPTIONS) {
            return method == null ? Method.allMethods() : EnumSet.of(method);
        }

        return null;
    }
}
