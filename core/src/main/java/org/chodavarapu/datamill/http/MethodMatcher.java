package org.chodavarapu.datamill.http;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MethodMatcher {
    Method get();

    boolean isGet();
    boolean isHead();
    boolean isPost();
    boolean isPut();
    boolean isDelete();

    Optional<Response> ifGet(Function<Request, Response> handler);
    Optional<Response> ifHead(Function<Request, Response> handler);
    Optional<Response> ifPost(Function<Request, Response> handler);
    Optional<Response> ifPut(Function<Request, Response> handler);
    Optional<Response> ifDelete(Function<Request, Response> handler);
}
