package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.HttpException;
import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.json.JsonObject;
import rx.Observable;
import rx.functions.Action0;
import rx.observables.StringObservable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class InputStreamBody extends AbstractBody implements Body {
    private final InputStream inputStream;
    private final Action0 completionHandler;

    public InputStreamBody(InputStream inputStream) {
        this(inputStream, null);
    }

    public InputStreamBody(InputStream inputStream, Action0 completionHandler) {
        this.inputStream = inputStream;
        this.completionHandler = completionHandler;
    }

    @Override
    public Observable<byte[]> asBytes() {
        return asChunks().collect(
                () -> new ByteArrayOutputStream(),
                (stream, chunk) -> {
                    try {
                        stream.write(chunk);
                    } catch (IOException e) {
                        throw new HttpException(e);
                    }
                }).map(os -> os.toByteArray());
    }

    @Override
    public Observable<JsonObject> asJson() {
        return asString().map(string -> new JsonObject(string));
    }

    @Override
    public Observable<ByteBuffer> asBufferChunks() {
        return asChunks().map(bytes -> ByteBuffer.wrap(bytes));
    }

    @Override
    public Observable<byte[]> asChunks() {
        return StringObservable.from(inputStream)
                .doAfterTerminate(completionHandler != null ? completionHandler : () -> {});
    }

    @Override
    public Observable<String> asString() {
        return asBytes().map(bytes -> new String(bytes));
    }
}
