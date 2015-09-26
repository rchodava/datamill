package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class RouteMatcher implements Matcher {
    private final Route route;

    protected RouteMatcher(Route route) {
        this.route = route;
    }

    public Observable<Response> applyIfMatches(Request request) {
        if (matches(request)) {
            return getRoute().apply(request);
        }

        return null;
    }

    protected Route getRoute() {
        return route;
    }

    protected abstract boolean matches(Request request);
}
