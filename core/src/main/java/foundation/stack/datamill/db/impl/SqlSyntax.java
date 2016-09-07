package foundation.stack.datamill.db.impl;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface SqlSyntax {
    String SQL_ASC = " ASC";
    String SQL_ASSIGNMENT = " = ";
    String SQL_DELETE_FROM = "DELETE FROM ";
    String SQL_DELETE = "DELETE ";
    String SQL_DESC = " DESC";
    String SQL_EQ = " = ";
    String SQL_FROM = " FROM ";
    String SQL_GREATER_THAN = " > ";
    String SQL_INSERT_INTO = "INSERT INTO ";
    String SQL_LEFT_JOIN = " LEFT JOIN ";
    String SQL_LESS_THAN = " < ";
    String SQL_LIMIT = " LIMIT ";
    String SQL_NULL = "NULL";
    String SQL_ON = " ON ";
    String SQL_ON_DUPLICATE_KEY_UPDATE = " ON DUPLICATE KEY UPDATE ";
    String SQL_PARAMETER_PLACEHOLDER = "?";
    String SQL_SELECT = "SELECT ";
    String SQL_SET = " SET ";
    String SQL_WHERE = " WHERE ";
    String SQL_UPDATE = "UPDATE ";
    String SQL_IS = " IS ";
    String SQL_AND = " AND ";
    String SQL_IN = " IN ";
    String SQL_OR = " OR ";
    String SQL_ORDER_BY = " ORDER BY ";
    String OPEN_PARENTHESIS = "(";
    String CLOSE_PARENTHESIS = ")";
    String COMMA = ",";

    static String qualifiedName(String table, String column) {
        return table + "." + column;
    }
}
