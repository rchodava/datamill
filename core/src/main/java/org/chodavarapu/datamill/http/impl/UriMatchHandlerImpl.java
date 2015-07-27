package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Request;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.matching.RequestMatchingChain;
import org.chodavarapu.datamill.http.matching.UriMatchHandler;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class UriMatchHandlerImpl extends GuardedHandlerImpl implements UriMatchHandler {
    private final Map<String, String> parameters;

    public UriMatchHandlerImpl(Request request, Map<String, String> parameters, boolean matched) {
        super(request, matched);

        this.parameters = parameters;
    }

    @Override
    public RequestMatchingChain then(BiFunction<Request, Map<String, String>, Response> handler) {
        return matched ? new NoopMatcher(request, handler.apply(request, parameters)) : new RequestMatcher(request);
    }
}
