package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Request;
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
    public boolean matches(Request request) {
        return matchesMethod(request) && matchesUri(request);
    }

    private boolean matchesUri(Request request) {
        if (uriTemplate != null) {
            Map<String, String> uriParameters = uriTemplate.match(request.uri());
            if (uriParameters != null) {
                ((RequestImpl) request).setUriParameters(uriParameters);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean matchesMethod(Request request) {
        return method != null ? request.method() == method : true;
    }
}
