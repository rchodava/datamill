package org.chodavarapu.datamill.cucumber;

import org.chodavarapu.datamill.json.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class FuzzyJsonTester {
    private static boolean areJsonObjectsSimilarEnough(JsonObject expected, JsonObject actual) {
        if (!actual.propertyNames().containsAll(expected.propertyNames())) {
            return false;
        }

        for (String propertyName : expected.propertyNames()) {
            JsonObject.JsonProperty expectedProperty = expected.get(propertyName);
            JsonObject.JsonProperty actualProperty = actual.get(propertyName);
            if ("ANY_VALUE".equals(expectedProperty.asString())) {
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
        if (expected instanceof String && actual instanceof String) {
            return expected.equals(actual);
        } else if (expected instanceof Number && actual instanceof Number) {
            return expected.getClass() == actual.getClass() && expected.equals(actual);
        } else if (expected instanceof JSONObject && actual instanceof JSONObject) {
            return areJsonObjectsSimilarEnough((JSONObject) expected, (JSONObject) actual);
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
