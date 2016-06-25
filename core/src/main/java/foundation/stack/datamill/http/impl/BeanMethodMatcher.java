package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.annotations.*;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.reflection.Bean;
import foundation.stack.datamill.reflection.Method;
import foundation.stack.datamill.reflection.Outline;
import rx.Observable;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BeanMethodMatcher implements Matcher {
    private static String combinePaths(String path1, String path2) {
        String combined;

        if (path2 == null) {
            path2 = "";
        }

        if (path1.endsWith("/")) {
            if (path2.startsWith("/")) {
                combined = path1 + path2.substring(1);
            } else {
                combined = path1 + path2;
            }
        } else {
            if (path2.startsWith("/")) {
                combined = path1 + path2;
            } else {
                combined = path1 + "/" + path2;
            }
        }

        return combined;
    }

    private final Outline<?> beanOutline;
    private final List<RouteMatcher> matchers = new ArrayList<>();
    private final BiFunction<ServerRequest, Method, Observable<Response>> route;

    public BeanMethodMatcher(
            Bean<?> bean,
            BiFunction<ServerRequest, Method, Observable<Response>> route) {
        this.beanOutline = bean.outline();
        this.route = route;

        createBeanMethodMatchers();
    }

    private void createBeanMethodMatchers() {
        String path = null;

        Path pathAnnotation = beanOutline.getAnnotation(Path.class);
        if (pathAnnotation != null) {
            path = pathAnnotation.value();
        }

        final String beanPath = path;

        beanOutline.methods().stream().forEach(method -> {
            String methodPath = beanPath;

            Path methodPathAnnotation = method.getAnnotation(Path.class);
            if (methodPathAnnotation != null) {
                methodPath = combinePaths(beanPath == null ? "" : beanPath, methodPathAnnotation.value());
            }

            Route handler = request -> route.apply(request, method);

            if (method.hasAnnotation(DELETE.class)) {
                matchers.add(new MethodAndUriMatcher(foundation.stack.datamill.http.Method.DELETE, methodPath, handler));
            }

            if (method.hasAnnotation(GET.class)) {
                matchers.add(new MethodAndUriMatcher(foundation.stack.datamill.http.Method.GET, methodPath, handler));
            }

            if (method.hasAnnotation(HEAD.class)) {
                matchers.add(new MethodAndUriMatcher(foundation.stack.datamill.http.Method.HEAD, methodPath, handler));
            }

            if (method.hasAnnotation(OPTIONS.class)) {
                matchers.add(new MethodAndUriMatcher(foundation.stack.datamill.http.Method.OPTIONS, methodPath, handler));
            }

            if (method.hasAnnotation(PATCH.class)) {
                matchers.add(new MethodAndUriMatcher(foundation.stack.datamill.http.Method.PATCH, methodPath, handler));
            }

            if (method.hasAnnotation(POST.class)) {
                matchers.add(new MethodAndUriMatcher(foundation.stack.datamill.http.Method.POST, methodPath, handler));
            }

            if (method.hasAnnotation(PUT.class)) {
                matchers.add(new MethodAndUriMatcher(foundation.stack.datamill.http.Method.PUT, methodPath, handler));
            }
        });
    }

    @Override
    public Observable<Response> applyIfMatches(ServerRequest request) {
        for (Matcher matcher : matchers) {
            Observable<Response> responseObservable = matcher.applyIfMatches(request);
            if (responseObservable != null) {
                return responseObservable;
            }
        }

        return null;
    }

    @Override
    public Set<foundation.stack.datamill.http.Method> queryOptions(ServerRequest request) {
        EnumSet<foundation.stack.datamill.http.Method> methods = EnumSet.noneOf(foundation.stack.datamill.http.Method.class);
        for (Matcher matcher : matchers) {
            Set<foundation.stack.datamill.http.Method> matchedMethods = matcher.queryOptions(request);
            if (matchedMethods != null) {
                methods.addAll(matchedMethods);
            }
        }

        if (!methods.isEmpty()) {
            return methods;
        }

        return null;
    }
}
