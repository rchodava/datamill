package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.values.Value;

import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Request {
    Entity entity();

    Map<String, String> headers();

    Optional<Value> header(String header);

    Optional<Value> header(RequestHeader header);

    Method method();

    String uri();

    Value uriParameter(String parameter);

    Map<String, String> uriParameters();
}
