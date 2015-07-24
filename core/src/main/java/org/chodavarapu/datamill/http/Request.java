package org.chodavarapu.datamill.http;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Request {
    RequestEntity entity();

    MethodMatcher method();

    ResponseBuilder respond();

    HttpServletRequest servletRequest();

    UriMatcher<Response> uri();
}
