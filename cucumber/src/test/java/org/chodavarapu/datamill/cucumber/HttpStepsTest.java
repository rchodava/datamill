package org.chodavarapu.datamill.cucumber;

import com.google.common.collect.ImmutableMap;
import org.chodavarapu.datamill.http.Method;
import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Server;
import org.chodavarapu.datamill.http.ServerRequest;
import org.chodavarapu.datamill.http.Status;
import org.chodavarapu.datamill.http.annotations.GET;
import org.chodavarapu.datamill.http.annotations.POST;
import org.chodavarapu.datamill.http.annotations.Path;
import org.chodavarapu.datamill.http.impl.InputStreamEntity;
import org.chodavarapu.datamill.http.impl.ResponseImpl;
import org.chodavarapu.datamill.reflection.OutlineBuilder;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Map;

import static org.chodavarapu.datamill.cucumber.HttpSteps.HEADER_KEY;
import static org.chodavarapu.datamill.cucumber.HttpSteps.LAST_RESPONSE_BODY_KEY;
import static org.chodavarapu.datamill.cucumber.HttpSteps.RESPONSE_KEY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class HttpStepsTest {

    private static final int DEFAULT_SERVER_PORT = 10000;
    private static final String EMPTY_STRING = "";
    private HttpSteps httpSteps;

    private PropertyStore propertyStore;
    private PlaceholderResolver placeholderResolver;

    private final static String URI = "http://localhost:%d/%s";
    private final static String EXPECTED_JSON = "{ \"expected\" : \"json\"}";
    private final static String STORE_KEY = "STORE_KEY";
    private final static String HEADER_VALUE = "HEADER_VALUE";

    private final HttpClient httpClient = new HttpClient();
    private final TestController testController = new TestController();

    private Server server;
    private int serverPort;

    private Response reponse;


    @Before
    public void setUp() {
        propertyStore = new PropertyStore();
        placeholderResolver = new PlaceholderResolver(propertyStore);
        httpSteps = new HttpSteps(propertyStore, placeholderResolver, httpClient);

        server = new Server(
                rb -> rb.ifMatchesBeanMethod(new OutlineBuilder().wrap(testController))
                        .orElse(r -> r.respond(b -> b.notFound())));
        serverPort = TestUtil.findRandomPort(DEFAULT_SERVER_PORT);
        server.listen("localhost", serverPort);
    }

    @Test
    public void userMakesCallWithEmptyPayload_worksAsExpected() {
        prepareResponse(Status.OK, null);

        httpSteps.userMakesCallWithEmptyPayload(Method.GET, String.format(URI, serverPort, "test/get"));

        assertResponse(EMPTY_STRING);
    }

    @Test
    public void userMakesCallWithProvidedPayload_worksAsExpected() {
        prepareResponse(Status.OK, EXPECTED_JSON);

        final String inputJson = "{ \"input\" : \"json\"}";
        httpSteps.userMakesCallWithProvidedPayload(Method.POST, String.format(URI, serverPort, "test/post"), inputJson);

        assertResponse(EXPECTED_JSON);
    }


    @Test(expected = AssertionError.class)
    public void assertStatusAndJsonResponse_failsWhenNoResponseStored() {
        prepareResponse(Status.OK, null);

        httpSteps.userMakesCallWithEmptyPayload(Method.GET, String.format(URI, serverPort, "test/get"));

        httpSteps.assertStatusAndJsonResponse(Status.OK.getCode(), "{ \"expected\" : \"json\"}");
    }

    @Test
    public void assertStatusAndJsonResponse_verifiesJsonResponse() {
        prepareResponse(Status.OK, EXPECTED_JSON);

        final String inputJson = "{ \"input\" : \"json\"}";
        httpSteps.userMakesCallWithProvidedPayload(Method.POST, String.format(URI, serverPort, "test/post"), inputJson);

        httpSteps.assertStatusAndJsonResponse(Status.OK.getCode(), EXPECTED_JSON);
    }

    @Test
    public void assertStatusAndNonEmptyResponse_verifiesNonEmptyResponse() {
        prepareResponse(Status.OK, EXPECTED_JSON);

        final String inputJson = "{ \"input\" : \"json\"}";
        httpSteps.userMakesCallWithProvidedPayload(Method.POST, String.format(URI, serverPort, "test/post"), inputJson);

        httpSteps.assertStatusAndNonEmptyResponse(Status.OK.getCode());
    }

    @Test
    public void assertStatus_verifiesResponseStatus() {
        prepareResponse(Status.OK, EXPECTED_JSON);

        final String inputJson = "{ \"input\" : \"json\"}";
        httpSteps.userMakesCallWithProvidedPayload(Method.POST, String.format(URI, serverPort, "test/post"), inputJson);

        httpSteps.assertStatus(Status.OK.getCode());
    }

    @Test
    public void storeResponse_worksAsExpected() {
        prepareResponse(Status.OK, EXPECTED_JSON);

        final String inputJson = "{ \"input\" : \"json\"}";
        httpSteps.userMakesCallWithProvidedPayload(Method.POST, String.format(URI, serverPort, "test/post"), inputJson);

        httpSteps.storeResponse(STORE_KEY);

        assertThat(propertyStore.get(STORE_KEY), is(EXPECTED_JSON));
    }

    @Test
    public void addValueToHeader_worksAsExpected() {
        prepareResponse(Status.OK, EXPECTED_JSON);

        final String inputJson = "{ \"input\" : \"json\"}";
        httpSteps.userMakesCallWithProvidedPayload(Method.POST, String.format(URI, serverPort, "test/post"), inputJson);

        httpSteps.addValueToHeader(STORE_KEY, HEADER_VALUE);

        assertThat(((Map<String, String>) propertyStore.get(HEADER_KEY)).get(STORE_KEY), is(HEADER_VALUE));
    }

    @Test
    public void assertResponseAndHeaders_worksAsExpected() {
        Map<String, String> responseHeaders = ImmutableMap.of("ExpectedHeader", "ExpectedHeaderValue");
        prepareResponse(Status.OK, EXPECTED_JSON, responseHeaders);

        final String inputJson = "{ \"input\" : \"json\"}";
        httpSteps.userMakesCallWithProvidedPayload(Method.POST, String.format(URI, serverPort, "test/post"), inputJson);

        httpSteps.assertResponseAndHeaders(Status.OK.getCode(), responseHeaders);

        assertResponse(EXPECTED_JSON, responseHeaders);
    }

    private void prepareResponse(Status status, String expectedJson) {
        prepareResponse(status, expectedJson, Collections.emptyMap());
    }

    private void prepareResponse(Status status, String expectedJson, Map<String, String> headers) {
        if (expectedJson == null) {
            reponse = new ResponseImpl(status, headers, null);
        }
        else {
            reponse = new ResponseImpl(status, headers, new InputStreamEntity(new ByteArrayInputStream(expectedJson.getBytes())));
        }
        testController.setResponse(reponse);
    }

    private void assertResponse(String expectedJsonResponse) {
        assertResponse(expectedJsonResponse, null);
    }

    private void assertResponse(String expectedJsonResponse, Map<String, String> expectedHeaders) {
        Response receivedResponse = (Response) propertyStore.get(RESPONSE_KEY);
        assertThat(reponse.status(), is(receivedResponse.status()));
        assertThat(propertyStore.get(LAST_RESPONSE_BODY_KEY), is(expectedJsonResponse));
        if (expectedHeaders != null && !expectedHeaders.isEmpty()) {
            httpSteps.compareHeaders(expectedHeaders, receivedResponse.headers());
        }
    }

    private class TestController {

        private Response response;

        @Path("/test/get")
        @GET
        public Observable<Response> get(ServerRequest request) {
            return Observable.just(response);
        }

        @Path("/test/post")
        @POST
        public Observable<Response> post(ServerRequest request) {
            return Observable.just(response);
        }

        public void setResponse(Response response) {
            this.response = response;
        }
    }

}
