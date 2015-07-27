package org.chodavarapu.datamill.json.patch;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public enum OperationType {
    ADD,
    COPY,
    MOVE,
    REMOVE,
    REPLACE,
    TEST;

    public static OperationType fromString(String type) {
        switch (type) {
            case "add": return ADD;
            case "copy": return COPY;
            case "move": return MOVE;
            case "remove": return REMOVE;
            case "replace": return REPLACE;
            case "test": return TEST;
        }

        return null;
    }
}
