package org.chodavarapu.datamill.http.matching;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MatchHandler {
    RequestMatchingChain then(Function<Request, Response> handler);
    RequestMatchingChain then(Supplier<Response> handler);
    RequestMatchingChain then(Response response);
}
