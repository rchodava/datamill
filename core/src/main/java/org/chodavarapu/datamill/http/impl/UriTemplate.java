package org.chodavarapu.datamill.http.impl;

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

    private static class TemplateRegion {
        private final String variable;
        private final String content;
        private final Pattern pattern;

        public TemplateRegion(String content) {
            this.variable = null;
            this.content = content;
            this.pattern = null;
        }

        public TemplateRegion(String variable, String content) {
            this.variable = variable;
            this.content = content;

            if (content != null) {
                this.pattern = Pattern.compile(content);
            } else {
                this.pattern = null;
            }
        }

        public boolean isFixedContent() {
            return variable == null;
        }

        public boolean isDefaultPattern() {
            return content == null;
        }

        public int match(String uri, int start) {
            if (isFixedContent()) {
                return matchFixedContent(uri, start);
            } else {
                if (isDefaultPattern()) {
                    return matchDefaultPatternContent(uri, start);
                } else {
                    return matchCustomPatternContent(uri, start);
                }
            }
        }

        private int matchCustomPatternContent(String uri, int start) {
            Matcher matcher = pattern.matcher(uri);
            if (matcher.find(start) && matcher.start() == start) {
                return matcher.end();
            }

            return -1;
        }

        private int matchDefaultPatternContent(String uri, int start) {
            int position = start;
            int uriLength = uri.length();
            while (position < uriLength && uri.charAt(position) != '/') {
                position++;
            }

            return position;
        }

        private int matchFixedContent(String uri, int start) {
            if (start + content.length() > uri.length()) {
                return -1;
            }

            if (uri.substring(start, start + content.length()).equals(content)) {
                return start + content.length();
            } else {
                return -1;
            }
        }
    }

    private final List<TemplateRegion> regions = new ArrayList<>();

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
                regions.add(new TemplateRegion(template.substring(previousRegion, regionStart)));
            }

            addMatchedVariableRegion(matcher);

            previousRegion = regionEnd;
        }

        if (previousRegion < template.length()) {
            regions.add(new TemplateRegion(template.substring(previousRegion)));
        }
    }

    private void addMatchedVariableRegion(Matcher matcher) {
        String variableName = matcher.group(1);
        String regex = matcher.group(3);

        if (regex == null) {
            regions.add(new TemplateRegion(variableName, null));
        } else {
            regions.add(new TemplateRegion(variableName, regex));
        }
    }

    public Map<String, String> match(String uri) {
        uri = stripSlashes(uri);

        HashMap<String, String> matches = null;

        int uriLength = uri.length();
        int position = 0;
        for (TemplateRegion region : regions) {
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

                matches.put(region.variable, uri.substring(position, regionEnd));
            }

            position = regionEnd;
        }

        if (matches == null) {
            return Collections.emptyMap();
        }

        return matches;
    }
}
