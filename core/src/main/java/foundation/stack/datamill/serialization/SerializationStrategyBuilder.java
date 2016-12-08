package foundation.stack.datamill.serialization;

import foundation.stack.datamill.Pair;
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
    private final List<Outline<?>> outlines = new ArrayList<>();

    private final List<Pair<
            Func1<OutlineBuilder, Outline<SourceType>>,
            Func3<StructuredOutput<?>, SourceType, Outline<?>, StructuredOutput<?>>>> strategies = new ArrayList<>();

    public SerializationStrategy<SourceType> build() {
        return (target, source) -> {
            for (int i = 0; i < strategies.size(); i++) {
                Pair<Func1<OutlineBuilder, Outline<SourceType>>,
                        Func3<StructuredOutput<?>, SourceType, Outline<?>, StructuredOutput<?>>> strategy =
                        strategies.get(i);
                Outline<?> outline = outlines.get(i);
                if (outline == null) {
                    outline = strategy.getFirst().call(OutlineBuilder.CAMEL_CASED);
                    outlines.set(i, outline);
                }

                strategy.getSecond().call(target, source, outline);
            }

            return target;
        };
    }

    public SerializationStrategyBuilder<SourceType> withOutline(
            Func1<OutlineBuilder, Outline<SourceType>> outlineBuilder,
            Func3<StructuredOutput<?>, SourceType, Outline<SourceType>, StructuredOutput<?>> strategy) {
        strategies.add(new Pair(outlineBuilder, strategy));
        outlines.add(null);
        return this;
    }
}
