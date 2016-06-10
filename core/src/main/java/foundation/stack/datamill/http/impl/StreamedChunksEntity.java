package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Entity;
import foundation.stack.datamill.http.HttpException;
import foundation.stack.datamill.json.JsonObject;
import rx.Observable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class StreamedChunksEntity implements Entity {
    private final Observable<byte[]> chunks;
    private final Charset charset;

    public StreamedChunksEntity(Observable<byte[]> chunks, Charset charset) {
        this.chunks = chunks;
        this.charset = charset;
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
    public Observable<byte[]> asChunks() {
        return chunks;
    }

    @Override
    public Observable<JsonObject> asJson() {
        return asString().map(string -> new JsonObject(string));
    }

    @Override
    public Observable<String> asString() {
        return asBytes().map(bytes -> new String(bytes, charset));
    }
}
