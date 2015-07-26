package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.matching.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class NoopMatcher implements RequestMatchingChain, MethodMatcher, UriMatcher, GuardedHandler {
    private final Request request;
    private final Response response;

    public NoopMatcher(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public MatchHandler and(boolean guard) {
        return this;
    }

    @Override
    public String get() {
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
    public GuardedHandler ifMatches(String pattern) {
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
    public GuardedHandler elseIfUriMatches(String pattern) {
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
        return "DELETE".equals(get());
    }

    @Override
    public boolean isGet() {
        return "GET".equals(get());
    }

    @Override
    public boolean isHead() {
        return "HEAD".equals(get());
    }

    @Override
    public boolean isOptions() {
        return "OPTIONS".equals(get());
    }

    @Override
    public boolean isPatch() {
        return "PATCH".equals(get());
    }

    @Override
    public boolean isPost() {
        return "POST".equals(get());
    }

    @Override
    public boolean isPut() {
        return "PUT".equals(get());
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
        return response;
    }

    @Override
    public Response orElse(Supplier<Response> handler) {
        return response;
    }

    @Override
    public Response orElse(Response response) {
        return response;
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
