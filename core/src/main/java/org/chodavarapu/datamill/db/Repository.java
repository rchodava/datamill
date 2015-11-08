package org.chodavarapu.datamill.db;

import org.chodavarapu.datamill.reflection.Outline;
import org.chodavarapu.datamill.reflection.OutlineBuilder;

import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Repository<T> {
    private DatabaseClient client;
    private OutlineBuilder outlineBuilder;
    private Class<T> entityClass;

    protected Repository(DatabaseClient client, OutlineBuilder outlineBuilder, Class<T> entityClass) {
        this.client = client;
        this.outlineBuilder = outlineBuilder;
        this.entityClass = entityClass;
    }

    protected <R> R executeQuery(BiFunction<DatabaseClient, Outline<T>, R> executor) {
        return executor.apply(client, outlineBuilder.build(entityClass));
    }
}
