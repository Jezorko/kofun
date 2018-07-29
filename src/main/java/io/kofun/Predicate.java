package io.kofun;

/**
 * Equivalent of {@link java.util.function.Predicate} with additional methods.
 *
 * @param <T> the type of input for predicate
 */
@FunctionalInterface
public interface Predicate<T> extends java.util.function.Predicate<T> {

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
    public static <T> Predicate<T> alwaysTrue() {
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
    public static <T> Predicate<T> alwaysFalse() {
        return ALWAYS_FALSE;
    }

    /**
     * Enhances given predicate with methods of {@link Predicate}.
     *
     * @param predicate to be enhanced
     * @param <T>       type of value tested by the predicate
     *
     * @return a new {@link Predicate}
     */
    static <T> Predicate<T> from(java.util.function.Predicate<T> predicate) {
        return predicate::test;
    }

    /**
     * Combines other predicate with XOR boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new {@link Predicate}
     */
    default Predicate<? extends T> xor(java.util.function.Predicate<T> other) {
        return t -> this.test(t) ^ other.test(t);
    }

    /**
     * Combines other predicate with NAND boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new {@link Predicate}
     */
    default Predicate<? extends T> nand(java.util.function.Predicate<T> other) {
        return t -> this.negate()
                        .test(t) || other.negate()
                                         .test(t);
    }

    /**
     * Combines other predicate with NOR boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new {@link Predicate}
     */
    default Predicate<? extends T> nor(java.util.function.Predicate<T> other) {
        return t -> !(this.test(t) || other.test(t));
    }

}
