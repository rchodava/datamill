package org.chodavarapu.datamill.http.matching;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface UriMatchingChain {
    /**
     * Try to match the URI against the specified pattern.
     *
     * @param pattern Pattern to match the URI against. The pattern can include templates in the JAX-RS @Path annotation
     *                style. See https://jax-rs-spec.java.net/nonav/2.0/apidocs/javax/ws/rs/Path.html#value() for a
     *                description of the syntax for these templates. For example, these can look like: "/users/{id}",
     *                "/users/{name:\w+}".
     */
    GuardedHandler elseIfUriMatches(String pattern);
}
