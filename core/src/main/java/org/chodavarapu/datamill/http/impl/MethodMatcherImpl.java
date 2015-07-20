package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
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
    public Method get() {
        return Method.valueOf(request.servletRequest().getMethod());
    }

    @Override
    public boolean isGet() {
        return get() == Method.GET;
    }

    @Override
    public boolean isHead() {
        return get() == Method.HEAD;
    }

    @Override
    public boolean isPost() {
        return get() == Method.POST;
    }

    @Override
    public boolean isPut() {
        return get() == Method.PUT;
    }

    @Override
    public boolean isDelete() {
        return get() == Method.DELETE;
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
