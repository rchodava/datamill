package org.chodavarapu.datamill.http;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MethodMatcher {
    String get();

    boolean isDelete();
    boolean isGet();
    boolean isHead();
    boolean isOptions();
    boolean isPatch();
    boolean isPost();
    boolean isPut();

    Optional<Response> ifDelete(Function<Request, Response> handler);
    Optional<Response> ifGet(Function<Request, Response> handler);
    Optional<Response> ifHead(Function<Request, Response> handler);
    Optional<Response> ifOptions(Function<Request, Response> handler);
    Optional<Response> ifPatch(Function<Request, Response> handler);
    Optional<Response> ifPost(Function<Request, Response> handler);
    Optional<Response> ifPut(Function<Request, Response> handler);
}
