package org.chodavarapu.datamill.db;

import org.chodavarapu.datamill.reflection.Outline;
import org.chodavarapu.datamill.reflection.OutlineBuilder;
import rx.Observable;

import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Repository<T> {
    private Client client;
    private OutlineBuilder outlineBuilder;
    private Class<T> entityClass;

    protected Repository(Client client, OutlineBuilder outlineBuilder, Class<T> entityClass) {
        this.client = client;
        this.outlineBuilder = outlineBuilder;
        this.entityClass = entityClass;
    }

    protected <R> Observable<R> executeQuery(BiFunction<Client, Outline<T>, Observable<R>> executor) {
        return executor.apply(client, outlineBuilder.build(entityClass));
    }
}
