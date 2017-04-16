package foundation.stack.datamill.configuration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SingletonScope implements Scope {
    private Map<Class<?>, Object> constructed = new HashMap<>();

    @Override
    public <T, R extends T> R resolve(
            Wiring wiring,
            QualifyingFactory<T, R> factory,
            Class<? extends T> type,
            Collection<String> qualifiers) {
        Object instance = constructed.get(type);
        if (instance == null) {
            instance = factory.call(wiring, type, qualifiers);
            if (instance != null) {
                constructed.put(type, instance);
            }
        }

        return (R) instance;
    }
}
