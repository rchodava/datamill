package org.chodavarapu.datamill.http.matching;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RequestMatchingChain extends UriMatchingChain, MethodMatchingChain {
    Response orElse(Function<Request, Response> handler);
    Response orElse(Supplier<Response> handler);
    Response orElse(Response response);
}
