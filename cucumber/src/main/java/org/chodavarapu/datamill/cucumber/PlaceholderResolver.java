package org.chodavarapu.datamill.cucumber;

import com.jayway.jsonpath.JsonPath;
import org.chodavarapu.datamill.security.impl.BCrypt;
import org.chodavarapu.datamill.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.functions.Func1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class PlaceholderResolver {
    private final static Logger logger = LoggerFactory.getLogger(PlaceholderResolver.class);

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^\\}]+)\\}");
    private static final String RANDOM_ALPHANUMERIC_PLACEHOLDER_PREFIX = "randomAlphanumeric";
    private static final String HASHED_PLACEHOLDER_PREFIX = "blowfish:";
    private static final String DATE_FORMATTER_PLACEHOLDER_PREFIX = "currentTime:";

    private final PropertyStore propertyStore;

    public PlaceholderResolver(PropertyStore propertyStore) {
        this.propertyStore = propertyStore;
    }

    private String resolvePlaceholders(String value, Func1<String, String> placeholderValueLookup) {
        StringBuilder resolved = new StringBuilder();

        int startIndex = 0;

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        while (matcher.find(startIndex)) {
            String placeholder = matcher.group(1);
            String replacement = placeholderValueLookup.call(placeholder);

            resolved.append(value.substring(startIndex, matcher.start()));

            if (replacement != null) {
                resolved.append(replacement);
                startIndex = matcher.end();
            } else {
                resolved.append('{');
                startIndex = matcher.start() + 1;
            }
        }

        if (startIndex > -1 && startIndex < value.length()) {
            resolved.append(value.substring(startIndex, value.length()));
        }

        return resolved.toString();
    }

    public String resolve(String value) {
        if (value == null) {
            return null;
        }

        String resolved = resolvePlaceholders(value, this::resolvePlaceholder);
        logger.trace("Resolved string {} to {}", value, resolved);
        return resolved;
    }

    private String resolveRandomStringPlaceholder(String key) {
        if (key.startsWith(RANDOM_ALPHANUMERIC_PLACEHOLDER_PREFIX)) {
            key = key.substring(RANDOM_ALPHANUMERIC_PLACEHOLDER_PREFIX.length());

            int length = 16;
            try {
                length = Integer.parseInt(key);
            } catch (NumberFormatException e) {
            }

            return RandomGenerator.generateRandomAlphanumeric(length);
        }

        return null;
    }

    private String resolveHashedPasswordPlaceholder(String key) {
        if (key.startsWith(HASHED_PLACEHOLDER_PREFIX)) {
            key = key.substring(HASHED_PLACEHOLDER_PREFIX.length());
            return BCrypt.hashpw(key, BCrypt.gensalt());
        }

        return null;
    }

    private String resolveDateFormatterPlaceholder(String key) {
        if (key.startsWith(DATE_FORMATTER_PLACEHOLDER_PREFIX)) {
            key = key.substring(DATE_FORMATTER_PLACEHOLDER_PREFIX.length());
            return new SimpleDateFormat(key).format(Calendar.getInstance().getTime());
        }

        return null;
    }

    private String resolvePlaceholder(String key) {
        String value = findPropertyInStore(key);
        if (value != null) {
            return value;
        }

        value = resolveRandomStringPlaceholder(key);
        if (value != null) {
            return value;
        }

        value = resolveHashedPasswordPlaceholder(key);
        if (value != null) {
            return value;
        }

        value = resolveDateFormatterPlaceholder(key);
        if (value != null) {
            return value;
        }

        int expressionStart = key.indexOf('.');
        if (expressionStart < 0) {
            expressionStart = key.indexOf('[');
        }

        if (expressionStart > -1) {
            String expression = '$' + key.substring(expressionStart);
            key = key.substring(0, expressionStart);

            value = findPropertyInStore(key);
            if (value != null) {
                Object resolved = JsonPath.read(value, expression);
                if (resolved != null) {
                    return resolved.toString();
                }
            }
        }

        return null;
    }

    private String findPropertyInStore(String key) {
        Object value = propertyStore.get(key);
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Value) {
            return ((Value) value).asString();
        } else if (value != null) {
            return value.toString();
        }

        return null;
    }
}
