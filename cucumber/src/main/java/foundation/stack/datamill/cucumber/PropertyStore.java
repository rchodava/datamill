package foundation.stack.datamill.cucumber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class PropertyStore {
    private final static Logger logger = LoggerFactory.getLogger(PropertyStore.class);

    private final Map<String, Object> store = new HashMap<>();

    public void put(String key, Object value) {
        logger.debug("Adding property {}={}", key, value != null ? value.toString() : "null");
        store.put(key, value);
    }

    public void remove(String key) {
        logger.debug("Removing property {}", key);
        store.remove(key);
    }

    public Object get(String key) {
        return store.get(key);
    }

    public boolean contains(String key) {
        return store.containsKey(key);
    }

    public Collection<Object> values() {
        return store.values();
    }
}
