package foundation.stack.datamill.cucumber;

import foundation.stack.datamill.http.Client;
import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.values.StringValue;
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
            case POST:  return client.post(uri, headers, payload != null ? new StringValue(payload) : null);
            case PATCH:  return client.patch(uri, headers, payload != null ? new StringValue(payload) : null);
            case PUT:  return client.put(uri, headers, payload != null ? new StringValue(payload) : null);
            case DELETE:  return client.delete(uri, headers);
            default: throw new UnsupportedOperationException("Method " + method + " is not implemented by client");
        }
    }
}
