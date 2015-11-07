package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.ServerRequest;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.reflection.Bean;
import org.chodavarapu.datamill.reflection.Method;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BeanMethodMatcher implements Matcher {
    private final Bean<?> bean;
    private final List<RouteMatcher> matchers = new ArrayList<>();

    public BeanMethodMatcher(Bean<?> bean, BiFunction<ServerRequest, Method, Observable<Response>> route) {
//        setRoute(request -> routeToBeanMethod(request));
        this.bean = bean;
    }

    private Observable<Response> routeToBeanMethod(ServerRequest request) {
        return Observable.just(null);
    }

    private void createBeanMethodMatchers(Bean<?> bean) {
//        bean.methods().stream().forEach(method -> {
//            if (method.hasAnnotation(DELETE.class)) {
//
//            } else if (method.hasAnnotation(GET.class)) {
//
//            } else if (method.hasAnnotation(HEAD.class)) {
//
//            } else if (method.hasAnnotation(OPTIONS.class)) {
//
//            } else if (method.hasAnnotation(PATCH.class)) {
//
//            } else if (method.hasAnnotation(POST.class)) {
//
//            } else if (method.hasAnnotation(PUT.class)) {
//
//            }
//        });
    }

    @Override
    public Observable<Response> applyIfMatches(ServerRequest request) {
        return null;
    }
}
