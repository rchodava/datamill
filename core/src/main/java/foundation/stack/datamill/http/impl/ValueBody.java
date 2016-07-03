package foundation.stack.datamill.http.impl;

import foundation.stack.datamill.http.Body;
import foundation.stack.datamill.http.HttpException;
import foundation.stack.datamill.json.JsonObject;
import foundation.stack.datamill.values.Value;
import rx.Observable;

import java.nio.ByteBuffer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ValueBody extends AbstractBody implements Body {
    private Value value;

    public ValueBody(Value value) {
        this.value = value;
    }

    @Override
    public Observable<byte[]> asBytes() {
        return asString().map(s -> s.getBytes());
    }

    @Override
    public Observable<byte[]> asChunks() {
        return asBytes();
    }

    @Override
    public Observable<ByteBuffer> asBufferChunks() {
        return asBytes().map(bytes -> ByteBuffer.wrap(bytes));
    }

    @Override
    public Observable<JsonObject> asJson() {
        if (value instanceof JsonObject) {
            return Observable.just((JsonObject) value);
        }

        return Observable.error(new HttpException("Value is not JSON!"));
    }

    @Override
    public Observable<String> asString() {
        if (value == null) {
            return Observable.empty();
        }
        return Observable.just(value.asString());
    }
}
