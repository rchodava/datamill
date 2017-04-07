package foundation.stack.datamill.http.builder;

import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import foundation.stack.datamill.http.impl.RouteBuilderImpl;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class RouteBuilderTest {

    private RouteBuilder routeBuilder;

    private Route anyRoute = mock(Route.class);
    private Route orElseRoute = mock(Route.class);

    @Before
    public void setup() {
        routeBuilder = new RouteBuilderImpl();
    }

    @Test
    public void anyRouteHonoredIfObservableReturned() {
        Response anyRouteMockResponse = mock(Response.class);
        when(anyRoute.apply(any(ServerRequest.class))).thenReturn(Observable.just(anyRouteMockResponse));

        Response elseRouteMockResponse = mock(Response.class);
        when(orElseRoute.apply(any(ServerRequest.class))).thenReturn(Observable.just(elseRouteMockResponse));

        Route route = routeBuilder.any(anyRoute).orElse(orElseRoute);

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.method()).thenReturn(Method.GET);

        Observable<Response> responseObservable = route.apply(serverRequest);
        Response actualResponse = responseObservable.toBlocking().lastOrDefault(null);

        assertThat(anyRouteMockResponse, is(actualResponse));

    }

    @Test
    public void elseOfAnyRouteHonoredIfNoObservableReturned() {

        mock(Response.class);
        when(anyRoute.apply(any(ServerRequest.class))).thenReturn(null);

        Response elseRouteMockResponse = mock(Response.class);
        when(orElseRoute.apply(any(ServerRequest.class))).thenReturn(Observable.just(elseRouteMockResponse));

        Route route = routeBuilder.any(anyRoute).orElse(orElseRoute);

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.method()).thenReturn(Method.GET);

        Observable<Response> responseObservable = route.apply(serverRequest);
        Response actualResponse = responseObservable.toBlocking().lastOrDefault(null);

        assertThat(elseRouteMockResponse, is(actualResponse));
    }

}
