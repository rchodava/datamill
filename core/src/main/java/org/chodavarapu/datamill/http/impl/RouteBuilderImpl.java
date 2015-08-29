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
    private static abstract class Matcher {
        private Route route;

        public abstract boolean matches(Request request);

        protected Matcher(Route route) {
            this.route = route;
        }
    }

    private static class MethodAndUriMatcher extends Matcher {
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
        public boolean matches(Request request) {
            return (method != null ? request.method() == method : true) &&
                    (uriTemplate != null ? uriTemplate.match(request.uri()) != null : true);
        }
    }

    private static class TautologyMatcher extends Matcher {
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
        public boolean matches(Request request) {
            return true;
        }
    }

    private static class MatcherBasedRoute implements Route {
        private final List<Matcher> matchers;

        public MatcherBasedRoute(List<Matcher> matchers) {
            this.matchers = matchers;
        }

        @Override
        public Observable<Response> apply(Request request) {
            for (Matcher matcher : matchers) {
                if (matcher.matches(request)) {
                    return matcher.route.apply(request);
                }
            }

            return Observable.empty();
        }
    }

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
    public ElseBuilder ifMatchesBeanMethod(Bean bean, BiFunction<Request, Method, Observable<Response>> route) {
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
