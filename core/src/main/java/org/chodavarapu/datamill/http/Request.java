package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.http.matching.MethodMatcher;
import org.chodavarapu.datamill.http.matching.UriMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Request {
    RequestEntity entity();

    MethodMatcher method();

    ResponseBuilder respond();

    HttpServletRequest servletRequest();

    UriMatcher uri();
}
