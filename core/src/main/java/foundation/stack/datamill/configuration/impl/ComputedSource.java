package foundation.stack.datamill.configuration.impl;

import rx.functions.Func1;

import java.util.Optional;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class ComputedSource extends AbstractSource {
    private final Func1<String, String> computation;

    public ComputedSource(Func1<String, String> computation) {
        this.computation = computation;
    }

    @Override
    public Optional<String> getOptional(String name) {
        return Optional.ofNullable(computation.call(name));
    }
}
