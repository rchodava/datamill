package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.HttpException;
import org.chodavarapu.datamill.http.Entity;
import org.chodavarapu.datamill.json.JsonObject;
import org.chodavarapu.datamill.values.Value;
import rx.Observable;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ValueEntity implements Entity {
    private Value value;

    public ValueEntity(Value value) {
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
