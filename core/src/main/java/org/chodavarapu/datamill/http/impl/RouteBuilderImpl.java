package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;
import org.chodavarapu.datamill.http.builder.*;
import org.chodavarapu.datamill.http.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RouteBuilderImpl implements RouteBuilder, ElseBuilder, MatchHandlerBuilder {
    private static abstract class Matcher {
        private Function<Request, Response> handler;

        public abstract boolean matches(Request request);

        public void setHandler(Function<Request, Response> handler) {
            this.handler = handler;
        }

        public void setHandler(Supplier<Response> handler) {
            this.handler = r -> handler.get();
        }

        public void setResponse(Response response) {
            this.handler = r -> response;
        }
    }

    private static class MethodMatcher extends Matcher {
        private final Method method;

        public MethodMatcher(Method method) {
            this.method = method;
        }

        @Override
        public boolean matches(Request request) {
            return request.method() == method;
        }
    }

    private static class TautologyMatcher extends Matcher {
        public TautologyMatcher(Function<Request, Response> handler) {
            setHandler(handler);
        }

        public TautologyMatcher(Supplier<Response> handler) {
            setHandler(handler);
        }

        public TautologyMatcher(Response response) {
            setResponse(response);
        }

        @Override
        public boolean matches(Request request) {
            return true;
        }
    }

    private static class UriMatcher extends Matcher {
        private final UriTemplate template;

        public UriMatcher(String template) {
            this.template = new UriTemplate(template);
        }

        @Override
        public boolean matches(Request request) {
            return template.match(request.uri()) != null;
        }
    }

    private static class MatcherBasedRoute implements Route {
        private final List<Matcher> matchers;

        public MatcherBasedRoute(List<Matcher> matchers) {
            this.matchers = matchers;
        }

        @Override
        public Response apply(Request request) {
            for (Matcher matcher : matchers) {
                if (matcher.matches(request)) {
                    return matcher.handler.apply(request);
                }
            }

            return null;
        }
    }

    private final List<Matcher> matchers = new ArrayList<>();

    private Matcher getCurrentMatcher() {
        return matchers.get(matchers.size() - 1);
    }

    @Override
    public ElseBuilder and(Function<RouteBuilder, Route> subRouteBuilder) {

        return null;
    }

    @Override
    public MatchHandlerBuilder elseIfDelete() {
        matchers.add(new MethodMatcher(Method.DELETE));
        return this;
    }

    @Override
    public MatchHandlerBuilder elseIfGet() {
        matchers.add(new MethodMatcher(Method.GET));
        return this;
    }

    @Override
    public MatchHandlerBuilder elseIfHead() {
        matchers.add(new MethodMatcher(Method.HEAD));
        return this;
    }

    @Override
    public MatchHandlerBuilder elseIfOptions() {
        matchers.add(new MethodMatcher(Method.OPTIONS));
        return this;
    }

    @Override
    public MatchHandlerBuilder elseIfPatch() {
        matchers.add(new MethodMatcher(Method.PATCH));
        return this;
    }

    @Override
    public MatchHandlerBuilder elseIfPost() {
        matchers.add(new MethodMatcher(Method.POST));
        return this;
    }

    @Override
    public MatchHandlerBuilder elseIfPut() {
        matchers.add(new MethodMatcher(Method.PUT));
        return this;
    }

    @Override
    public MatchHandlerBuilder elseIfUriMatches(String pattern) {
        matchers.add(new UriMatcher(pattern));
        return this;
    }

    @Override
    public MatchHandlerBuilder ifDelete() {
        return elseIfDelete();
    }

    @Override
    public MatchHandlerBuilder ifGet() {
        return elseIfGet();
    }

    @Override
    public MatchHandlerBuilder ifHead() {
        return elseIfHead();
    }

    @Override
    public MatchHandlerBuilder ifOptions() {
        return elseIfOptions();
    }

    @Override
    public MatchHandlerBuilder ifPatch() {
        return elseIfPatch();
    }

    @Override
    public MatchHandlerBuilder ifPost() {
        return elseIfPost();
    }

    @Override
    public MatchHandlerBuilder ifPut() {
        return elseIfPut();
    }

    @Override
    public MatchHandlerBuilder ifUriMatches(String pattern) {
        return elseIfUriMatches(pattern);
    }

    @Override
    public Route orElse(Function<Request, Response> handler) {
        matchers.add(new TautologyMatcher(handler));
        return new MatcherBasedRoute(matchers);
    }

    @Override
    public Route orElse(Supplier<Response> handler) {
        matchers.add(new TautologyMatcher(handler));
        return new MatcherBasedRoute(matchers);
    }

    @Override
    public Route orElse(Response response) {
        matchers.add(new TautologyMatcher(response));
        return new MatcherBasedRoute(matchers);
    }

    @Override
    public ElseBuilder then(Function<Request, Response> handler) {
        getCurrentMatcher().setHandler(handler);
        return this;
    }

    @Override
    public ElseBuilder then(Supplier<Response> handler) {
        getCurrentMatcher().setHandler(handler);
        return this;
    }

    @Override
    public ElseBuilder then(Response response) {
        getCurrentMatcher().setResponse(response);
        return this;
    }
}
