package foundation.stack.datamill.http.impl;

import com.google.common.base.Joiner;
import foundation.stack.datamill.http.*;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class MatcherBasedRoute implements PostProcessedRoute {
    private final List<Matcher> matchers;
    private final List<Func2<Request, Response, Response>> postProcessors = new ArrayList<>();

    public MatcherBasedRoute(List<Matcher> matchers) {
        this.matchers = matchers;
    }

    @Override
    public Route andFinally(Func2<Request, Response, Response> postProcessor) {
        postProcessors.add(postProcessor);
        return this;
    }

    private Response postProcess(Request request, Response response) {
        for (Func2<Request, Response, Response> postProcessor : postProcessors) {
            response = postProcessor.call(request, response);
        }

        return response;
    }

    @Override
    public Observable<Response> apply(ServerRequest request) {
        if (request.method() == Method.OPTIONS) {
            EnumSet<Method> availableMethods = EnumSet.noneOf(Method.class);
            for (Matcher matcher : matchers) {
                Set<Method> matcherMethods = matcher.queryOptions(request);
                if (matcherMethods != null) {
                    availableMethods.addAll(matcherMethods);
                }
            }

            if (!availableMethods.isEmpty()) {
                return request.respond(b ->
                        b.header("Access-Control-Allow-Methods", Joiner.on(',').join(availableMethods))
                        .ok())
                        .map(response -> postProcess(request, response));
            }
        }

        for (Matcher matcher : matchers) {
            Observable<Response> responseObservable = matcher.applyIfMatches(request);
            if (responseObservable != null) {
                return responseObservable.map(response -> postProcess(request, response));
            }
        }

        return Observable.empty();
    }
}
