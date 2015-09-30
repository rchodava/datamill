package org.chodavarapu.datamill.json;

import org.chodavarapu.datamill.json.patch.OperationType;
import org.chodavarapu.datamill.json.patch.Operation;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface JsonMappers {
//    Function<JsonObject, Stream<Operation>> JSON_TO_JSON_PATCH_OPERATIONS =
//            j -> j.children().stream().map(e ->
//                    new Operation(
//                            OperationType.fromString(e.get("op").asString()),
//                            e.get("path").asString(),
//                            e.get("value")));
}
