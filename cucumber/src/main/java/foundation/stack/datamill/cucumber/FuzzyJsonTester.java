package foundation.stack.datamill.cucumber;

import foundation.stack.datamill.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class FuzzyJsonTester {
    private static final String ANY_VALUE = "*";
    private static final Logger logger = LoggerFactory.getLogger(FuzzyJsonTester.class);

    private FuzzyJsonTester() {
    }

    private static boolean areJsonObjectsSimilarEnough(JsonObject expected, JsonObject actual) {
        if (!actual.propertyNames().containsAll(expected.propertyNames())) {
            return false;
        }

        for (String propertyName : expected.propertyNames()) {
            JsonObject.JsonProperty expectedProperty = expected.get(propertyName);
            JsonObject.JsonProperty actualProperty = actual.get(propertyName);
            if (ANY_VALUE.equals(expectedProperty.asString())) {
                // When we can't anticipate the return value of a property (i.e. db set id) we must set ANY_VALUE as the property
                // value so that comparison only fails if it is not found in actual json object
                if (actualProperty == null) {
                    return false;
                } else {
                    continue;
                }
            }
            if (!expectedProperty.asString().equals(actualProperty.asString())) {
                return false;
            }
        }

        return true;
    }

    private static boolean areJsonObjectsSimilarEnough(JSONObject expected, JSONObject actual) {
        if (!actual.keySet().containsAll(expected.keySet())) {
            return false;
        }

        for (String propertyName : expected.keySet()) {
            Object expectedProperty = expected.get(propertyName);
            Object actualProperty = actual.get(propertyName);

            if (expectedProperty != null && !isSimilar(expectedProperty, actualProperty)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isSimilar(Object expected, Object actual) {
        logger.debug("Comparing expected {} and actual {}", expected, actual);
        if (expected instanceof String && expected.equals(ANY_VALUE)) {
            return true;
        }
        if (expected instanceof String && actual instanceof String) {
            return expected.equals(ANY_VALUE) || expected.equals(actual);
        } else if (expected instanceof Number && actual instanceof Number) {
            return expected.getClass() == actual.getClass() && expected.equals(actual);
        } else if (expected instanceof JSONObject && actual instanceof JSONObject) {
            return areJsonObjectsSimilarEnough((JSONObject) expected, (JSONObject) actual);
        } else if (expected instanceof JSONArray && actual instanceof JSONArray) {
            JSONArray expectedArray = (JSONArray) expected;
            JSONArray actualArray = (JSONArray) actual;
            for(int i = 0; i < expectedArray.length(); i++) {
                if(!isSimilar(expectedArray.get(i), actualArray.get(i))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public static boolean isJsonSimilarEnough(String expected, String actual) {
        if (expected != null) {
            expected = expected.trim();
        }

        if (actual != null) {
            actual = actual.trim();
        }

        try {
            JsonObject expectedObject = new JsonObject(expected);
            JsonObject actualObject = new JsonObject(actual);

            return areJsonObjectsSimilarEnough(expectedObject, actualObject);
        } catch (JSONException e) {
            JSONArray expectedArray = new JSONArray(expected);
            JSONArray actualArray = new JSONArray(actual);

            for (int i = 0; i < expectedArray.length(); i++) {
                boolean foundSimilarActualObject = false;

                for (int j = 0; j < actualArray.length(); j++) {
                    if (isSimilar(expectedArray.get(i), actualArray.get(j))) {
                        foundSimilarActualObject = true;
                    }
                }

                if (!foundSimilarActualObject) {
                    return false;
                }
            }
        }

        return true;
    }
}
