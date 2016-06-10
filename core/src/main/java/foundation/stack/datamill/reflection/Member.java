package foundation.stack.datamill.reflection;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface Member {
    String name();
    String camelCasedName();
    String snakeCasedName();

    Outline<?> outline();
}
