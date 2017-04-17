package foundation.stack.datamill.configuration;

import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class WiringException extends RuntimeException {
    private final List<WiringException> components;

    public WiringException(String message) {
        this(message, null);
    }

    public WiringException(String message, List<WiringException> components) {
        super(message);

        this.components = components;
    }

    public List<WiringException> getComponents() {
        return components;
    }

    private void indent(StringBuilder formatted, int indent) {
        for (int i = 0; i < indent; i++) {
            formatted.append("    ");
        }

        formatted.append(" |- ");
    }

    public String toString(int indent) {
        StringBuilder formatted = new StringBuilder();

        if (getMessage() != null) {
            indent(formatted, indent);
            formatted.append(getMessage());
        }

        if (components != null && components.size() > 0) {
            for (WiringException component : components) {
                formatted.append('\n');
                formatted.append(component.toString(indent + 1));
            }
        }

        return formatted.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
