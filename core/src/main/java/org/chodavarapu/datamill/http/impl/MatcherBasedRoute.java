package org.chodavarapu.datamill.http.impl;

import com.google.common.base.Joiner;
import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.ServerRequest;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import rx.Observable;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class MatcherBasedRoute implements Route {
    private final List<Matcher> matchers;

    public MatcherBasedRoute(List<Matcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public Observable<Response> apply(ServerRequest request) {
        if (request.method() == Method.OPTIONS) {
            EnumSet<Method> availableMethods = EnumSet.noneOf(Method.class);
            for (Matcher matcher : matchers) {
                Set<Method> matcherMethods = matcher.queryOptions(request);
                if (matcherMethods != null) {
                    availableMethods.addAll(matcherMethods);
                }
            }

            if (availableMethods.size() > 0) {
                return request.respond(b ->
                        b.header("Allow", Joiner.on(',').join(availableMethods))
                        .header("Access-Control-Allow-Headers", "Authorization")
                        .header("Access-Control-Allow-Origin", "*")
                        .ok());
            }
        }

        for (Matcher matcher : matchers) {
            Observable<Response> responseObservable = matcher.applyIfMatches(request);
            if (responseObservable != null) {
                return responseObservable;
            }
        }

        return Observable.empty();
    }
}
