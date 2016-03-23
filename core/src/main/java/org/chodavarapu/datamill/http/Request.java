package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.values.Value;

import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Request {
    String OPTION_CONNECT_TIMEOUT = "connectTimeout";

    Entity entity();

    Map<String, String> headers();

    Optional<Value> header(String header);

    Optional<Value> header(RequestHeader header);

    Method method();

    Map<String, Object> options();

    String uri();

    Value uriParameter(String parameter);

    Map<String, String> uriParameters();
}
