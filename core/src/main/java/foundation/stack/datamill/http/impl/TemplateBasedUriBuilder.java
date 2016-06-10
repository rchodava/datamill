package foundation.stack.datamill.http.impl;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class TemplateBasedUriBuilder {
    private static final Pattern placeholderPattern = Pattern.compile("\\{([a-zA-Z0-9]+)}");

    public String build(String uri, Map<String, String> uriParameters) {
        StringBuilder composedUri = new StringBuilder();

        java.util.regex.Matcher matcher = placeholderPattern.matcher(uri);
        int searchIx = 0;
        while (matcher.find(searchIx)) {
            composedUri.append(uri.substring(searchIx, matcher.start()));

            String parameterName = matcher.group(1);
            if (parameterName != null && parameterName.length() > 1) {
                String value = uriParameters.get(parameterName);
                if (value != null) {
                    composedUri.append(value);
                } else {
                    composedUri.append(matcher.group());
                }
            }

            searchIx = matcher.end();
        }

        if (searchIx < uri.length()) {
            composedUri.append(uri.substring(searchIx));
        }

        return composedUri.toString();
    }
}
