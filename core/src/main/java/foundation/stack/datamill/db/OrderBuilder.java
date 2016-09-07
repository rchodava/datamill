package foundation.stack.datamill.db;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface OrderBuilder<R> {
    ConjoinedOrderBuilder<R> desc();
    ConjoinedOrderBuilder<R> asc();
}
