package foundation.stack.datamill.http.impl;

import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.Body;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestImpl extends AbstractRequestImpl {
    private final Map<String, Object> options;
    private final Multimap<String, String> queryParameters;
    private final Map<String, String> uriParameters;

    RequestImpl(
            String method,
            Multimap<String, String> headers,
            String uri,
            Multimap<String, String> queryParameters,
            Map<String, String> uriParameters,
            Map<String, Object> options,
            Body body) {
        super(method, headers, uri, body);

        this.queryParameters = queryParameters;
        this.options = options;
        this.uriParameters = uriParameters;
    }

    @Override
    public Map<String, Object> options() {
        return options;
    }

    @Override
    public Multimap<String, String> queryParameters() {
        return queryParameters;
    }

    @Override
    public Map<String, String> uriParameters() {
        return uriParameters;
    }
}
