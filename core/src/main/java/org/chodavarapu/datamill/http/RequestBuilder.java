package org.chodavarapu.datamill.http;

import org.chodavarapu.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RequestBuilder {
    Request build();
    RequestBuilder connectTimeout(int milliseconds);
    RequestBuilder entity(Value entity);
    RequestBuilder entity(Entity entity);
    RequestBuilder header(String name, String value);
    RequestBuilder header(RequestHeader header, String value);
    RequestBuilder method(Method method);
    RequestBuilder method(String method);
    RequestBuilder uri(String uri);
    <T> RequestBuilder uriParameter(String name, T value);
}
