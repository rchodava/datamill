package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Request {
    RequestEntity entity();

    Method method();

    ResponseBuilder respond();

    String uri();

    Value uriParameter(String parameter);
}
