package org.chodavarapu.datamill.cucumber;

import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.jayway.jsonpath.JsonPath;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Status;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
            assertTrue(FuzzyJsonTester.isJsonSimilarEnough(resolvedExpectedJson, actualJsonBody));
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

    public Status getLastResponseStatus() {
        Response storedResponse = (Response) propertyStore.get(RESPONSE_KEY);
        return storedResponse.status();
    }

    private Map<String, String> initAndGetHeaders() {
        Map<String, String> headers = (Map<String, String>) propertyStore.get(HEADER_KEY);
        if (headers == null) {
            headers = new HashMap<>();
            propertyStore.put(HEADER_KEY, headers);
        }

        return headers;
    }

    private String getResponseBodyAsString(Response response) {
        return response.entity().asString().toBlocking().lastOrDefault(null);
    }

    private Map<String, String> getHeaders() {
        return (Map<String, String>) propertyStore.get(HEADER_KEY);
    }
}
