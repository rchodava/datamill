package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
abstract class Matcher {
    private Route route;

    protected Matcher(Route route) {
        setRoute(route);
    }

    protected Matcher() {
    }

    protected Route getRoute() {
        return route;
    }

    public abstract Observable<Response> applyIfMatches(Request request);

    protected void setRoute(Route route) {
        this.route = route;
    }
}
