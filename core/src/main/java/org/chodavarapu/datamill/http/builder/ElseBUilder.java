package org.chodavarapu.datamill.http.builder;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Route;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ElseBuilder extends UriMatchingElseBuilder, MethodMatchingElseBuilder {
    Route orElse(Function<Request, Response> handler);
    Route orElse(Supplier<Response> handler);
    Route orElse(Response response);
}
