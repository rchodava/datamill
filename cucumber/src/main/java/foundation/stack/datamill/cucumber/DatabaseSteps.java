package foundation.stack.datamill.cucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import foundation.stack.datamill.db.DatabaseClient;
import foundation.stack.datamill.db.Row;
import org.json.JSONObject;
import rx.Observable;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DatabaseSteps {

    private final static String SQL_SELECT = "SELECT * FROM ";
    private final static String SQL_WHERE = " WHERE ";
    private final static String SQL_AND = " AND ";

    private final PlaceholderResolver placeholderResolver;

    public DatabaseSteps(PlaceholderResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    @Given("^" + Phrases.SUBJECT + " store" + Phrases.OPTIONAL_PLURAL + " in table (.+) on (.+) a row with:$")
    public void storeDatabaseRow(String tableName, String databaseUrl, String json) {
        String resolvedUrl = placeholderResolver.resolve(databaseUrl);
        String resolvedJson = placeholderResolver.resolve(json);

        JSONObject rowJson = new JSONObject(resolvedJson);

        int count = new DatabaseClient(resolvedUrl).insertInto(tableName).row(builder -> {
            for (String propertyName : rowJson.keySet()) {
                Object value = rowJson.get(propertyName);
                if (value != null) {
                    if (value == JSONObject.NULL || value.equals("null")) {
                        builder.put(propertyName, null);
                    }
                    else if (value instanceof Number || value instanceof String) {
                        builder.put(propertyName, value);
                    }
                }
            }

            return builder.build();
        }).count().toBlocking().last();

        assertEquals("Failed to insert row into " + tableName, 1, count);
    }

    @Then("^the (.+) table in (.+) should have (.+) where (.+)$")
    public void checkDatabaseRowExists(String tableName, String databaseUrl, String criteriaFragment, String testFragment) {
        String resolvedUrl = placeholderResolver.resolve(databaseUrl);
        String resolvedCriteriaFragment = placeholderResolver.resolve(criteriaFragment);
        String resolvedTestFragment = placeholderResolver.resolve(testFragment);
        Row row = executeSelect(resolvedUrl, buildQuery(tableName, resolvedCriteriaFragment, resolvedTestFragment)).toBlocking().lastOrDefault(null);
        assertThat(row, notNullValue());
    }

    @Then("^the (.+) table in (.+) should contain a row where (.+)$")
    public void checkDatabaseRowExists(String tableName, String databaseUrl, String testFragment) {
        String resolvedUrl = placeholderResolver.resolve(databaseUrl);
        String resolvedTestFragment = placeholderResolver.resolve(testFragment);
        Row row = executeSelect(resolvedUrl, buildQuery(tableName, resolvedTestFragment, null)).toBlocking().lastOrDefault(null);
        assertThat(row, notNullValue());
    }

    private Observable<Row> executeSelect(String resolvedUrl, String sql, Object... parameters) {
        return buildDatabaseClient(resolvedUrl).query(sql, parameters).stream();
    }

    private DatabaseClient buildDatabaseClient(String resolvedUrl) {
        return new DatabaseClient(resolvedUrl);
    }

    protected String buildQuery(String tableName, String testFragment, String criteriaFragment) {
        StringBuilder queryBuilder = new StringBuilder(SQL_SELECT);

        queryBuilder.append(tableName);
        queryBuilder.append(SQL_WHERE);
        queryBuilder.append(testFragment);

        if (criteriaFragment != null && !criteriaFragment.isEmpty()) {
            queryBuilder.append(SQL_AND);
            queryBuilder.append(criteriaFragment);
        }
        return queryBuilder.toString();
    }

}
