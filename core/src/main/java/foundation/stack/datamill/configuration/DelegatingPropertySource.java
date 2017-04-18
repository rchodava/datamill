package foundation.stack.datamill.configuration;

import foundation.stack.datamill.configuration.impl.AbstractSource;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class DelegatingPropertySource extends AbstractSource {
    private volatile PropertySource delegate;

    public DelegatingPropertySource(PropertySource delegate) {
        setDelegate(delegate);
    }

    public DelegatingPropertySource() {
        this(null);
    }

    public void setDelegate(PropertySource delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Optional<String> getOptional(String name) {
        return delegate != null ? delegate.get(name) : Optional.empty();
    }
}
