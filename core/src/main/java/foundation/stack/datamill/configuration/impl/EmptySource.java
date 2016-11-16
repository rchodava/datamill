package foundation.stack.datamill.configuration.impl;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class EmptySource extends AbstractSource {
    public static final EmptySource INSTANCE = new EmptySource();

    @Override
    public Optional<String> getOptional(String name) {
        return Optional.empty();
    }
}
