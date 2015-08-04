package org.chodavarapu.datamill.http.builder;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MatchHandlerBuilder {
    ElseBuilder and(Function<RouteBuilder, Route> subRouteBuilder);
    ElseBuilder then(Function<Request, Response> handler);
    ElseBuilder then(Supplier<Response> handler);
    ElseBuilder then(Response response);
}
