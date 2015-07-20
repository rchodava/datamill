package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;

import javax.servlet.http.HttpServletResponse;
import java.util.function.Consumer;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseImpl implements Response, Consumer<HttpServletResponse> {
    private int status;

    public ResponseImpl(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public void accept(HttpServletResponse response) {
        response.setStatus(status);
    }
}
