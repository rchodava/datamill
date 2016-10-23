package foundation.stack.datamill.cucumber;

import com.google.common.base.Strings;
import com.jayway.jsonpath.JsonPath;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import foundation.stack.datamill.http.Method;
import foundation.stack.datamill.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class HttpSteps extends AbstractHttpSteps {
    private final static Logger logger = LoggerFactory.getLogger(HttpSteps.class);

    public HttpSteps(PropertyStore propertyStore, PlaceholderResolver placeholderResolver, HttpClient httpClient) {
        super(propertyStore, placeholderResolver, httpClient);
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
        doAddValueToHeader(headerKey, value);
    }

    @And("^the request header (.+) is removed$")
    public void removeHeader(String header) {
        Map<String, String> headers = initAndGetHeaders();
        headers.remove(header);
    }
}
