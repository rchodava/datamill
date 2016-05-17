package org.chodavarapu.datamill.cucumber;

import org.chodavarapu.datamill.http.Client;
import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.values.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Map;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class HttpClient {
    private final static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private final Client client = new Client();

    public Observable<Response> request(Method method, String uri, Map<String, String> headers, String payload) {
        logger.debug("Making a {} request to {} with headers {} and payload {}", method, uri, headers, payload);
        switch (method) {
            case GET: return client.get(uri, headers);
            case POST:  return client.post(uri, headers, new StringValue(payload));
            case PATCH:  return client.patch(uri, headers, new StringValue(payload));
            case PUT:  return client.put(uri, headers, new StringValue(payload));
            case DELETE:  return client.delete(uri, headers);
            default: throw new UnsupportedOperationException("Method " + method + " is not implemented by client");
        }
    }
}
