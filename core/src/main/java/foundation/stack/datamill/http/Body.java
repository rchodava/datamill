package foundation.stack.datamill.http;

import foundation.stack.datamill.json.JsonObject;
import rx.Observable;

import java.nio.ByteBuffer;

/**
 * Represents a HTTP request or response body, if it is present. It can be obtained as a stream of data, either in
 * chunks or as a whole.
 *
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Body {
    /** Get an {@link Observable} that emits the whole body as a single byte array. */
    Observable<byte[]> asBytes();

    /** Get an {@link Observable} that emits the whole body as a JSON object. */
    Observable<JsonObject> asJson();

    /** Get an {@link Observable} that emits the items in a JSON array. */
    Observable<JsonObject> asJsonArray();

    /** Get an {@link Observable} that emits the body as chunks of byte arrays. */
    Observable<byte[]> asChunks();

    /** Get an {@link Observable} that emits the body as chunks of {@link ByteBuffer}s. */
    Observable<ByteBuffer> asBufferChunks();

    /** Get an {@link Observable} that emits the whole body as a single string. */
    Observable<String> asString();

    /**
     * Deserialize (using Jackson) the body as an object.
     * @param clazz Class to deserialize as.
     * @param <T> Type of object to deserialize.
     * @return An observable that emits the body as an object of the desired type.
     */
    <T> Observable<T> fromJson(Class<T> clazz);
}
