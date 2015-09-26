package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import rx.Observable;

import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class MatcherBasedRoute implements Route {
    private final List<Matcher> matchers;

    public MatcherBasedRoute(List<Matcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public Observable<Response> apply(Request request) {
        for (Matcher matcher : matchers) {
            Observable<Response> responseObservable = matcher.applyIfMatches(request);
            if (responseObservable != null) {
                return responseObservable;
            }
        }

        return Observable.empty();
    }
}
