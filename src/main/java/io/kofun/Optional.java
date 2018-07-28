package io.kofun;

import io.kofun.prototypes.OptionalPrototype;
import org.jetbrains.annotations.NotNull;

import java.util.function.*;
import java.util.function.Predicate;

/**
 * Equivalent of {@link java.util.Optional} created by implementing the {@link OptionalPrototype}.
 *
 * @param <ValueType> the type of optional value
 */
public interface Optional<ValueType> extends OptionalPrototype<ValueType, Optional> {

    /**
     * An {@link EmptyOptional} instance.
     * Do not use this object directly, instead call {@link #empty()}.
     */
    Optional<?> EMPTY = new EmptyOptional();

    /**
     * Returns an instance of {@link EmptyOptional}.
     * Equivalent of {@link java.util.Optional#empty()}.
     *
     * @param <ValueType>    the type of optional value
     * @param <OptionalType> the type of {@link Optional} to be returned
     *
     * @return {@link #EMPTY}
     */
    @NotNull
    @SuppressWarnings("unchecked")
    static <ValueType, OptionalType extends Optional<ValueType>> OptionalType empty() {
        return (OptionalType) EMPTY;
    }

    /**
     * Returns an instance of {@link FullOptional} that wraps the given value.
     * Equivalent of {@link java.util.Optional#ofNullable(Object)}.
     *
     * @param value          to be wrapped
     * @param <ValueType>    the type of optional value
     * @param <OptionalType> the type of {@link Optional} to be returned
     *
     * @return a new instance of {@link FullOptional}
     */
    @NotNull
    @SuppressWarnings("unchecked")
    static <ValueType, OptionalType extends Optional<ValueType>> OptionalType optional(ValueType value) {
        return value == null ? empty() : (OptionalType) new FullOptional<>(value);
    }

    /**
     * Converts {@link java.util.Optional} to {@link Optional} instance.
     *
     * @param optionalValue  to be converted
     * @param <ValueType>    the type of optional value
     * @param <OptionalType> the type of {@link Optional} to be returned
     *
     * @return either a new {@link FullOptional} or an {@link EmptyOptional}
     */
    @NotNull
    @SuppressWarnings("unchecked")
    static <ValueType, OptionalType extends Optional<ValueType>> OptionalType fromJavaOptional(@NotNull java.util.Optional<ValueType> optionalValue) {
        return (OptionalType) optionalValue.map(Optional::optional)
                                           .orElseGet(Optional::empty);
    }

    @NotNull
    @Override
    default <AnyValueType> Optional<AnyValueType> recreateFull(AnyValueType anyValue) {
        return optional(anyValue);
    }

    @NotNull
    @Override
    default <AnyValueType> Optional<AnyValueType> recreateEmpty() {
        return empty();
    }

    /* Reimplementing @ExtensibleFluentChain methods */

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Optional<ValueType> onPresent(@NotNull Consumer<? super ValueType> optionalValueConsumer) {
        return OptionalPrototype.super.onPresent(optionalValueConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Optional<ValueType> onEmpty(@NotNull Runnable onEmptyAction) {
        return OptionalPrototype.super.onEmpty(onEmptyAction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Optional<ValueType> filter(@NotNull Predicate<ValueType> predicate) {
        return OptionalPrototype.super.filter(predicate);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewValueType> Optional<NewValueType> map(@NotNull Function<? super ValueType, ? extends NewValueType> mappingFunction) {
        return OptionalPrototype.super.map(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewValueType> Optional<NewValueType> flatMap(@NotNull Function<? super ValueType, ? extends OptionalPrototype<NewValueType, ?>> mappingFunction) {
        return OptionalPrototype.super.flatMap(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <OtherValueType, NewValueType> Optional<ValueType> mergeWith(@NotNull OptionalPrototype<OtherValueType, ?> other,
                                                                         @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        return OptionalPrototype.super.mergeWith(other, mergeFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <OtherValueType, NewValueType> Optional<ValueType> mergeWith(@NotNull OptionalPrototype<OtherValueType, ?> other,
                                                                         @NotNull Function<? super ValueType, ? extends NewValueType> mergeFallback,
                                                                         @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        return OptionalPrototype.super.mergeWith(other, mergeFallback, mergeFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <OtherValueType, NewValueType> Optional<ValueType> mergeWith(@NotNull OptionalPrototype<OtherValueType, ?> other,
                                                                         @NotNull Function<? super ValueType, ? extends NewValueType> thisMergeFallback,
                                                                         @NotNull Function<? super OtherValueType, ? extends NewValueType> otherMergeFallback,
                                                                         @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        return OptionalPrototype.super.mergeWith(other, thisMergeFallback, otherMergeFallback, mergeFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <FirstValueType, SecondValueType, NewValueType> Optional<ValueType> explodeAndMerge(
            @NotNull Function<? super ValueType, ? extends FirstValueType> firstComponentExtractor,
            @NotNull Function<? super ValueType, ? extends SecondValueType> secondComponentExtractor,
            @NotNull BiFunction<? super FirstValueType, ? super SecondValueType, ?
                    extends NewValueType> mergeFunction) {
        return OptionalPrototype.super.explodeAndMerge(firstComponentExtractor, secondComponentExtractor, mergeFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <FirstValueType, SecondValueType, NewValueType> Optional<ValueType> explodeAndMerge(
            @NotNull Function<? super ValueType, ? extends FirstValueType> firstComponentExtractor,
            @NotNull Function<? super ValueType, ? extends SecondValueType> secondComponentExtractor,
            @NotNull Function<? super FirstValueType, ? extends NewValueType> mergeFallback,
            @NotNull BiFunction<? super FirstValueType, ? super SecondValueType, ?
                    extends NewValueType> mergeFunction) {
        return OptionalPrototype.super.explodeAndMerge(firstComponentExtractor, secondComponentExtractor, mergeFallback, mergeFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <FirstValueType, SecondValueType, NewValueType> Optional<ValueType> explodeAndMerge(
            @NotNull Function<? super ValueType, ? extends FirstValueType> firstComponentExtractor,
            @NotNull Function<? super ValueType, ? extends SecondValueType> secondComponentExtractor,
            @NotNull Function<? super FirstValueType, ? extends NewValueType> firstComponentMergeFallback,
            @NotNull Function<? super SecondValueType, ? extends NewValueType> secondComponentMergeFallback,
            @NotNull BiFunction<? super FirstValueType, ? super SecondValueType, ?
                    extends NewValueType> mergeFunction) {
        return OptionalPrototype.super.explodeAndMerge(firstComponentExtractor, secondComponentExtractor, firstComponentMergeFallback, secondComponentMergeFallback, mergeFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Optional<ValueType> or(ValueType alternativeValue) {
        return OptionalPrototype.super.or(alternativeValue);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Optional<ValueType> orGet(@NotNull Supplier<? extends ValueType> alternativeValueSupplier) {
        return OptionalPrototype.super.orGet(alternativeValueSupplier);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ExceptionType extends Throwable> Optional<ValueType> orThrow(@NotNull Supplier<? extends ExceptionType> exceptionSupplier) throws ExceptionType {
        return OptionalPrototype.super.orThrow(exceptionSupplier);
    }

}
