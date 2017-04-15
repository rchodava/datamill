package foundation.stack.datamill.configuration.impl;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class EnvironmentPropertiesSource extends AbstractSource {
    public static final EnvironmentPropertiesSource DEFAULT = new EnvironmentPropertiesSource();

    private EnvironmentPropertiesSource() {
    }

    @Override
    public Optional<String> getOptional(String name) {
        return Optional.ofNullable(System.getenv(name));
    }
}
