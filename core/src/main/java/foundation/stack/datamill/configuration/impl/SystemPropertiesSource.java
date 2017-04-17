package foundation.stack.datamill.configuration.impl;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SystemPropertiesSource extends AbstractSource {
    public static final SystemPropertiesSource DEFAULT = new SystemPropertiesSource();

    private SystemPropertiesSource() {
    }

    @Override
    public Optional<String> getOptional(String name) {
        return Optional.ofNullable(System.getProperty(name));
    }
}
