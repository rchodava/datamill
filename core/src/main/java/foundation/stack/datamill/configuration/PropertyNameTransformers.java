package foundation.stack.datamill.configuration;

import com.google.common.base.CaseFormat;
import rx.functions.Func1;

/**
 * Some common property name transformers that are useful when creating {@link PropertySources}.
 */
public class PropertyNameTransformers {    /**
     * Transforms from lower-camel case to upper-underscore case, for example: propertyName -> PROPERTY_NAME.
     */
    public static final Func1<String, String> LOWER_CAMEL_TO_UPPER_UNDERSCORE =
            name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);

    /**
     * Same as calling {@link #leaf(char)} with '/'.
     */
    public static final Func1<String, String> LEAF = leaf('/');

    /**
     * <p>
     * Returns a transformation function that applies the first function, and then the second function on the result
     * of the first. So compose(LEAF, LOWER_CAMEL_TO_UPPER_UNDERSCORE) will return a function that transforms:
     * </p>
     * <p>
     * category/propertyName -> PROPERTY_NAME
     * </p>
     */
    public static final Func1<String, String> compose(Func1<String, String> a, Func1<String, String> b) {
        return name -> b.call(a.call(name));
    }

    /**
     * <p>
     * Returns a transformation function that returns the leaf name of a name separated by the specified separator
     * character. For example:
     * </p>
     * <p>
     * leaf('/') returns a function which transforms: category/propertyName -> propertyName
     * leaf(':') returns a function which transforms: category:propertyName -> propertyName
     * </p>
     */
    public static final Func1<String, String> leaf(char separator) {
        return name -> {
            if (name != null) {
                int leafSeparator = name.lastIndexOf('/');
                if (leafSeparator > 0) {
                    return name.substring(leafSeparator + 1);
                }
            }

            return name;
        };
    }
}
