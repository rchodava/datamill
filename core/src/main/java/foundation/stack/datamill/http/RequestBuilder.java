package foundation.stack.datamill.http;

import foundation.stack.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface RequestBuilder {
    Request build();
    RequestBuilder connectTimeout(int milliseconds);
    RequestBuilder body(Value entity);
    RequestBuilder body(Body body);
    RequestBuilder header(String name, String value);
    RequestBuilder header(RequestHeader header, String value);
    RequestBuilder method(Method method);
    RequestBuilder method(String method);
    RequestBuilder queryParameter(String name, String value);
    RequestBuilder uri(String uri);
    <T> RequestBuilder uriParameter(String name, T value);
}
