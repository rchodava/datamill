package foundation.stack.datamill.configuration;

import java.util.*;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SingletonScope implements Scope {
    private Map<QualifiedType, Object> constructed = new HashMap<>();

    @Override
    public <T, R extends T> R resolve(
            Wiring wiring,
            QualifyingFactory<T, R> factory,
            Class<? extends T> type,
            Collection<String> qualifiers) {
        Object instance = constructed.get(new QualifiedType(type, qualifiers));
        if (instance == null) {
            instance = factory.call(wiring, type, qualifiers);
            if (instance != null) {
                constructed.put(new QualifiedType(type, qualifiers), instance);
            }
        }

        return (R) instance;
    }

    private static class QualifiedType {
        private final Class<?> type;
        private final String[] qualifiers;

        public QualifiedType(Class<?> type, Collection<String> qualifiers) {
            this.type = type;
            this.qualifiers = qualifiers != null && qualifiers.size() > 0 ?
                    qualifiers.toArray(new String[qualifiers.size()]) : null;
        }

        @Override
        public int hashCode() {
            return qualifiers != null ?
                    Objects.hash(type, Arrays.hashCode(qualifiers)) :
                    type.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof QualifiedType) {
                QualifiedType other = (QualifiedType) obj;
                return qualifiers != null ?
                        Arrays.equals(qualifiers, other.qualifiers) && type == other.type :
                        type == other.type;
            }

            return false;
        }
    }
}
