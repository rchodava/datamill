package foundation.stack.datamill.http.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class UriTemplate {
    private static final String TEMPLATE_REGEX = "(:\\s*([^\\}]*)\\s*)?";
    private static final String TEMPLATE_VARIABLE = "(\\w[\\w\\.-]*)";
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\{\\s*" + TEMPLATE_VARIABLE + "\\s*" + TEMPLATE_REGEX + "\\}");

    private static String stripSlashes(String uri) {
        int start = 0;
        if (uri.charAt(0) == '/') {
            start = 1;
        }

        int end = uri.length() - 1;
        if (uri.charAt(end) == '/') {
            end--;
        }

        if (end > start) {
            return uri.substring(start, end + 1);
        }

        return "";
    }

    private final List<UriTemplateRegion> regions = new ArrayList<>();

    public UriTemplate(String template) {
        computeTemplateRegions(template);
    }

    private void computeTemplateRegions(String template) {
        template = stripSlashes(template);

        int previousRegion = 0;
        Matcher matcher = TEMPLATE_PATTERN.matcher(template);
        while (matcher.find()) {
            int regionStart = matcher.start();
            int regionEnd = matcher.end();

            if (previousRegion < regionStart) {
                regions.add(new UriTemplateRegion(template.substring(previousRegion, regionStart)));
            }

            addMatchedVariableRegion(matcher);

            previousRegion = regionEnd;
        }

        if (previousRegion < template.length()) {
            regions.add(new UriTemplateRegion(template.substring(previousRegion)));
        }
    }

    private void addMatchedVariableRegion(Matcher matcher) {
        String variableName = matcher.group(1);
        String regex = matcher.group(3);

        if (regex == null) {
            regions.add(new UriTemplateRegion(variableName, null));
        } else {
            regions.add(new UriTemplateRegion(variableName, regex));
        }
    }

    public Map<String, String> match(String uri) {
        uri = stripSlashes(uri);

        HashMap<String, String> matches = null;

        int uriLength = uri.length();
        int position = 0;
        for (UriTemplateRegion region : regions) {
            if (position > uriLength) {
                return null;
            }

            int regionEnd = region.match(uri, position);
            if (regionEnd < 0) {
                return null;
            }

            if (!region.isFixedContent()) {
                if (matches == null) {
                    matches = new HashMap<>();
                }

                matches.put(region.getVariable(), uri.substring(position, regionEnd));
            }

            position = regionEnd;
        }

        if (position != uri.length()) {
            return null;
        }

        if (matches == null) {
            return Collections.emptyMap();
        }

        return matches;
    }

    @Override
    public String toString() {
        StringBuilder template = new StringBuilder();
        for (UriTemplateRegion region : regions) {
            template.append(region.toString());
        }
        return template.toString();
    }
}
