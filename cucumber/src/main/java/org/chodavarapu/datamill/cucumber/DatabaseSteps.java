package org.chodavarapu.datamill.cucumber;

import cucumber.api.java.en.Given;
import org.chodavarapu.datamill.db.DatabaseClient;
import org.json.JSONException;
import org.json.JSONObject;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DatabaseSteps {
//    @Then("^the (.+) table should have (.+) where (.+)$")
//    public void checkDatabase(String tableName, String criteriaFragment, String testFragment) {
//        String modifiedTestFragment = getReplacedString(placeholderPattern, testFragment, placeholderRemover, standardPlaceholderReplacer);
//        String modifiedCriteriaFragment = getReplacedString(placeholderPattern, criteriaFragment, placeholderRemover, placeholderReplacer);
//        Row row = executeSelect(buildQuery(tableName, modifiedTestFragment, modifiedCriteriaFragment)).toBlocking().lastOrDefault(null);
//        assertThat(row, notNullValue());
//    }
//
//    @Then("^the (.+) table should contain a row where (.+)$")
//    public void checkDatabase(String tableName, String testFragment) {
//        String modifiedTestFragment = getReplacedString(placeholderPattern, testFragment, placeholderRemover, standardPlaceholderReplacer);
//        Row row = executeSelect(buildQuery(tableName, modifiedTestFragment, null)).toBlocking().lastOrDefault(null);
//        assertThat(row, notNullValue());
//    }
//
//    @And("^the (.+) in the (.+) table row where (.+) is stored as (.+)$")
//    public void storeDatabaseValue(String columnName, String tableName, String testFragment, String key) {
//        String modifiedTestFragment = getReplacedString(placeholderPattern, testFragment, placeholderRemover, standardPlaceholderReplacer);
//        executeSelect(buildQuery(tableName, modifiedTestFragment, null))
//                .doOnNext(row -> addToRepository(key, row.column(columnName).asString()))
//                .toBlocking().last();
//    }

    private final PlaceholderResolver placeholderResolver;

    public DatabaseSteps(PlaceholderResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    @Given("^the (.+) table in (.+) contains a row with:$")
    public void storeDatabaseRow(String tableName, String databaseUrl, String json) {
        String resolvedUrl = placeholderResolver.resolve(databaseUrl);
        String resolvedJson = placeholderResolver.resolve(json);

        JSONObject rowJson = new JSONObject(resolvedJson);

        int count = new DatabaseClient(resolvedUrl).insertInto(tableName).row(builder -> {
            for (String propertyName : rowJson.keySet()) {
                Object value = rowJson.get(propertyName);
                if (value != null) {
                    if (value == JSONObject.NULL) {
                        builder.put(propertyName, null);
                    } else if (value instanceof Number || value instanceof String) {
                        builder.put(propertyName, value);
                    }
                }
            }

            return builder.build();
        }).count().toBlocking().last();

        assertEquals("Failed to insert row into " + tableName, 1, count);
    }
}
