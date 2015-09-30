package org.chodavarapu.datamill.json.patch;

import org.chodavarapu.datamill.json.JsonObject;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Operation {
    private final OperationType type;

    public Operation(OperationType type, String path, JsonObject value) {
        this.type = type;
    }

    public OperationType getType() {
        return type;
    }
}
