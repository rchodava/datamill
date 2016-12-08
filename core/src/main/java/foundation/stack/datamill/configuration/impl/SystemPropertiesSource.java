package foundation.stack.datamill.configuration.impl;

import com.github.davidmoten.rx.Functions;
import rx.functions.Func1;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SystemPropertiesSource extends AbstractSource {
    public static final SystemPropertiesSource IDENTITY = new SystemPropertiesSource();

    private final Func1<String, String> transformer;

    public SystemPropertiesSource(Func1<String, String> transformer) {
        this.transformer = transformer != null ? transformer : Functions.identity();
    }

    private SystemPropertiesSource() {
        this(null);
    }

    @Override
    public Optional<String> getOptional(String name) {
        return Optional.ofNullable(System.getProperty(transformer.call(name)));
    }
}
