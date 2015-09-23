package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import rx.Observable;

import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
class MatcherBasedRoute implements Route {
    private final List<Matcher> matchers;

    public MatcherBasedRoute(List<Matcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public Observable<Response> apply(Request request) {
        for (Matcher matcher : matchers) {
//            if (matcher.applyIfMatches(request)) {
                return matcher.getRoute().apply(request);
//            }
        }

        return Observable.empty();
    }
}
