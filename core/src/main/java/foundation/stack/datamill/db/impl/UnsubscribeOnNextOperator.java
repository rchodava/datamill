package foundation.stack.datamill.db.impl;

import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class UnsubscribeOnNextOperator<T> implements Observable.Operator<T, T> {
    @Override
    public Subscriber<? super T> call(Subscriber<? super T> subscriber) {
        return new Subscriber<T>() {
            private final AtomicBoolean completed = new AtomicBoolean();

            @Override
            public void onCompleted() {
                this.unsubscribe();

                if (!subscriber.isUnsubscribed()) {
                    if (completed.compareAndSet(false, true)) {
                        subscriber.onCompleted();
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                this.unsubscribe();

                if (!subscriber.isUnsubscribed()) {
                    if (completed.compareAndSet(false, true)) {
                        subscriber.onError(e);
                    }
                }
            }

            @Override
            public void onNext(T t) {
                this.unsubscribe();

                if (!subscriber.isUnsubscribed()) {
                    if (completed.compareAndSet(false, true)) {
                        try {
                            subscriber.onNext(t);
                            subscriber.onCompleted();
                        } catch (Throwable e) {
                            Exceptions.throwIfFatal(e);
                            subscriber.onError(e);
                        }
                    }
                }
            }
        };
    }
}
