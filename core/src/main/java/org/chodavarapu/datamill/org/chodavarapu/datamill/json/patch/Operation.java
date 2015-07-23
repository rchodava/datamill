package org.chodavarapu.datamill.org.chodavarapu.datamill.json.patch;

import org.chodavarapu.datamill.org.chodavarapu.datamill.json.JsonElement;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Operation {
    private final OperationType type;

    public Operation(OperationType type, String path, JsonElement value) {
        this.type = type;
    }

    public OperationType getType() {
        return type;
    }
}
