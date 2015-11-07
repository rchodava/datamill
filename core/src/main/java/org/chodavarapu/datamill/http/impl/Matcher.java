package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.ServerRequest;
import org.chodavarapu.datamill.http.Response;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Matcher {
    Observable<Response> applyIfMatches(ServerRequest request);
}
