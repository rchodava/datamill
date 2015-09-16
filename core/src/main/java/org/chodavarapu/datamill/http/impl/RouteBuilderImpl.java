package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import org.chodavarapu.datamill.http.builder.*;
import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.reflection.Bean;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RouteBuilderImpl implements RouteBuilder, ElseBuilder {

    private final List<Matcher> matchers = new ArrayList<>();

    @Override
    public ElseBuilder elseIfMethodAndUriMatch(Method method, String pattern, Route route) {
        matchers.add(new MethodAndUriMatcher(method, pattern, route));
        return this;
    }

    @Override
    public ElseBuilder elseIfMethodMatches(Method method, Route route) {
        matchers.add(new MethodAndUriMatcher(method, null, route));
        return this;
    }

    @Override
    public ElseBuilder elseIfUriMatches(String pattern, Route route) {
        matchers.add(new MethodAndUriMatcher(null, pattern, route));
        return this;
    }

    @Override
    public ElseBuilder ifMatchesBeanMethod(
            Bean bean,
            BiFunction<Request, org.chodavarapu.datamill.reflection.Method, Observable<Response>> route) {
        return null;
    }

    @Override
    public ElseBuilder ifMethodAndUriMatch(Method method, String pattern, Route route) {
        matchers.add(new MethodAndUriMatcher(method, pattern, route));
        return this;
    }

    @Override
    public ElseBuilder ifMethodMatches(Method method, Route route) {
        matchers.add(new MethodAndUriMatcher(method, null, route));
        return this;
    }

    @Override
    public ElseBuilder ifUriMatches(String pattern, Route route) {
        matchers.add(new MethodAndUriMatcher(null, pattern, route));
        return this;
    }

    @Override
    public Route orElse(Route route) {
        matchers.add(new TautologyMatcher(route));
        return new MatcherBasedRoute(matchers);
    }

    @Override
    public Route orElse(Observable<Response> response) {
        matchers.add(new TautologyMatcher(response));
        return new MatcherBasedRoute(matchers);
    }

    @Override
    public Route orElse(Response response) {
        matchers.add(new TautologyMatcher(response));
        return new MatcherBasedRoute(matchers);
    }
}
