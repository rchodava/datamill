package foundation.stack.datamill.cucumber;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Phrases {
    String HTTP_BODY = "(?:entity|body|payload)";
    String HTTP_METHOD = "(GET|POST|PUT|PATCH|DELETE)";
    String HTTP_REQUEST = "(?:request|call)";
    String OPTIONAL_PLURAL = "(?:s)?";
    String PROPERTY_KEY = "(\\w+)";
    String SUBJECT = "(?:we|he|she|the user|a user|another user|the other user)";
}
