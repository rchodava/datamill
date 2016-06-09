package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class RouteMatcher implements Matcher {
    private final Route route;

    protected RouteMatcher(Route route) {
        this.route = route;
    }

    public Observable<Response> applyIfMatches(ServerRequest request) {
        if (matches(request)) {
            return getRoute().apply(request);
        }

        return null;
    }

    protected Route getRoute() {
        return route;
    }

    protected abstract boolean matches(ServerRequest request);
}
