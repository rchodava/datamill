package org.chodavarapu.datamill.http.builder;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MethodMatchingBuilder {
    MatchHandlerBuilder ifDelete();
    MatchHandlerBuilder ifGet();
    MatchHandlerBuilder ifHead();
    MatchHandlerBuilder ifOptions();
    MatchHandlerBuilder ifPatch();
    MatchHandlerBuilder ifPost();
    MatchHandlerBuilder ifPut();
}
