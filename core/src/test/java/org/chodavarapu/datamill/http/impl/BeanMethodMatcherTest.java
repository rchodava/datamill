package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.ServerRequest;
import org.chodavarapu.datamill.http.annotations.*;
import org.chodavarapu.datamill.reflection.OutlineBuilder;
import org.junit.Test;
import rx.Observable;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class BeanMethodMatcherTest {
    private static class TestBean {
        @DELETE
        public Observable<Response> delete() {
            return new ResponseBuilderImpl().ok("DELETE");
        }

        @GET
        public Observable<Response> get() {
            return new ResponseBuilderImpl().ok("GET");
        }

        @HEAD
        public Observable<Response> head() {
            return new ResponseBuilderImpl().ok("HEAD");
        }

        @OPTIONS
        public Observable<Response> options() {
            return new ResponseBuilderImpl().ok("OPTIONS");
        }

        @PATCH
        public Observable<Response> patch() {
            return new ResponseBuilderImpl().ok("PATCH");
        }

        @POST
        public Observable<Response> post() {
            return new ResponseBuilderImpl().ok("POST");
        }

        @PUT
        public Observable<Response> put() {
            return new ResponseBuilderImpl().ok("PUT");
        }
    }

    @Path("/test")
    private static class TestBeanWithPaths {
        @Path("delete")
        @DELETE
        public Observable<Response> delete() {
            return new ResponseBuilderImpl().ok("DELETE");
        }

        @Path("get/")
        @GET
        public Observable<Response> get() {
            return new ResponseBuilderImpl().ok("GET");
        }

        @Path("/head/")
        @HEAD
        public Observable<Response> head() {
            return new ResponseBuilderImpl().ok("HEAD");
        }

        @Path("/options")
        @OPTIONS
        public Observable<Response> options() {
            return new ResponseBuilderImpl().ok("OPTIONS");
        }

        @Path("")
        @PATCH
        public Observable<Response> patch() {
            return new ResponseBuilderImpl().ok("PATCH");
        }

        @Path("/")
        @POST
        public Observable<Response> post() {
            return new ResponseBuilderImpl().ok("POST");
        }
    }

    private static class TestBeanWithOnlyMethodPaths {
        @POST
        @Path("/post")
        public Observable<Response> post() {
            return new ResponseBuilderImpl().ok("POST");
        }
    }

    @Path("/test")
    private static class TestBeanWithOnlyBeanPath {
        @POST
        public Observable<Response> post() {
            return new ResponseBuilderImpl().ok("POST");
        }

        @GET
        public Observable<Response> get() {
            return new ResponseBuilderImpl().ok("GET");
        }
    }

    private static String responseBodyAsString(Observable<Response> responseObservable) {
        return responseObservable.toBlocking().last().entity().asString().toBlocking().last();
    }

    @Test
    public void beanMethodMatching() {
        TestBean bean = new TestBean();
        BeanMethodMatcher matcher = new BeanMethodMatcher(
                new OutlineBuilder().wrap(bean),
                (request, method) -> method.invoke(bean));

        ServerRequest request = mock(ServerRequest.class);

        when(request.method()).thenReturn(Method.DELETE);
        assertEquals("DELETE", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.GET);
        assertEquals("GET", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.HEAD);
        assertEquals("HEAD", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.OPTIONS);
        assertEquals("OPTIONS", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.PATCH);
        assertEquals("PATCH", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.POST);
        assertEquals("POST", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.PUT);
        assertEquals("PUT", responseBodyAsString(matcher.applyIfMatches(request)));
    }

    @Test
    public void beanMethodMatchingWithPaths() {
        TestBeanWithPaths bean = new TestBeanWithPaths();
        BeanMethodMatcher matcher = new BeanMethodMatcher(
                new OutlineBuilder().wrap(bean),
                (request, method) -> method.invoke(bean));

        ServerRequest request = mock(ServerRequest.class);

        when(request.method()).thenReturn(Method.DELETE);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.DELETE);
        when(request.uri()).thenReturn("/test/delete");
        assertEquals("DELETE", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.GET);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.GET);
        when(request.uri()).thenReturn("/test/get");
        assertEquals("GET", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.HEAD);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.HEAD);
        when(request.uri()).thenReturn("/test/head");
        assertEquals("HEAD", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.OPTIONS);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.OPTIONS);
        when(request.uri()).thenReturn("/test/options");
        assertEquals("OPTIONS", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.PATCH);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.PATCH);
        when(request.uri()).thenReturn("/test/");
        assertEquals("PATCH", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.POST);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.POST);
        when(request.uri()).thenReturn("/test/");
        assertEquals("POST", responseBodyAsString(matcher.applyIfMatches(request)));
    }

    @Test
    public void beanMethodMatchingWithOnlyMethodPaths() {
        TestBeanWithOnlyMethodPaths bean = new TestBeanWithOnlyMethodPaths();
        BeanMethodMatcher matcher = new BeanMethodMatcher(
                new OutlineBuilder().wrap(bean),
                (request, method) -> method.invoke(bean));

        ServerRequest request = mock(ServerRequest.class);

        when(request.method()).thenReturn(Method.POST);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.POST);
        when(request.uri()).thenReturn("/post");
        assertEquals("POST", responseBodyAsString(matcher.applyIfMatches(request)));
    }

    @Test
    public void beanMethodMatchingWithOnlyBeanPath() {
        TestBeanWithOnlyBeanPath bean = new TestBeanWithOnlyBeanPath();
        BeanMethodMatcher matcher = new BeanMethodMatcher(
                new OutlineBuilder().wrap(bean),
                (request, method) -> method.invoke(bean));

        ServerRequest request = mock(ServerRequest.class);

        when(request.method()).thenReturn(Method.POST);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.POST);
        when(request.uri()).thenReturn("/test");
        assertEquals("POST", responseBodyAsString(matcher.applyIfMatches(request)));

        when(request.method()).thenReturn(Method.GET);
        when(request.uri()).thenReturn("/abc");
        assertEquals(null, matcher.applyIfMatches(request));
        when(request.method()).thenReturn(Method.GET);
        when(request.uri()).thenReturn("/test");
        assertEquals("GET", responseBodyAsString(matcher.applyIfMatches(request)));
    }

    @Test
    public void queryOptions() {
        TestBeanWithOnlyBeanPath bean = new TestBeanWithOnlyBeanPath();
        BeanMethodMatcher matcher = new BeanMethodMatcher(
                new OutlineBuilder().wrap(bean),
                (request, method) -> method.invoke(bean));

        ServerRequest request = mock(ServerRequest.class);

        when(request.method()).thenReturn(Method.OPTIONS);
        when(request.uri()).thenReturn("/test");
        assertThat(matcher.queryOptions(request), hasItems(Method.GET, Method.POST));

        when(request.method()).thenReturn(Method.OPTIONS);
        when(request.uri()).thenReturn("/abc");
        assertNull(matcher.queryOptions(request));
    }
}
