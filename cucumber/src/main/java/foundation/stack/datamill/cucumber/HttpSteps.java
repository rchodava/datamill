package foundation.stack.datamill.cucumber;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.JsonPath;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Status;
import foundation.stack.datamill.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class HttpSteps {
    private final static Logger logger = LoggerFactory.getLogger(HttpSteps.class);

    final static String RESPONSE_KEY = "$$response";
    final static String LAST_RESPONSE_BODY_KEY = "$$lastResponseBody";
    final static String HEADER_KEY = "$$header";

    private final PropertyStore propertyStore;
    private final PlaceholderResolver placeholderResolver;
    private final HttpClient httpClient;

    public HttpSteps(PropertyStore propertyStore, PlaceholderResolver placeholderResolver, HttpClient httpClient) {
        this.propertyStore = propertyStore;
        this.placeholderResolver = placeholderResolver;
        this.httpClient = httpClient;
    }

    @Given("^" + Phrases.SUBJECT + " make" + Phrases.OPTIONAL_PLURAL + " a " + Phrases.HTTP_METHOD + " " +
            Phrases.HTTP_REQUEST + " to \"([^\"]+)\"?$")
    public void userMakesCallWithEmptyPayload(Method method, String uri) {
        makeCall(method, uri, null);
    }

    @Given("^" + Phrases.SUBJECT + " make" + Phrases.OPTIONAL_PLURAL + " a " + Phrases.HTTP_METHOD + " " +
            Phrases.HTTP_REQUEST + " to \"([^\"]+)\" with " + Phrases.HTTP_BODY + " (.+)$")
    public void userMakesCallWithProvidedPayload(Method method, String uri, String payload) {
        makeCall(method, uri, payload);
    }

    @Given("^" + Phrases.SUBJECT + " make" + Phrases.OPTIONAL_PLURAL + " a " + Phrases.HTTP_METHOD + " " +
            Phrases.HTTP_REQUEST + " to \"([^\"]+)\" with " + Phrases.HTTP_BODY + ":$")
    public void userMakesCallWithProvidedMultiLinePayload(Method method, String uri, String payload) {
        userMakesCallWithProvidedPayload(method, uri, payload);
    }

    private void makeCall(Method method, String uri, String payload) {
        final String resolvedUri = placeholderResolver.resolve(uri);
        final String resolvedPayload = placeholderResolver.resolve(payload);

        addValueToHeader("Origin", uri);

        httpClient.request(method, resolvedUri, getHeaders(), resolvedPayload)
                .doOnNext(response -> {
                    propertyStore.put(RESPONSE_KEY, response);
                    propertyStore.put(LAST_RESPONSE_BODY_KEY, getResponseBodyAsString(response));
                }).toBlocking().lastOrDefault(null);
    }

    @Then("^" + Phrases.SUBJECT + " should get a (\\d+) response and JSON matching:$")
    public void assertStatusAndJsonResponse(int statusCode, String expectedJson) {
        assertStatus(statusCode);

        String actualJsonBody = (String) propertyStore.get(LAST_RESPONSE_BODY_KEY);
        if (actualJsonBody != null && !actualJsonBody.isEmpty()) {
            String resolvedExpectedJson = placeholderResolver.resolve(expectedJson);
            boolean similarEnough = FuzzyJsonTester.isJsonSimilarEnough(resolvedExpectedJson, actualJsonBody);
            if (!similarEnough) {
                logger.debug("Not similar enough expected [{}] actual [{}]", resolvedExpectedJson, actualJsonBody);
            }
            assertTrue(similarEnough);
        } else {
            fail("Response was empty when expecting a non-empty JSON body!");
        }
    }

    @Then("^" + Phrases.SUBJECT + " should get a (\\d+) response and JSON with (.+) matching:$")
    public void assertStatusAndJsonResponsePortion(int statusCode, String path, String expectedJson) {
        assertStatus(statusCode);

        String actualJsonBody = (String) propertyStore.get(LAST_RESPONSE_BODY_KEY);
        if (actualJsonBody != null && !actualJsonBody.isEmpty()) {
            Object portion = JsonPath.read(actualJsonBody, path);
            String resolvedExpectedJson = placeholderResolver.resolve(expectedJson);
            assertTrue(FuzzyJsonTester.isJsonSimilarEnough(resolvedExpectedJson, portion.toString()));
        } else {
            fail("Response was empty when expecting a non-empty JSON body!");
        }
    }

    @Then("^" + Phrases.SUBJECT + " should get a (\\d+) response with a non-empty " + Phrases.HTTP_BODY + "$")
    public void assertStatusAndNonEmptyResponse(int statusCode) {
        assertStatus(statusCode);

        String responseEntity = (String) propertyStore.get(LAST_RESPONSE_BODY_KEY);
        assertFalse(Strings.isNullOrEmpty(responseEntity));
    }

    @Then("^" + Phrases.SUBJECT + " should get a (\\d+) response with " + Phrases.HTTP_BODY + " containing \"(.+)\"$")
    public void assertStatusAndResponseWithContent(int statusCode, String expectedContent) {
        assertStatus(statusCode);

        String responseEntity = (String) propertyStore.get(LAST_RESPONSE_BODY_KEY);

        String resolvedExpectedContent = placeholderResolver.resolve(expectedContent);
        assertTrue(responseEntity.contains(resolvedExpectedContent));
    }

    @Then("^" + Phrases.SUBJECT + " should get a (\\d+) response$")
    public void assertStatus(int statusCode) {
        Response storedResponse = (Response) propertyStore.get(RESPONSE_KEY);
        assertThat(storedResponse.status().getCode(), is(statusCode));
    }

    @Then("^" + Phrases.SUBJECT + " should get a (\\d+) response with headers:$")
    public void assertResponseAndHeaders(int statusCode, Map<String, String> headers) {
        Response storedResponse = (Response) propertyStore.get(RESPONSE_KEY);
        assertThat(storedResponse.status().getCode(), is(statusCode));
        compareHeaders(headers, storedResponse.headers());
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

    @And("^the response " + Phrases.HTTP_BODY + " is stored as (.+)$")
    public void storeResponse(String key) {
        String lastResponseBody = (String) propertyStore.get(LAST_RESPONSE_BODY_KEY);
        propertyStore.put(key, lastResponseBody);
    }

    @And("^(.+) from the JSON response is stored as (.+)$")
    public void storeJsonResponsePortion(String path, String key) {
        String lastResponseBody = (String) propertyStore.get(LAST_RESPONSE_BODY_KEY);
        Object portion = JsonPath.read(lastResponseBody, path);
        propertyStore.put(key, portion);
    }

    @And("^a request header (.+) is added with value \"(.*)\"$")
    public void addValueToHeader(String headerKey, String value) {
        Map<String, String> headers = initAndGetHeaders();
        headers.put(headerKey, placeholderResolver.resolve(value));
    }

    @And("^the request header (.+) is removed$")
    public void removeHeader(String header) {
        Map<String, String> headers = initAndGetHeaders();
        headers.remove(header);
    }

    public Status getLastResponseStatus() {
        Response storedResponse = (Response) propertyStore.get(RESPONSE_KEY);
        return storedResponse.status();
    }

    public Map<String, String> initAndGetHeaders() {
        Map<String, String> headers = (Map<String, String>) propertyStore.get(HEADER_KEY);
        if (headers == null) {
            headers = new HashMap<>();
            propertyStore.put(HEADER_KEY, headers);
        }

        return headers;
    }

    private String getResponseBodyAsString(Response response) {
        return response.body().map(body -> body.asString().toBlocking().lastOrDefault(null)).orElse(null);
    }

    public Map<String, String> getHeaders() {
        return (Map<String, String>) propertyStore.get(HEADER_KEY);
    }

    public Observable<JsonObject> makeStreamingJsonCall(Method method, String uri, Map<String, String> headers) {
        final String resolvedUri = placeholderResolver.resolve(uri);

        addValueToHeader("Origin", uri);

        return httpClient.request(method, resolvedUri, headers, null)
                .flatMap(response -> {
                    Body body = response.body().orElse(null);
                    logger.debug("Processing response for uri {}", uri);
                    return process(body.asChunks());
                });
    }

    private Observable<JsonObject> process(Observable<byte[]> chunksObservable) {
        return chunksObservable.flatMap(chunk -> {
            String chunkStr = new String(chunk);
            logger.trace("Got chunk {}", chunkStr);
            if (chunkStr.startsWith("[")) {
                chunkStr = chunkStr.substring(1, chunkStr.length());
            }
            if (chunkStr.endsWith(",")) {
                chunkStr = chunkStr.substring(0, chunkStr.length());
            }
            String[] jsonTokens = chunkStr.split("\\},");
            return Observable.from(buildJsonObjects(jsonTokens));
        });
    }

    private List<JsonObject> buildJsonObjects(String[] jsonTokens) {
        List<JsonObject> jsonObjects = Arrays.stream(jsonTokens).map(token -> new JsonObject(token + "}")).collect(Collectors.toList());
        return  jsonObjects;
    }

    public final void clearHeaders() {
        propertyStore.remove(HEADER_KEY);
    }

    public PropertyStore getPropertyStore() {
        return propertyStore;
    }

    public PlaceholderResolver getPlaceholderResolver() {
        return placeholderResolver;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }
}
