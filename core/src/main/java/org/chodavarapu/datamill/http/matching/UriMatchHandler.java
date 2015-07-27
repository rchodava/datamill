package org.chodavarapu.datamill.http.matching;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface UriMatchHandler extends GuardedHandler {
    RequestMatchingChain then(BiFunction<Request, Map<String, String>, Response> handler);
}
