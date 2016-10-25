package foundation.stack.datamill.cucumber;

import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Status;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class AbstractHttpSteps {
    final static String RESPONSE_KEY = "$$response";
    final static String LAST_RESPONSE_BODY_KEY = "$$lastResponseBody";
    final static String HEADER_KEY = "$$header";

    protected final PropertyStore propertyStore;
    protected final PlaceholderResolver placeholderResolver;
    protected final HttpClient httpClient;

    public AbstractHttpSteps(PropertyStore propertyStore, PlaceholderResolver placeholderResolver, HttpClient httpClient) {
        this.propertyStore = propertyStore;
        this.placeholderResolver = placeholderResolver;
        this.httpClient = httpClient;
    }

    protected final void makeCall(Method method, String uri, String payload) {
        final String resolvedUri = placeholderResolver.resolve(uri);
        final String resolvedPayload = placeholderResolver.resolve(payload);

        doAddValueToHeader("Origin", uri);

        httpClient.request(method, resolvedUri, getHeaders(), resolvedPayload)
                .doOnNext(response -> {
                    propertyStore.put(RESPONSE_KEY, response);
                    propertyStore.put(LAST_RESPONSE_BODY_KEY, getResponseBodyAsString(response));
                }).toBlocking().lastOrDefault(null);
    }

    protected void doAddValueToHeader(String headerKey, String value) {
        Map<String, String> headers = initAndGetHeaders();
        headers.put(headerKey, placeholderResolver.resolve(value));
    }

    void compareHeaders(Map<String, String> expectedHeaders, Multimap<String, String> actualHeaders) {
        for (Map.Entry<String, String> expectedHeadersEntries : expectedHeaders.entrySet()) {
            String resolvedExpectedValue = placeholderResolver.resolve(expectedHeadersEntries.getValue());
            Iterator<String> iterator = actualHeaders.get(expectedHeadersEntries.getKey()).iterator();
            if (iterator.hasNext()) {
                String actualValue = iterator.next();
                assertThat(resolvedExpectedValue, equalTo(actualValue));
                continue;
            }
            fail("Could not find corresponding header in response for " + expectedHeadersEntries.getKey());
        }
    }

    public Status getLastResponseStatus() {
        Response storedResponse = (Response) propertyStore.get(RESPONSE_KEY);
        return storedResponse.status();
    }

    protected final Map<String, String> initAndGetHeaders() {
        Map<String, String> headers = (Map<String, String>) propertyStore.get(HEADER_KEY);
        if (headers == null) {
            headers = new HashMap<>();
            propertyStore.put(HEADER_KEY, headers);
        }

        return headers;
    }

    protected String getResponseBodyAsString(Response response) {
        return response.body().map(body -> body.asString().toBlocking().lastOrDefault(null)).orElse(null);
    }

    protected final Map<String, String> getHeaders() {
        return (Map<String, String>) propertyStore.get(HEADER_KEY);
    }

    protected final void clearHeaders() {
        propertyStore.remove(HEADER_KEY);
    }
}
