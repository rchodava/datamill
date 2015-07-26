package org.chodavarapu.datamill.http.matching;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface GuardedHandler extends MatchHandler {
    MatchHandler and(boolean guard);
    MatchHandler or(boolean guard);
}
