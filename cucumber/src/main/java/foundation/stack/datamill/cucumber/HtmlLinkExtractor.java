package foundation.stack.datamill.cucumber;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class HtmlLinkExtractor {

    private Pattern patternTag, patternLink;
    private Matcher matcherTag, matcherLink;

    private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";
    private static final String HTML_A_HREF_TAG_PATTERN =
            "\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))";


    public HtmlLinkExtractor() {
        patternTag = Pattern.compile(HTML_A_TAG_PATTERN);
        patternLink = Pattern.compile(HTML_A_HREF_TAG_PATTERN);
    }

    public List<HtmlLink> extractLinks(final String html) {

        List<HtmlLink> result = new ArrayList<>();

        matcherTag = patternTag.matcher(html);

        while (matcherTag.find()) {

            String href = matcherTag.group(1); // href
            String linkText = matcherTag.group(2); // link text

            matcherLink = patternLink.matcher(href);

            while (matcherLink.find()) {

                String target = matcherLink.group(1); // target
                HtmlLink obj = new HtmlLink();
                obj.setLinkTarget(target);
                obj.setLinkText(linkText);

                result.add(obj);
            }
        }
        return result;
    }

    class HtmlLink {

        String target;
        String linkText;

        HtmlLink() {
        }

        public String getLinkTarget() {
            return target;
        }

        public void setLinkTarget(String link) {
            this.target = replaceInvalidChar(link);
        }

        public String getLinkText() {
            return linkText;
        }

        public void setLinkText(String linkText) {
            this.linkText = linkText;
        }

        private String replaceInvalidChar(String link) {
            link = link.replaceAll("'", "");
            link = link.replaceAll("\"", "");
            return link;
        }
    }
}
