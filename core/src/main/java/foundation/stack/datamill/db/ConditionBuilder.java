package foundation.stack.datamill.db;

import foundation.stack.datamill.reflection.Member;
import rx.functions.Func1;

import java.util.Collection;

/**
 * @author Ravi Chodavarapu (rchodava@gmail.com)
 */
public interface ConditionBuilder {
    <T> ConjunctionBuilder eq(String column, T value);
    <T> ConjunctionBuilder eq(String table, String column, T value);
    <T> ConjunctionBuilder eq(Member member, T value);

    <T> ConjunctionBuilder lt(String column, T value);
    <T> ConjunctionBuilder lt(String table, String column, T value);
    <T> ConjunctionBuilder lt(Member member, T value);

    <T> ConjunctionBuilder gt(String column, T value);
    <T> ConjunctionBuilder gt(String table, String column, T value);
    <T> ConjunctionBuilder gt(Member member, T value);

    <T> ConjunctionBuilder is(String column, T value);
    <T> ConjunctionBuilder is(String table, String column, T value);
    <T> ConjunctionBuilder is(Member member, T value);

    <T> ConjunctionBuilder in(String column, Collection<T> value);
    <T> ConjunctionBuilder in(String table, String column, Collection<T> value);
    <T> ConjunctionBuilder in(Member member, Collection<T> value);

    TerminalCondition and(
            Func1<ConditionBuilder, TerminalCondition> left,
            Func1<ConditionBuilder, TerminalCondition> right);

    TerminalCondition or(
            Func1<ConditionBuilder, TerminalCondition> left,
            Func1<ConditionBuilder, TerminalCondition> right);
}
