package org.chodavarapu.datamill;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public class Triple<T1, T2, T3> {
    private final T1 first;
    private final T2 second;
    private final T3 third;

    public Triple(T1 first, T2 second, T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    public T3 getThird() {
        return third;
    }
}
