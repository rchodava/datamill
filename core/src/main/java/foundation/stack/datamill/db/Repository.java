package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.reflection.OutlineBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class Repository<T> {
    private static final Logger logger = LoggerFactory.getLogger(Repository.class);

    private DatabaseClient client;
    private OutlineBuilder outlineBuilder;
    protected Class<T> entityClass;
    protected Outline<T> outline;

    protected Repository(DatabaseClient client, OutlineBuilder outlineBuilder, Class<T> entityClass) {
        this.client = client;
        this.outlineBuilder = outlineBuilder;
        this.entityClass = entityClass;
        this.outline = buildOutline(entityClass);
    }

    protected <E> Outline<E> buildOutline(Class<E> entityClass) {
        return outlineBuilder.defaultSnakeCased().build(entityClass);
    }

    protected <R> R executeQuery(BiFunction<DatabaseClient, Outline<T>, R> executor) {
        try {
            return executor.apply(client, outline);
        } catch (Throwable t) {
            logger.debug("An error occurred while building a SQL query!", t);
            throw t;
        }
    }
}
