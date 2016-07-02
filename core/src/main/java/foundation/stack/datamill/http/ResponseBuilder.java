package foundation.stack.datamill.http;

import foundation.stack.datamill.json.Json;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;

import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * Should be used to build HTTP {@link Response}s. A builder instance is passed to the lambda specified in the
 * {@link ServerRequest#respond(Function)} method. This builder should be used to construct responses.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ResponseBuilder {
    /** Build a response with a 400 Bad Request status, and an empty body. */
    Response badRequest();

    /** Build a response with a 400 Bad Request status, and the given string body. */
    Response badRequest(String content);

    /** Add a header to the response being built. */
    <T> ResponseBuilder header(String name, T value);

    /** Build a response with a 500 Internal Server Error status, and an empty body. */
    Response internalServerError();

    /** Build a response with a 500 Internal Server Error status, and the given string body. */
    Response internalServerError(String content);

    /** Build a response with a 20 No Content status, and an empty body. */
    Response noContent();

    /** Build a response with a 404 Not Found status, and an empty body. */
    Response notFound();

    /** Build a response with a 200 OK status, and an empty body. */
    Response ok();

    /** Build a response with a 200 OK status, and the given string body. */
    Response ok(String content);

    /** Build a response with a 200 OK status, and the given byte array body. */
    Response ok(byte[] content);

    /**
     * Add a body to the response being built which is made up of byte buffer data emissions. The lambda will receive a
     * {@link Observer} on which it can call {@link Observer#onNext(Object)} to emit data as byte buffers. These
     * emissions are sent out first, followed by the emissions made by the Observable returned by the lambda.
     */
    ResponseBuilder streamingBodyAsBufferChunks(Func1<Observer<ByteBuffer>, Observable<ByteBuffer>> bodyStreamer);

    /**
     * Add a body to the response being built which is made up of byte array data emissions. The lambda will receive a
     * {@link Observer} on which it can call {@link Observer#onNext(Object)} to emit data as byte arrays. These
     * emissions are sent out first, followed by the byte array emissions made by the Observable returned by the lambda.
     */
    ResponseBuilder streamingBody(Func1<Observer<byte[]>, Observable<byte[]>> bodyStreamer);

    /**
     * Add a body to the response being built which is made up of JSON object emissions. The lambda will receive a
     * {@link Observer} on which it can call {@link Observer#onNext(Object)} to emit JSON objects. These emissions are
     * sent out first, followed by the JSON objects emissions made by the Observable returned by the lambda.
     */
    ResponseBuilder streamingJson(Func1<Observer<Json>, Observable<Json>> jsonStreamer);

    /** Build a response with a 401 Unauthorized status, and an empty body. */
    Response unauthorized();

    /** Build a response with a 401 Unauthorized status, and the given string body. */
    Response unauthorized(String content);

    /** Build a response with a 403 Forbidden status, and an empty body. */
    Response forbidden();

    /** Build a response with a 403 Forbidden status, and the given string body. */
    Response forbidden(String content);

    /** Build a response with a 409 Conflict status, and the given string body. */
    Response conflict(String content);
}
