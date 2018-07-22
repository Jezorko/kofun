package ko.prototypes;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface PredicatePrototype<TestedType, NewPredicateType extends PredicatePrototype> extends FluentPrototype<NewPredicateType> {

    /**
     * Tests given value against this predicate.
     * Equivalent of {@link Predicate#test(Object)}.
     *
     * @param testedValue to be tested
     *
     * @return true if value matches predicate, otherwise false
     */
    boolean test(TestedType testedValue);

    /**
     * Changes type of the given predicate to generic type.
     * This method may be used by all methods marked with {@link ExtensibleFluentChain}
     * which require type change.
     *
     * @param predicate to have its' type changed
     *
     * @return same predicate with changed type
     */
    @NotNull
    @ExtensibleFluentChain
    @SuppressWarnings("unchecked")
    default NewPredicateType recreate(PredicatePrototype<TestedType, ?> predicate) {
        return (NewPredicateType) predicate;
    }

    /**
     * Negates this predicate with NOT boolean function.
     * Equivalent of {@link Predicate#negate()}.
     *
     * @return a new predicate
     */
    @NotNull
    @ExtensibleFluentChain
    NewPredicateType negate();

    /**
     * Combines other predicate with AND boolean function.
     * Equivalent of {@link java.util.function.Predicate#and(Predicate)}.
     *
     * @param other predicate to be combined with
     *
     * @return a new predicate
     */
    @NotNull
    @ExtensibleFluentChain
    NewPredicateType and(java.util.function.Predicate<TestedType> other);

    /**
     * Combines other predicate with OR boolean function.
     * Equivalent of {@link java.util.function.Predicate#or(Predicate)}.
     *
     * @param other predicate to be combined with
     *
     * @return a new predicate
     */
    @NotNull
    @ExtensibleFluentChain
    NewPredicateType or(java.util.function.Predicate<TestedType> other);

    /**
     * Combines other predicate with XOR boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new {@link ko.Predicate}
     */
    default NewPredicateType xor(java.util.function.Predicate<? super TestedType> other) {
        return recreate(testedValue -> this.test(testedValue) ^ other.test(testedValue));
    }

    /**
     * Combines other predicate with NAND boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new predicate
     */
    @NotNull
    @ExtensibleFluentChain
    default NewPredicateType nand(java.util.function.Predicate<? super TestedType> other) {
        return recreate(testedValue -> negate().test(testedValue) || other.negate().test(testedValue));
    }

    /**
     * Combines other predicate with NOR boolean function.
     *
     * @param other predicate to be combined with
     *
     * @return a new predicate
     */
    @NotNull
    @ExtensibleFluentChain
    default NewPredicateType nor(java.util.function.Predicate<? super TestedType> other) {
        return recreate(testedValue -> !(test(testedValue) || other.test(testedValue)));
    }

}
