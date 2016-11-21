package foundation.stack.datamill.serialization;

import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface StructuredInput {
    Value get(String name);

    default Value get(String qualifier, String name) {
        return get(qualifier != null ? (qualifier + "." + name) : name);
    }

    Value get(Member member);
}
