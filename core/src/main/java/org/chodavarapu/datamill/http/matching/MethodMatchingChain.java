package org.chodavarapu.datamill.http.matching;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MethodMatchingChain {
    GuardedHandler elseIfDelete();
    GuardedHandler elseIfGet();
    GuardedHandler elseIfHead();
    GuardedHandler elseIfOptions();
    GuardedHandler elseIfPatch();
    GuardedHandler elseIfPost();
    GuardedHandler elseIfPut();
}
