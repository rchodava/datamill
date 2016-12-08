package foundation.stack.datamill.configuration;

import rx.functions.Action1;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
@FunctionalInterface
public interface Module extends Action1<Wiring> {
}
