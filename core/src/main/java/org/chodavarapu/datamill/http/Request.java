package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.values.Value;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Request {
    RequestEntity entity();

    Optional<Value> header(String header);

    Optional<Value> header(RequestHeaders header);

    Method method();

    ResponseBuilder respond();

    String uri();

    Value uriParameter(String parameter);
}
