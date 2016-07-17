package foundation.stack.datamill.configuration.impl;

import com.github.davidmoten.rx.Functions;
import rx.functions.Func1;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class EnvironmentPropertiesSource extends AbstractSource {
    public static final EnvironmentPropertiesSource IDENTITY = new EnvironmentPropertiesSource();

    private final Func1<String, String> transformer;

    public EnvironmentPropertiesSource(Func1<String, String> transformer) {
        this.transformer = transformer != null ? transformer : Functions.identity();
    }

    private EnvironmentPropertiesSource() {
        this(null);
    }

    @Override
    public Optional<String> get(String name) {
        return Optional.ofNullable(System.getenv(transformer.call(name)));
    }
}
