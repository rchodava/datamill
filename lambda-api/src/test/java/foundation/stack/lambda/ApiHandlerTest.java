package foundation.stack.lambda;

import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.Response;
import foundation.stack.datamill.http.Route;
import foundation.stack.datamill.http.ServerRequest;
import foundation.stack.datamill.http.builder.RouteBuilder;
import org.json.JSONObject;
import org.junit.Test;
import rx.Observable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ApiHandlerTest {
    private static String toString(Multimap<String, String> map) {
        if (map != null) {
            StringBuilder builder = new StringBuilder("(");
            for (Map.Entry<String, String> entry : map.entries()) {
                builder.append(entry.getKey());
                builder.append("=");
                builder.append(entry.getValue());
                builder.append(',');
            }
            builder.append(")");

            return builder.toString();
        }

        return "()";
    }

    private static class TestApiHandler extends ApiHandler {
        @Override
        protected Route constructRoute(RouteBuilder builder) {
            return builder.ifUriMatches("/test",
                    request -> request.respond(b ->
                            b.header("Location", "http://localhost")
                                    .ok(request.method() + " " + request.uri() + " " +
                                    ApiHandlerTest.toString(request.headers()) + " " +
                                    ApiHandlerTest.toString(request.queryParameters()))))
                    .elseIfUriMatches("/null", request -> Observable.just(null))
                    .elseIfUriMatches("/error", request -> Observable.error(new IllegalArgumentException()))
                    .elseIfUriMatches("/handled", request -> Observable.error(new IllegalStateException()))
                    .elseIfUriMatches("/unhandled", request -> {
                        throw new IllegalStateException();
                    })
                    .orElse(request -> request.respond(b -> b.ok("else")));
        }

        @Override
        protected Observable<Response> handleError(ServerRequest request, Throwable error) {
            if (error instanceof IllegalStateException) {
                return request.respond(b -> b.ok("handled"));
            }

            return null;
        }
    }

    @Test
    public void handle() {
        // Test valid request
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        TestApiHandler handler = new TestApiHandler();
        handler.handleRequest(new ByteArrayInputStream(("{" +
                "\"path\": \"/test\", " +
                "\"httpMethod\": \"GET\", " +
                "\"headers\": {" +
                "\"Authorization\": \"token\", " +
                "\"Accept\": \"application/json\"" +
                "}," +
                "\"queryStringParameters\": {" +
                "\"param1\": \"value1\", " +
                "\"param2\": \"value2\"" +
                "}" +
                "}")
                .getBytes(Charsets.UTF_8)), output, null);

        JSONObject response = new JSONObject(new String(output.toByteArray(), Charsets.UTF_8));
        assertEquals(200, response.optInt("statusCode"));
        assertEquals("GET /test (Authorization=token,Accept=application/json,) (param1=value1,param2=value2,)",
                response.optString("body"));
        assertEquals("http://localhost", response.optJSONObject("headers").optString("Location"));

        // Request with no HTTP method
        output = new ByteArrayOutputStream();
        handler.handleRequest(new ByteArrayInputStream(("{ \"path\": \"/test\" }").getBytes(Charsets.UTF_8)), output, null);

        response = new JSONObject(new String(output.toByteArray(), Charsets.UTF_8));
        assertEquals(400, response.optInt("statusCode"));
        assertEquals("", response.optString("body"));

        // Test a route that returns a null response
        output = new ByteArrayOutputStream();
        handler.handleRequest(new ByteArrayInputStream(("{" +
                "\"path\": \"/null\", " +
                "\"httpMethod\": \"GET\"" +
                "}")
                .getBytes(Charsets.UTF_8)), output, null);

        response = new JSONObject(new String(output.toByteArray(), Charsets.UTF_8));
        assertEquals(404, response.optInt("statusCode"));
        assertEquals("", response.optString("body"));

        // Test a request with an uknown path
        output = new ByteArrayOutputStream();
        handler.handleRequest(new ByteArrayInputStream(("{" +
                "\"path\": \"/other\", " +
                "\"httpMethod\": \"GET\"" +
                "}")
                .getBytes(Charsets.UTF_8)), output, null);

        response = new JSONObject(new String(output.toByteArray(), Charsets.UTF_8));
        assertEquals(200, response.optInt("statusCode"));
        assertEquals("else", response.optString("body"));

        // Test a route that results in an error being emitted, that isn't handled
        output = new ByteArrayOutputStream();
        handler.handleRequest(new ByteArrayInputStream(("{" +
                "\"path\": \"/error\", " +
                "\"httpMethod\": \"GET\"" +
                "}")
                .getBytes(Charsets.UTF_8)), output, null);

        response = new JSONObject(new String(output.toByteArray(), Charsets.UTF_8));
        assertEquals(500, response.optInt("statusCode"));
        assertEquals("", response.optString("body"));

        // Test a route that results in an error being thrown
        output = new ByteArrayOutputStream();
        handler.handleRequest(new ByteArrayInputStream(("{" +
                "\"path\": \"/unhandled\", " +
                "\"httpMethod\": \"GET\"" +
                "}")
                .getBytes(Charsets.UTF_8)), output, null);

        response = new JSONObject(new String(output.toByteArray(), Charsets.UTF_8));
        assertEquals(500, response.optInt("statusCode"));
        assertEquals("", response.optString("body"));
    }
}
