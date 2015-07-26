package org.chodavarapu.datamill.http.matching;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface UriMatcher {
    String get();

    GuardedHandler ifMatches(String pattern);
}
