package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import rx.Observable;

import java.util.Set;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class TautologyMatcher extends RouteMatcher {
    public TautologyMatcher(Route route) {
        super(route);
    }

    public TautologyMatcher(Observable<Response> response) {
        super(r -> response);
    }

    public TautologyMatcher(Response response) {
        super(r -> Observable.just(response));
    }

    @Override
    protected boolean matches(ServerRequest request) {
        return true;
    }

    @Override
    public Set<Method> queryOptions(ServerRequest request) {
        return null;
    }
}
