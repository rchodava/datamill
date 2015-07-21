package org.chodavarapu.datamill.http.impl;

import com.google.common.io.CharStreams;
import org.chodavarapu.datamill.http.RequestEntity;
import org.chodavarapu.datamill.http.HttpException;
import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonElement;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class RequestEntityImpl implements RequestEntity {
    private final HttpServletRequest request;

    public RequestEntityImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public JsonElement asJson() {
        return null;
    }

    @Override
    public Reader asReader() {
        try {
            return request.getReader();
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    @Override
    public InputStream asStream() {
        try {
            return request.getInputStream();
        } catch (IOException e) {
            throw new HttpException(e);
        }
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
