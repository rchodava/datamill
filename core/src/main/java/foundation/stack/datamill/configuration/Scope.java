package foundation.stack.datamill.configuration;

import java.util.Collection;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Scope {
    <T, R extends T> R resolve(
            Wiring wiring,
            QualifyingFactory<T, R> factory,
            Class<? extends T> type,
            Collection<String> qualifiers);
}
