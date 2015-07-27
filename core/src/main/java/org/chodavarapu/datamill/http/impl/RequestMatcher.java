package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.matching.*;
import org.chodavarapu.datamill.http.Request;

import java.util.Map;
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
        return request.servletRequest().getRequestURI();
    }

    @Override
    public String name() {
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
    public UriMatchHandler ifMatches(String pattern) {
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
    public UriMatchHandler elseIfUriMatches(String pattern) {
        UriTemplate template = new UriTemplate(pattern);
        Map<String, String> matchedParameters = template.match(request.uri().get());
        return new UriMatchHandlerImpl(request, matchedParameters, matchedParameters != null);
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
    public Response orElse(Function<Request, Response> handler) {
        return matched ? response : handler.apply(request);
    }

    @Override
    public Response orElse(Supplier<Response> handler) {
        return matched ? response : handler.get();
    }

    @Override
    public Response orElse(Response response) {
        return matched ? this.response : response;
    }
}
