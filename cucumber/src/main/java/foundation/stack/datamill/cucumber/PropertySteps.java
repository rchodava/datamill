package foundation.stack.datamill.cucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import foundation.stack.datamill.security.impl.BCrypt;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class PropertySteps {
    private final PlaceholderResolver placeholderResolver;
    private final PropertyStore propertyStore;
    private final HtmlLinkExtractor linkExtractor = new HtmlLinkExtractor();

    public PropertySteps(PropertyStore propertyStore, PlaceholderResolver placeholderResolver) {
        this.propertyStore = propertyStore;
        this.placeholderResolver = placeholderResolver;
    }


    @And("^the first link in stored HTML " + Phrases.PROPERTY_KEY + " is stored as " + Phrases.PROPERTY_KEY + "$")
    public void extractFirstLinkFromStoredHtml(String htmlKey, String propertyKey) {
        String html = (String) propertyStore.get(htmlKey);
        List<HtmlLinkExtractor.HtmlLink> links = linkExtractor.extractLinks(html);

        if (!links.isEmpty()) {
            propertyStore.put(propertyKey, links.get(0).getLinkTarget());
        } else {
            fail("Could not find any links in stored HTML!");
        }
    }

    @And("^the regex (.+) is used on stored value " + Phrases.PROPERTY_KEY + " to capture " + Phrases.PROPERTY_KEY + "$")
    public void captureUsingRegex(String regex, String existingProperty, String newProperty) {
        Pattern pattern = Pattern.compile(regex);
        String existingValue = (String) propertyStore.get(existingProperty);

        Matcher matcher = pattern.matcher(existingValue);
        if (matcher.find()) {
            try {
                String newValue = matcher.group(1);
                propertyStore.put(newProperty, newValue);
            } catch (IndexOutOfBoundsException e) {
                fail("The regex " + regex + " must have at least one capture group!");
            }
        } else {
            fail("Could not capture using regex " + regex + " in target " + existingValue);
        }
    }

    @Given("^a random alphanumeric (?:name|string) is stored as " + Phrases.PROPERTY_KEY + "$")
    public void generateRandomAlphanumeric(String key) {
        propertyStore.put(key, RandomGenerator.generateRandomAlphanumeric(16));
    }

    @Given("^" + Phrases.SUBJECT + " store" + Phrases.OPTIONAL_PLURAL + " a random alphanumeric (?:name|string) as " +
            Phrases.PROPERTY_KEY + "$")
    public void generateRandomAlphanumericAlternate(String key) {
        generateRandomAlphanumeric(key);
    }

    @Given("^" + Phrases.SUBJECT + " store" + Phrases.OPTIONAL_PLURAL + " (.+) as " +
            Phrases.PROPERTY_KEY + "$")
    public void storeProperty(String value, String key) {
        String resolvedValue = placeholderResolver.resolve(value);
        propertyStore.put(key, resolvedValue);
    }

    @Given("^" + Phrases.SUBJECT + " hash(?:es)? (.+) (?:using Blowfish |using BCrypt )?and stores it as " +
            Phrases.PROPERTY_KEY + "$")
    public void generatePassword(String password, String key) {
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        propertyStore.put(key, hashed);
    }

    @Given("^" + Phrases.PROPERTY_KEY + " stored value is removed$")
    public void removeProperty(String key) {
        propertyStore.remove(key);
    }
}
