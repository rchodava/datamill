package org.chodavarapu.datamill.http.matching;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface MethodMatcher {
    String name();

    GuardedHandler ifDelete();
    GuardedHandler ifGet();
    GuardedHandler ifHead();
    GuardedHandler ifOptions();
    GuardedHandler ifPatch();
    GuardedHandler ifPost();
    GuardedHandler ifPut();

    boolean isDelete();
    boolean isGet();
    boolean isHead();
    boolean isOptions();
    boolean isPatch();
    boolean isPost();
    boolean isPut();
}
