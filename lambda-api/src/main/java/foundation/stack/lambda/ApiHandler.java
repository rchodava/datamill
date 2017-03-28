package foundation.stack.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import foundation.stack.datamill.http.*;
import foundation.stack.datamill.http.builder.RouteBuilder;
import foundation.stack.datamill.http.impl.RouteBuilderImpl;
import foundation.stack.datamill.http.impl.ServerRequestImpl;
import foundation.stack.datamill.http.impl.ValueBody;
import foundation.stack.datamill.values.StringValue;
import org.apache.log4j.Level;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public abstract class ApiHandler {
    private static final String BODY_PROPERTY = "body";
    private static final String LOG_LEVEL_PROPERTY = "logLevel";
    private static final String HEADERS_PROPERTY = "headers";
    private static final String HTTP_METHOD_PROPERTY = "httpMethod";
    private static final String PATH_PROPERTY = "path";
    private static final String QUERY_PARAMETERS_PROPERTY = "queryStringParameters";
    private static final String STATUS_CODE = "statusCode";

    private static final Logger logger = LoggerFactory.getLogger(ApiHandler.class);

    private static JSONObject badRequest() {
        return buildResponse(Status.BAD_REQUEST.getCode());
    }

    private static ServerRequest buildRequest(
            String method,
            String path,
            Multimap<String, String> headers,
            Multimap<String, String> queryParameters,
            String body) {
        return new ServerRequestImpl(method, headers, path, queryParameters, Charsets.UTF_8,
                body != null ? new ValueBody(new StringValue(body)) : null);
    }

    private static JSONObject internalServerError() {
        return buildResponse(Status.INTERNAL_SERVER_ERROR.getCode());
    }

    private static JSONObject buildResponse(int statusCode) {
        JSONObject json = new JSONObject();
        json.put(STATUS_CODE, statusCode);
        return json;
    }

    private static JSONObject notFound() {
        return buildResponse(Status.NOT_FOUND.getCode());
    }

    private static void setLogLevel() {
        String logLevelValue = System.getenv(LOG_LEVEL_PROPERTY);
        if (logLevelValue != null) {
            logger.debug("Setting log level to {}", logLevelValue);
            Level logLevel = Level.toLevel(logLevelValue);

            org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
            if (rootLogger != null) {
                rootLogger.setLevel(logLevel);
            }
        }
    }

    private static Multimap<String, String> toStringMap(JSONObject object) {
        if (object != null) {
            Multimap<String, String> map = HashMultimap.create();
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key != null) {
                    map.put(key, object.optString(key));
                }
            }

            return map;
        }

        return null;
    }

    private static JSONObject toJson(Multimap<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> entry : headers.entries()) {
                json.put(entry.getKey(), entry.getValue());
            }

            return json;
        }

        return null;
    }

    private static JSONObject transformResponse(Response response) {
        if (response != null) {
            Status status = response.status();
            if (status != null) {
                int statusCode = status.getCode();
                JSONObject json = buildResponse(statusCode);

                JSONObject headers = toJson(response.headers());
                json.put(HEADERS_PROPERTY, headers);

                Optional<Body> body = response.body();
                if (body != null && body.isPresent()) {
                    body.get().asString().subscribe(bodyContent -> json.put(BODY_PROPERTY, bodyContent));
                }

                return json;
            }

            logger.debug("Application returned a response with no status code!");
            return internalServerError();
        }

        return notFound();
    }

    private static void transformErrorResponse(
            Throwable originalError,
            Observable<Response> handledResponse,
            JSONObject[] responseJson) {
        if (handledResponse != null) {
            logger.debug("Error occurred: {} - invoking application error handler", originalError.getMessage());
            handledResponse.subscribe(
                    response -> responseJson[0] = transformResponse(response),
                    ___ -> responseJson[0] = internalServerError());
        } else {
            logger.debug("No application error handler to handle error", originalError);
            responseJson[0] = internalServerError();
        }
    }

    private static void write(OutputStream outputStream, JSONObject json) {
        logger.debug("Returning {} response", json.get(STATUS_CODE));
        if (logger.isTraceEnabled()) {
            logger.trace("Response: {}", json);
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, Charsets.UTF_8)) {
            json.write(writer);
        } catch (IOException e) {
            logger.debug("Error while closing output response");
        }
    }

    protected abstract Route constructRoute(RouteBuilder builder);

    public final void handle(InputStream requestStream, OutputStream responseStream, Context __) {
        setLogLevel();

        ServerRequest request = null;
        JSONObject[] responseJson = new JSONObject[]{null};
        try {
            JSONObject json = new JSONObject(new JSONTokener(requestStream));

            String path = json.optString(PATH_PROPERTY);
            Multimap<String, String> headers = toStringMap(json.optJSONObject(HEADERS_PROPERTY));
            Multimap<String, String> queryParameters = toStringMap(json.optJSONObject(QUERY_PARAMETERS_PROPERTY));
            String method = json.optString(HTTP_METHOD_PROPERTY);
            String body = json.optString(BODY_PROPERTY);

            if (method != null) {
                logger.debug("{} {} request received", method, path);

                Route route = constructRoute(new RouteBuilderImpl());
                request = buildRequest(method, path, headers, queryParameters, body);

                Observable<Response> responseObservable = route.apply(request);
                if (responseObservable != null) {
                    ServerRequest pinnedRequest = request;
                    responseObservable.subscribe(
                            response -> responseJson[0] = transformResponse(response),
                            error -> handleError(pinnedRequest, error, responseJson));

                    if (responseJson[0] == null) {
                        responseJson[0] = notFound();
                    }

                    write(responseStream, responseJson[0]);
                } else {
                    write(responseStream, notFound());
                }
            } else {
                logger.debug("Received request with no HTTP method!");
                write(responseStream, badRequest());
            }
        } catch (Exception e) {
            logger.debug("Error occurred: {} - invoking error handler", e.getMessage());
            handleError(request, e, responseJson);
            write(responseStream, responseJson[0]);
        }
    }

    protected Observable<Response> handleError(Throwable error) {
        return null;
    }

    protected Observable<Response> handleError(ServerRequest request, Throwable error) {
        return null;
    }

    private void handleError(ServerRequest request, Throwable error, JSONObject[] responseJson) {
        Observable<Response> errorObservable;

        if (request != null) {
            errorObservable = handleError(request, error);
        } else {
            errorObservable = handleError(error);
        }

        transformErrorResponse(error, errorObservable, responseJson);
    }
}
