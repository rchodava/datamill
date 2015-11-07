package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.ServerRequest;
import org.chodavarapu.datamill.http.Route;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class MethodAndUriMatcher extends RouteMatcher {
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
                ((ServerRequestImpl) request).setUriParameters(uriParameters);
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
}
