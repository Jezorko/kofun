package ko;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Optional<Value> extends Iterable<Value> {

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

    Value get();

    boolean isPresent();

    @NotNull
    <AnyValue, NewOptionalType extends Optional<AnyValue>> NewOptionalType createFrom(AnyValue anyValue);

    @SuppressWarnings("unchecked")
    default <OptionalType extends Optional<Value>> OptionalType retype() {
        return (OptionalType) this;
    }

    @NotNull
    default <OptionalType extends Optional<Value>> OptionalType onPresent(@NotNull Consumer<? super Value> optionalValueConsumer) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
        return retype();
    }

    @NotNull
    default <OptionalType extends Optional<Value>> OptionalType onEmpty(@NotNull Runnable onEmptyAction) {
        if (!isPresent()) {
            onEmptyAction.run();
        }
        return retype();
    }

    default void ifPresent(@NotNull Consumer<? super Value> optionalValueConsumer) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
    }

    default void ifEmpty(@NotNull Runnable onEmptyAction) {
        if (!isPresent()) {
            onEmptyAction.run();
        }
    }

    default void ifPresentOrElse(@NotNull Consumer<? super Value> optionalValueConsumer, @NotNull Runnable onEmptyAction) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
        else {
            onEmptyAction.run();
        }
    }

    default <OptionalType extends Optional<Value>> OptionalType filter(@NotNull Predicate<Value> predicate) {
        if (isPresent() && predicate.test(get())) {
            return retype();
        }
        else {
            return empty();
        }
    }

    default <NewValue, NewOptionalType extends Optional<NewValue>> NewOptionalType map(@NotNull Function<? super Value, ? extends NewValue> mappingFunction) {
        return isPresent() ? createFrom(mappingFunction.apply(get())) : empty();
    }

    default <NewValue, NewOptionalType extends Optional<NewValue>> NewOptionalType flatMap(@NotNull Function<? super Value, NewOptionalType> mappingFunction) {
        if (isPresent()) {
            final NewOptionalType newOptional = mappingFunction.apply(get());
            Objects.requireNonNull(newOptional, "Optional flat mapping resulted in a null object");
            return newOptional;
        }
        else {
            return empty();
        }
    }

    default <OptionalType extends Optional<Value>> OptionalType or(Value alternativeValue) {
        return isPresent() ? retype() : createFrom(alternativeValue);
    }

    default <OptionalType extends Optional<Value>> OptionalType orGet(@NotNull Supplier<? extends Value> alternativeValueSupplier) {
        return isPresent() ? retype() : createFrom(alternativeValueSupplier.get());
    }

    default <ExceptionType extends Throwable, OptionalType extends Optional<Value>> OptionalType orThrow(
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

    default Value orElseNull() {
        return orElse(null);
    }

    @Contract(value = "null -> null", pure = true)
    default Value orElse(Value alternativeValue) {
        return isPresent() ? get() : alternativeValue;
    }

    default Value orElseGet(@NotNull Supplier<? extends Value> alternativeValueSupplier) {
        return isPresent() ? get() : alternativeValueSupplier.get();
    }

    default <ExceptionType extends Throwable> Value orElseThrow(@NotNull Supplier<? extends ExceptionType> exceptionSupplier) throws ExceptionType {
        if (isPresent()) {
            return get();
        }
        else {
            final ExceptionType exception = exceptionSupplier.get();
            Objects.requireNonNull(exception, "Supplied exception is null");
            throw exception;
        }
    }

    default java.util.Optional<Value> toJavaOptional() {
        return isPresent() ? java.util.Optional.of(get()) : java.util.Optional.empty();
    }

    @NotNull
    @Override
    default Iterator<Value> iterator() {
        return isPresent() ?
               Collections.singleton(get())
                          .iterator() :
               Collections.emptyIterator();
    }

    // TODO: move to "Optionals" static methods collection
    static boolean equals(Optional that, Optional other) {
        if (!that.isPresent() && !other.isPresent()) {
            return true;
        }
        else if (that.isPresent() && other.isPresent()) {
            return Objects.equals(that.get(), other.get());
        }
        else {
            return false;
        }
    }

}
