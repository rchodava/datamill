package foundation.stack.datamill.db;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface QueryRunner {
    ResultBuilder query(String sql);
    ResultBuilder query(String sql, Object... parameters);
    UpdateQueryExecution update(String sql, Object... parameters);
}
