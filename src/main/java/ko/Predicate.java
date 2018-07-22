package ko;

import ko.prototypes.PredicatePrototype;
import org.jetbrains.annotations.NotNull;

/**
 * Equivalent of {@link java.util.function.Predicate} with additional methods.
 *
 * @param <TestedType> the type of input for predicate
 */
@FunctionalInterface
public interface Predicate<TestedType> extends PredicatePrototype<TestedType, Predicate>, java.util.function.Predicate<TestedType> {

    /**
     * Raw {@link Predicate} for which any value is a match.
     * Do not use this object directly, instead call {@link #alwaysTrue()} for a type-casted version.
     */
    Predicate ALWAYS_TRUE = anyValue -> true;

    /**
     * Raw {@link Predicate} for which no value is a match.
     * Do not use this object directly, instead call {@link #alwaysFalse()} for a type-casted version.
     */
    Predicate ALWAYS_FALSE = anyValue -> false;

    /**
     * Returns a predicate for which any value is a match.
     *
     * @param <T> type of the tested value
     *
     * @return {@link #ALWAYS_TRUE}
     */
    @SuppressWarnings("unchecked")
    static <T> Predicate<T> alwaysTrue() {
        return ALWAYS_TRUE;
    }

    /**
     * Returns a predicate for which no value is a match.
     *
     * @param <T> type of the tested value
     *
     * @return {@link #ALWAYS_FALSE}
     */
    @SuppressWarnings("unchecked")
    static <T> Predicate<T> alwaysFalse() {
        return ALWAYS_FALSE;
    }

    /**
     * Enhances given predicate with methods of {@link Predicate}.
     *
     * @param predicate to be enhanced
     * @param <TestedType>       type of value tested by the predicate
     *
     * @return a new {@link Predicate}
     */
    static <TestedType> Predicate<TestedType> from(java.util.function.Predicate<TestedType> predicate) {
        return predicate::test;
    }

    @Override
    boolean test(TestedType testedValue);

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Predicate<TestedType> negate() {
        return from(java.util.function.Predicate.super.negate());
    }

    @Override
    default Predicate<TestedType> and(java.util.function.Predicate<? super TestedType> other) {
        return from(java.util.function.Predicate.super.and(other));
    }

    @Override
    default Predicate<TestedType> or(java.util.function.Predicate<? super TestedType> other) {
        return from(java.util.function.Predicate.super.or(other));
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Predicate<TestedType> xor(java.util.function.Predicate<? super TestedType> other) {
        return PredicatePrototype.super.xor(other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Predicate<TestedType> nand(java.util.function.Predicate<? super TestedType> other) {
        return PredicatePrototype.super.nand(other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Predicate<TestedType> nor(java.util.function.Predicate<? super TestedType> other) {
        return PredicatePrototype.super.nor(other);
    }


}
