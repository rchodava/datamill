package org.chodavarapu.datamill.http.impl;

import com.google.common.io.CharStreams;
import io.vertx.core.http.HttpServerRequest;
import org.chodavarapu.datamill.http.RequestEntity;
import org.chodavarapu.datamill.http.HttpException;
import org.chodavarapu.datamill.json.JsonElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestEntityImpl implements RequestEntity {
    private final HttpServerRequest request;

    public RequestEntityImpl(HttpServerRequest request) {
        this.request = request;
    }

    @Override
    public JsonElement asJson() {
        return null;
    }

    @Override
    public Reader asReader() {
        return null;
    }

    @Override
    public InputStream asStream() {
        return null;
    }

    @Override
    public String asString() {
        try {
            return CharStreams.toString(asReader());
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }
}
