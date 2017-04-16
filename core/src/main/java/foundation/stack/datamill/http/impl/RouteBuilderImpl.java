package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.PostProcessedRoute;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import foundation.stack.datamill.http.builder.ElseBuilder;
import foundation.stack.datamill.http.builder.RouteBuilder;
import foundation.stack.datamill.reflection.Method;
import foundation.stack.datamill.reflection.Bean;
import foundation.stack.datamill.reflection.OutlineBuilder;
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
    public <T> ElseBuilder elseIfMatchesInstanceMethod(T bean) {
        return elseIfMatchesBeanMethod(OutlineBuilder.DEFAULT.wrap(bean));
    }

    @Override
    public ElseBuilder elseIfMatchesBeanMethod(Bean<?> bean) {
        return ifMatchesBeanMethod(bean);
    }

    @Override
    public ElseBuilder elseIfMatchesBeanMethod(Bean<?> bean, BiFunction<ServerRequest, Method, Observable<Response>> route) {
        return ifMatchesBeanMethod(bean, route);
    }

    @Override
    public ElseBuilder elseIfMethodAndUriMatch(foundation.stack.datamill.http.Method method, String pattern, Route route) {
        return ifMethodAndUriMatch(method, pattern, route);
    }

    @Override
    public ElseBuilder elseIfMethodMatches(foundation.stack.datamill.http.Method method, Route route) {
        return ifMethodMatches(method, route);
    }

    @Override
    public ElseBuilder elseIfUriMatches(String pattern, Route route) {
        return ifUriMatches(pattern, route);
    }

    @Override
    public <T> ElseBuilder ifMatchesInstanceMethod(T bean) {
        return ifMatchesBeanMethod(OutlineBuilder.DEFAULT.wrap(bean));
    }

    @Override
    public ElseBuilder ifMatchesBeanMethod(Bean<?> bean) {
        return ifMatchesBeanMethod(bean,
                (request, method) ->
                        bean.<Observable<Response>, ServerRequest>invoke(method, request));
    }

    @Override
    public ElseBuilder ifMatchesBeanMethod(
            Bean<?> bean,
            BiFunction<ServerRequest, Method, Observable<Response>> route) {
        matchers.add(new BeanMethodMatcher(bean, route));
        return this;
    }

    @Override
    public ElseBuilder ifMethodAndUriMatch(foundation.stack.datamill.http.Method method, String pattern, Route route) {
        matchers.add(new MethodAndUriMatcher(method, pattern, route));
        return this;
    }

    @Override
    public ElseBuilder ifMethodMatches(foundation.stack.datamill.http.Method method, Route route) {
        matchers.add(new MethodAndUriMatcher(method, null, route));
        return this;
    }

    @Override
    public ElseBuilder ifUriMatches(String pattern, Route route) {
        matchers.add(new MethodAndUriMatcher(null, pattern, route));
        return this;
    }

    @Override
    public PostProcessedRoute orElse(Route route) {
        matchers.add(new TautologyMatcher(route));
        return new MatcherBasedRoute(matchers);
    }

    @Override
    public PostProcessedRoute orElse(Observable<Response> response) {
        matchers.add(new TautologyMatcher(response));
        return new MatcherBasedRoute(matchers);
    }

    @Override
    public PostProcessedRoute orElse(Response response) {
        matchers.add(new TautologyMatcher(response));
        return new MatcherBasedRoute(matchers);
    }

    @Override
    public ElseBuilder any(Route route) {
        matchers.add(new TautologyMatcher(route));
        return this;
    }

    @Override
    public ElseBuilder elseAny(Route route) {
        matchers.add(new TautologyMatcher(route));
        return this;
    }
}
