package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.values.StructuredValue;
import foundation.stack.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Row extends StructuredValue {
    int size();
    Value get(int index);
    Value get(String name);
    Value get(String table, String name);
    Value get(Member member);
}
