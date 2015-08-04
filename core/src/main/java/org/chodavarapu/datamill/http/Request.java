package org.chodavarapu.datamill.http;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Request {
    RequestEntity entity();

    Method method();

    ResponseBuilder respond();

    String uri();
}
