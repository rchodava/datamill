package org.chodavarapu.datamill.http.builder;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MethodMatchingElseBuilder {
    MatchHandlerBuilder elseIfDelete();
    MatchHandlerBuilder elseIfGet();
    MatchHandlerBuilder elseIfHead();
    MatchHandlerBuilder elseIfOptions();
    MatchHandlerBuilder elseIfPatch();
    MatchHandlerBuilder elseIfPost();
    MatchHandlerBuilder elseIfPut();
}
