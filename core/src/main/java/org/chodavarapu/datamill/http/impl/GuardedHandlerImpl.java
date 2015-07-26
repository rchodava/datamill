package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.matching.GuardedHandler;
import org.chodavarapu.datamill.http.matching.MatchHandler;
import org.chodavarapu.datamill.http.matching.RequestMatchingChain;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class GuardedHandlerImpl implements GuardedHandler {
    private final Request request;
    private final boolean matched;

    public GuardedHandlerImpl(Request request, boolean matched) {
        this.request = request;
        this.matched = matched;
    }

    @Override
    public MatchHandler and(boolean guard) {
        return new GuardedHandlerImpl(request, matched && guard);
    }

    @Override
    public MatchHandler or(boolean guard) {
        return new GuardedHandlerImpl(request, matched || guard);
    }

    @Override
    public RequestMatchingChain then(Function<Request, Response> handler) {
        return matched ? new RequestMatcher(request, handler.apply(request)) : new RequestMatcher(request);
    }

    @Override
    public RequestMatchingChain then(Supplier<Response> handler) {
        return matched ? new RequestMatcher(request, handler.get()) : new RequestMatcher(request);
    }

    @Override
    public RequestMatchingChain then(Response response) {
        return matched ? new RequestMatcher(request, response) : new RequestMatcher(request);
    }
}
