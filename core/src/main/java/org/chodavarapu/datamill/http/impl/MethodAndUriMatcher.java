package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.ServerRequest;
import org.chodavarapu.datamill.http.Route;
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
        return matchesMethod(request) && matchesUri(request);
    }

    private boolean matchesUri(ServerRequest request) {
        if (uriTemplate != null) {
            Map<String, String> uriParameters = uriTemplate.match(request.uri());
            if (uriParameters != null) {
                if (!uriParameters.isEmpty()) {
                    ((ServerRequestImpl) request).setUriParameters(uriParameters);
                }

                logger.debug("Request matched {} {}", method, uriTemplate);
                return true;
            } else {
                return false;
            }
        } else {
            logger.debug("Request matched {} *", method);
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
