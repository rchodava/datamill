package foundation.stack.datamill.values;

import foundation.stack.datamill.reflection.Member;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface StructuredValue {
    Value get(String name);

    default Value get(String qualifier, String name) {
        return get(qualifier != null ? (qualifier + "." + name) : name);
    }

    Value get(Member member);
}
