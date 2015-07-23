package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.MethodMatcher;
import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class MethodMatcherImpl implements MethodMatcher {
    private final Request request;

    public MethodMatcherImpl(Request request) {
        this.request = request;
    }

    @Override
    public String get() {
        return request.servletRequest().getMethod();
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

    @Override
    public boolean isDelete() {
        return "DELETE".equals(get());
    }

    @Override
    public Optional<Response> ifGet(Function<Request, Response> handler) {
        return isGet() ? Optional.ofNullable(handler.apply(request)) : Optional.empty();
    }

    @Override
    public Optional<Response> ifHead(Function<Request, Response> handler) {
        return isHead() ? Optional.ofNullable(handler.apply(request)) : Optional.empty();
    }

    @Override
    public Optional<Response> ifOptions(Function<Request, Response> handler) {
        return isOptions() ? Optional.ofNullable(handler.apply(request)) : Optional.empty();
    }

    @Override
    public Optional<Response> ifPatch(Function<Request, Response> handler) {
        return isPatch() ? Optional.ofNullable(handler.apply(request)) : Optional.empty();
    }


    @Override
    public Optional<Response> ifPost(Function<Request, Response> handler) {
        return isPost() ? Optional.ofNullable(handler.apply(request)) : Optional.empty();
    }

    @Override
    public Optional<Response> ifPut(Function<Request, Response> handler) {
        return isPut() ? Optional.ofNullable(handler.apply(request)) : Optional.empty();
    }

    @Override
    public Optional<Response> ifDelete(Function<Request, Response> handler) {
        return isDelete() ? Optional.ofNullable(handler.apply(request)) : Optional.empty();
    }
}
