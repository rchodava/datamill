package org.chodavarapu.datamill.http.builder;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface UriMatchingBuilder {
    MatchHandlerBuilder ifUriMatches(String pattern);
}
