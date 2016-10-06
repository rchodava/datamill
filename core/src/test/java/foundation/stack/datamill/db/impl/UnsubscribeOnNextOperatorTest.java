package foundation.stack.datamill.db.impl;

import org.junit.Test;
import rx.Observable;

import static org.junit.Assert.assertEquals;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class UnsubscribeOnNextOperatorTest {
    @Test
    public void operator() {
        int[] unsubscribed = new int[] { 1 };
        int result = Observable.just(1, 2, 3)
                .doOnUnsubscribe(() -> unsubscribed[0] = unsubscribed[0] + 10)
                .lift(new UnsubscribeOnNextOperator<>())
                .doOnUnsubscribe(() -> unsubscribed[0] = unsubscribed[0] * 2)
                .toBlocking()
                .lastOrDefault(0);
        assertEquals(1, result);
        assertEquals(22, unsubscribed[0]);
    }
}
