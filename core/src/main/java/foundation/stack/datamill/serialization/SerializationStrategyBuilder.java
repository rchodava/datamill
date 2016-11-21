package foundation.stack.datamill.serialization;

import foundation.stack.datamill.reflection.Outline;
import foundation.stack.datamill.reflection.OutlineBuilder;
import rx.functions.Func1;
import rx.functions.Func3;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public class SerializationStrategyBuilder<SourceType> {
    private final List<Func3<StructuredOutput<?>, SourceType, Outline<?>, StructuredOutput<?>>> strategies = new ArrayList<>();

    public SerializationStrategyBuilder<SourceType> withOutline(
            Func1<OutlineBuilder, Outline<SourceType>> outlineBuilder,
            Func3<StructuredOutput<?>, SourceType, Outline<SourceType>, StructuredOutput<?>> strategy) {
        Outline<SourceType> outline = outlineBuilder.call(OutlineBuilder.DEFAULT);
        return this;
    }

    public SerializationStrategy<SourceType> build() {
        return (target, source) -> {
            for (Func3<StructuredOutput<?>, SourceType, Outline<?>, StructuredOutput<?>> strategy : strategies) {
//                strategy.call(source);
            }

            return null;
        };
    }
}
