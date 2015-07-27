package org.chodavarapu.datamill.http.matching;

import com.google.common.collect.ImmutableMap;
import org.chodavarapu.datamill.http.impl.UriTemplate;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class UriTemplateTest {
    @Test
    public void testMatches() {
        assertThat(new UriTemplate("{id}/users/{userId}/").match("12/users/456/"),
                equalTo(ImmutableMap.of("id", "12", "userId", "456")));
        assertThat(new UriTemplate("{id}/users/{userId}/").match("/12/users/456/"),
                equalTo(ImmutableMap.of("id", "12", "userId", "456")));
        assertThat(new UriTemplate("/{id}/users/{userId}/").match("12/users/456"),
                equalTo(ImmutableMap.of("id", "12", "userId", "456")));

        assertThat(new UriTemplate("/{id}/property/").match("/12/property"),
                equalTo(ImmutableMap.of("id", "12")));
        assertThat(new UriTemplate("/{id}/property/").match("/12/property/second"),
                equalTo(ImmutableMap.of("id", "12")));

        assertNull(new UriTemplate("/{id}/property/").match("/12/prop"));
        assertNull(new UriTemplate("/{id}/property/").match("34/12/property"));

        assertThat(new UriTemplate("/users/{userId}/posts/{postId}").match("/users/user_12/posts/Post ID 456/"),
                equalTo(ImmutableMap.of("userId", "user_12", "postId", "Post ID 456")));
        assertThat(new UriTemplate("/users/{userId}/posts/{postId}").match("/users/user_12/posts/Post ID 456/furtherUri/content"),
                equalTo(ImmutableMap.of("userId", "user_12", "postId", "Post ID 456")));

        assertThat(new UriTemplate("{id:\\w+}/users/{userId:\\w+}/").match("/testIdContent/users/testUserIdContent"),
                equalTo(ImmutableMap.of("id", "testIdContent", "userId", "testUserIdContent")));
        assertThat(new UriTemplate("{id:\\w+}/users/{userId:\\w+}").match("/testIdContent/users/testUserIdContent/"),
                equalTo(ImmutableMap.of("id", "testIdContent", "userId", "testUserIdContent")));
        assertNull(new UriTemplate("{id:\\w+}/users/{userId:\\w+}/").match("/testIdContent.32/users/testUserIdContent"));
    }
}
