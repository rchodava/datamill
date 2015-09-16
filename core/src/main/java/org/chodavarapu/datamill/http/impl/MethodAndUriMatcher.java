package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
class MethodAndUriMatcher extends Matcher {
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
    public Observable<Response> applyIfMatches(Request request) {
        boolean matches = (method != null ? request.method() == method : true) &&
                (uriTemplate != null ? uriTemplate.match(request.uri()) != null : true);
        if (matches) {
            return getRoute().apply(request);
        }
        return null;
    }
}
