package org.chodavarapu.datamill.http.matching;

import org.chodavarapu.datamill.http.impl.UriTemplate;
import org.junit.Test;

import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class UriTemplateTest {
    @Test
    public void testGetProperties() {
        UriTemplate template = new UriTemplate("/{id}/property");
        UriTemplate template2 = new UriTemplate("/users/{userId}/posts/{postId}");
        UriTemplate template3 = new UriTemplate("{id}/users/{userId}/");
        UriTemplate template4 = new UriTemplate("{id:\\w+}/users/{userId:\\w+}/");
        UriTemplate template5 = new UriTemplate("{id:\\w+}/users/{userId:\\w+}/");


        Map<String, String> values = template3.match("12/users/456/");
        Map<String, String> values2 = template3.match("/12/users/456/");
        values = template.match("345/prop");
    }
}
