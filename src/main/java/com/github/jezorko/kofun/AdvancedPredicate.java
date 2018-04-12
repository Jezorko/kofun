package com.github.jezorko.kofun;

import java.util.function.Predicate;

public interface AdvancedPredicate<T> extends Predicate<T> {

    AdvancedPredicate ALWAYS_TRUE = anyValue -> true;
    AdvancedPredicate ALWAYS_FALSE = anyValue -> false;

    /**
     * Returns a predicate for which any value is a match.
     *
     * @param <T> type of the tested value
     *
     * @return {@link #ALWAYS_TRUE}
     */
    @SuppressWarnings("unchecked")
    static <T> AdvancedPredicate<T> alwaysTrue() {
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
    static <T> AdvancedPredicate<T> alwaysFalse() {
        return ALWAYS_FALSE;
    }

    /**
     * Enhances given predicate with methods of {@link AdvancedPredicate}.
     *
     * @param predicate to be enhanced
     * @param <T>       type of value tested by the predicate
     *
     * @return a new {@link AdvancedPredicate}
     */
    static <T> AdvancedPredicate<T> from(Predicate<? super T> predicate) {
        return predicate::test;
    }

    /**
     * Combines other predicate with XOR boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new {@link AdvancedPredicate}
     */
    default AdvancedPredicate<? extends T> xor(Predicate<? super T> other) {
        return t -> this.test(t) ^ other.test(t);
    }

    /**
     * Combines other predicate with NAND boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new {@link AdvancedPredicate}
     */
    default AdvancedPredicate<? extends T> nand(Predicate<? super T> other) {
        return t -> this.negate()
                        .test(t) || other.negate()
                                         .test(t);
    }

    /**
     * Combines other predicate with NOR boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new {@link AdvancedPredicate}
     */
    default AdvancedPredicate<? extends T> nor(Predicate<? super T> other) {
        return t -> !(this.test(t) || other.test(t));
    }

}
