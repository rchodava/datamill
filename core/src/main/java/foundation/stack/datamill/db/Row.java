package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;
import foundation.stack.datamill.values.Value;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Row {
    int size();
    Value column(int index);
    Value column(String name);
    Value column(String table, String name);
    Value column(Member member);
}
