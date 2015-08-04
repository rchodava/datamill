package org.chodavarapu.datamill.http.impl;

import org.chodavarapu.datamill.http.Response;
import org.chodavarapu.datamill.http.Status;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ResponseImpl implements Response {
    private Status status;

    public ResponseImpl(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
