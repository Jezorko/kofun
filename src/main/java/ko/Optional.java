package ko;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.*;
import java.util.function.Predicate;

/**
 * Equivalent of {@link java.util.Optional} in a more extensible form of an interface.
 *
 * @param <ValueType> the type of optional value
 */
public interface Optional<ValueType> extends Iterable<ValueType> {

    Optional<?> EMPTY = new EmptyOptional();

    @NotNull
    @SuppressWarnings("unchecked")
    static <Value, OptionalType extends Optional<Value>> OptionalType empty() {
        return (OptionalType) EMPTY;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    static <Value, OptionalType extends Optional<Value>> OptionalType optional(Value value) {
        return value == null ? empty() : (OptionalType) new FullOptional<>(value);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    static <Value, OptionalType extends Optional<Value>> OptionalType fromJavaOptional(@NotNull java.util.Optional<Value> optionalValue) {
        return (OptionalType) optionalValue.map(Optional::optional)
                                           .orElseGet(Optional::empty);
    }

    @NotNull
    ValueType get();

    boolean isPresent();

    @NotNull
    default <AnyValue, NewOptionalType extends Optional<AnyValue>> NewOptionalType createFrom(AnyValue anyValue) {
        return optional(anyValue);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    default <OptionalType extends Optional<ValueType>> OptionalType retype() {
        return (OptionalType) this;
    }

    @NotNull
    default <OptionalType extends Optional<ValueType>> OptionalType onPresent(@NotNull Consumer<? super ValueType> optionalValueConsumer) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
        return retype();
    }

    @NotNull
    default <OptionalType extends Optional<ValueType>> OptionalType onEmpty(@NotNull Runnable onEmptyAction) {
        if (!isPresent()) {
            onEmptyAction.run();
        }
        return retype();
    }

    default void ifPresent(@NotNull Consumer<? super ValueType> optionalValueConsumer) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
    }

    default void ifEmpty(@NotNull Runnable onEmptyAction) {
        if (!isPresent()) {
            onEmptyAction.run();
        }
    }

    default void ifPresentOrElse(@NotNull Consumer<? super ValueType> optionalValueConsumer, @NotNull Runnable onEmptyAction) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
        else {
            onEmptyAction.run();
        }
    }

    @NotNull
    default <OptionalType extends Optional<ValueType>> OptionalType filter(@NotNull Predicate<ValueType> predicate) {
        if (isPresent() && predicate.test(get())) {
            return retype();
        }
        else {
            return empty();
        }
    }

    @NotNull
    default <NewValueType, NewOptionalType extends Optional<NewValueType>> NewOptionalType map(@NotNull Function<? super ValueType, ? extends NewValueType> mappingFunction) {
        return isPresent() ? createFrom(mappingFunction.apply(get())) : empty();
    }

    @NotNull
    default <NewValueType, NewOptionalType extends Optional<NewValueType>> NewOptionalType flatMap(@NotNull Function<? super ValueType, NewOptionalType> mappingFunction) {
        if (isPresent()) {
            final NewOptionalType newOptional = mappingFunction.apply(get());
            Objects.requireNonNull(newOptional, "Optional flat mapping resulted in a null object");
            return newOptional;
        }
        else {
            return empty();
        }
    }

    @NotNull
    default <OtherValueType, NewValueType, NewOptionalType extends Optional<NewValueType>>
    NewOptionalType mergeWith(@NotNull Optional<OtherValueType> other,
                              @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent() && other.isPresent()) {
            return createFrom(mergeFunction.apply(get(), other.get()));
        }
        else {
            return empty();
        }
    }

    @NotNull
    default <OtherValueType, NewValueType, NewOptionalType extends Optional<NewValueType>>
    NewOptionalType mergeWith(@NotNull Optional<OtherValueType> other,
                              @NotNull Function<? super ValueType, ? extends NewValueType> mergeFallback,
                              @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent() && other.isPresent()) {
            return createFrom(mergeFunction.apply(get(), other.get()));
        }
        else if (isPresent()) {
            return createFrom(mergeFallback.apply(get()));
        }
        else {
            return empty();
        }
    }

    @NotNull
    default <OtherValueType, NewValueType, NewOptionalType extends Optional<NewValueType>>
    NewOptionalType mergeWith(@NotNull Optional<OtherValueType> other,
                              @NotNull Function<? super ValueType, ? extends NewValueType> thisMergeFallback,
                              @NotNull Function<? super OtherValueType, ? extends NewValueType> otherMergeFallback,
                              @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent() && other.isPresent()) {
            return createFrom(mergeFunction.apply(get(), other.get()));
        }
        else if (isPresent()) {
            return createFrom(thisMergeFallback.apply(get()));
        }
        else if (other.isPresent()) {
            return createFrom(otherMergeFallback.apply(other.get()));
        }
        else {
            return empty();
        }
    }

    default <OptionalType extends Optional<ValueType>> OptionalType or(ValueType alternativeValue) {
        return isPresent() ? retype() : createFrom(alternativeValue);
    }

    default <OptionalType extends Optional<ValueType>> OptionalType orGet(@NotNull Supplier<? extends ValueType> alternativeValueSupplier) {
        return isPresent() ? retype() : createFrom(alternativeValueSupplier.get());
    }

    default <ExceptionType extends Throwable, OptionalType extends Optional<ValueType>> OptionalType orThrow(
            @NotNull Supplier<? extends ExceptionType> exceptionSupplier) throws ExceptionType {
        if (isPresent()) {
            return retype();
        }
        else {
            final ExceptionType exception = exceptionSupplier.get();
            Objects.requireNonNull(exception, "Supplied exception is null");
            throw exception;
        }
    }

    default ValueType orElseNull() {
        return orElse(null);
    }

    @Contract(value = "null -> null", pure = true)
    default ValueType orElse(ValueType alternativeValue) {
        return isPresent() ? get() : alternativeValue;
    }

    default ValueType orElseGet(@NotNull Supplier<? extends ValueType> alternativeValueSupplier) {
        return isPresent() ? get() : alternativeValueSupplier.get();
    }

    default <ExceptionType extends Throwable> ValueType orElseThrow(@NotNull Supplier<? extends ExceptionType> exceptionSupplier) throws ExceptionType {
        if (isPresent()) {
            return get();
        }
        else {
            final ExceptionType exception = exceptionSupplier.get();
            Objects.requireNonNull(exception, "Supplied exception is null");
            throw exception;
        }
    }

    default java.util.Optional<ValueType> toJavaOptional() {
        return isPresent() ? java.util.Optional.of(get()) : java.util.Optional.empty();
    }

    @NotNull
    @Override
    default Iterator<ValueType> iterator() {
        return isPresent() ?
               Collections.singleton(get())
                          .iterator() :
               Collections.emptyIterator();
    }

}
