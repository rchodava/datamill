package foundation.stack.datamill.configuration.impl;

import com.github.davidmoten.rx.Functions;
import rx.functions.Func1;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SystemPropertiesSource extends AbstractSource {
    private final Func1<String, String> transformer;

    public SystemPropertiesSource(Func1<String, String> transformer) {
        this.transformer = transformer != null ? transformer : Functions.identity();
    }

    public SystemPropertiesSource() {
        this(null);
    }

    @Override
    public Optional<String> get(String name) {
        return Optional.ofNullable(System.getProperty(transformer.call(name)));
    }
}
