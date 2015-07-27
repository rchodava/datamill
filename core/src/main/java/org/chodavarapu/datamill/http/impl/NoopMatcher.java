package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.matching.*;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class NoopMatcher implements RequestMatchingChain, MethodMatcher, UriMatcher, UriMatchHandler {
    private final Request request;
    private final Response matchedResponse;

    public NoopMatcher(Request request, Response response) {
        this.request = request;
        this.matchedResponse = response;
    }

    @Override
    public MatchHandler and(boolean guard) {
        return this;
    }

    @Override
    public String get() {
        return request.servletRequest().getRequestURI();
    }

    @Override
    public String name() {
        return request.servletRequest().getMethod();
    }

    @Override
    public GuardedHandler elseIfDelete() {
        return this;
    }

    @Override
    public GuardedHandler elseIfGet() {
        return this;
    }

    @Override
    public GuardedHandler elseIfHead() {
        return this;
    }

    @Override
    public GuardedHandler elseIfOptions() {
        return this;
    }

    @Override
    public UriMatchHandler ifMatches(String pattern) {
        return this;
    }

    @Override
    public GuardedHandler elseIfPatch() {
        return this;
    }

    @Override
    public GuardedHandler elseIfPost() {
        return this;
    }

    @Override
    public GuardedHandler elseIfPut() {
        return this;
    }

    @Override
    public UriMatchHandler elseIfUriMatches(String pattern) {
        return this;
    }

    @Override
    public GuardedHandler ifDelete() {
        return this;
    }

    @Override
    public GuardedHandler ifGet() {
        return this;
    }

    @Override
    public GuardedHandler ifHead() {
        return this;
    }

    @Override
    public GuardedHandler ifOptions() {
        return this;
    }

    @Override
    public GuardedHandler ifPatch() {
        return this;
    }

    @Override
    public GuardedHandler ifPost() {
        return this;
    }

    @Override
    public GuardedHandler ifPut() {
        return this;
    }

    @Override
    public boolean isDelete() {
        return "DELETE".equals(name());
    }

    @Override
    public boolean isGet() {
        return "GET".equals(name());
    }

    @Override
    public boolean isHead() {
        return "HEAD".equals(name());
    }

    @Override
    public boolean isOptions() {
        return "OPTIONS".equals(name());
    }

    @Override
    public boolean isPatch() {
        return "PATCH".equals(name());
    }

    @Override
    public boolean isPost() {
        return "POST".equals(name());
    }

    @Override
    public boolean isPut() {
        return "PUT".equals(name());
    }

    private boolean matchesPattern() {
        return false;
    }

    @Override
    public MatchHandler or(boolean guard) {
        return this;
    }

    @Override
    public Response orElse(Function<Request, Response> handler) {
        return matchedResponse;
    }

    @Override
    public Response orElse(Supplier<Response> handler) {
        return matchedResponse;
    }

    @Override
    public Response orElse(Response response) {
        return matchedResponse;
    }

    @Override
    public RequestMatchingChain then(BiFunction<Request, Map<String, String>, Response> handler) {
        return this;
    }

    @Override
    public RequestMatchingChain then(Function<Request, Response> handler) {
        return this;
    }

    @Override
    public RequestMatchingChain then(Supplier<Response> handler) {
        return this;
    }

    @Override
    public RequestMatchingChain then(Response response) {
        return this;
    }
}
