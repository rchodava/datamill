package foundation.stack.datamill.db;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface InsertBuilder {
    <T> InsertSuffixBuilder values(Collection<T> values, BiFunction<RowBuilder, T, Map<String, ?>> constructor);
    InsertSuffixBuilder values(Map<String, ?>... values);
    InsertSuffixBuilder row(Function<RowBuilder, Map<String, ?>> constructor);
}
