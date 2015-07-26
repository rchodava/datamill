package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.matching.GuardedHandler;
import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.matching.MethodMatcher;
import org.chodavarapu.datamill.http.matching.RequestMatchingChain;
import org.chodavarapu.datamill.http.matching.UriMatcher;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestMatcher implements RequestMatchingChain, MethodMatcher, UriMatcher {
    private final boolean matched;
    private final Request request;
    private final Response response;

    public RequestMatcher(Request request) {
        this.matched = false;

        this.request = request;
        this.response = null;
    }

    public RequestMatcher(Request request, Response response) {
        this.matched = true;

        this.request = request;
        this.response = response;
    }

    @Override
    public String get() {
        return request.servletRequest().getMethod();
    }

    @Override
    public GuardedHandler elseIfDelete() {
        return new GuardedHandlerImpl(request, isDelete());
    }

    @Override
    public GuardedHandler elseIfGet() {
        return new GuardedHandlerImpl(request, isGet());
    }

    @Override
    public GuardedHandler elseIfHead() {
        return new GuardedHandlerImpl(request, isHead());
    }

    @Override
    public GuardedHandler elseIfOptions() {
        return new GuardedHandlerImpl(request, isOptions());
    }

    @Override
    public GuardedHandler ifMatches(String pattern) {
        return elseIfUriMatches(pattern);
    }

    @Override
    public GuardedHandler elseIfPatch() {
        return new GuardedHandlerImpl(request, isPatch());
    }

    @Override
    public GuardedHandler elseIfPost() {
        return new GuardedHandlerImpl(request, isPost());
    }

    @Override
    public GuardedHandler elseIfPut() {
        return new GuardedHandlerImpl(request, isPut());
    }

    @Override
    public GuardedHandler elseIfUriMatches(String pattern) {
        return new GuardedHandlerImpl(request, matchesPattern());
    }

    @Override
    public GuardedHandler ifDelete() {
        return elseIfDelete();
    }

    @Override
    public GuardedHandler ifGet() {
        return elseIfGet();
    }

    @Override
    public GuardedHandler ifHead() {
        return elseIfHead();
    }

    @Override
    public GuardedHandler ifOptions() {
        return elseIfOptions();
    }

    @Override
    public GuardedHandler ifPatch() {
        return elseIfPatch();
    }

    @Override
    public GuardedHandler ifPost() {
        return elseIfPost();
    }

    @Override
    public GuardedHandler ifPut() {
        return elseIfPut();
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
    public Response orElse(Function<Request, Response> handler) {
        return null;
    }

    @Override
    public Response orElse(Supplier<Response> handler) {
        return null;
    }

    @Override
    public Response orElse(Response response) {
        return null;
    }
}
