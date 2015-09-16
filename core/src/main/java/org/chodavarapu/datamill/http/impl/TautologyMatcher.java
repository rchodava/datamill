package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
class TautologyMatcher extends Matcher {
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
    public boolean applyIfMatches(Request request) {
        return true;
    }
}
