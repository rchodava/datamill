package foundation.stack.datamill;

/**
 * @author Israel Colomer (israelcolomer@gmail.com)
 */
public interface ProcessOutputListener {
    void output(String message, boolean errorStream);
}