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
 * Implementors of this class should respect the following contract:<br/>
 * * {@link #get()} method should return a value only if it exists<br/>
 * * {@link #get()} method should throw if value does not exist<br/>
 * * {@link #isPresent()} method should return true if value exists and false if value is absent<br/>
 * * {@link #createFrom(Object)} method should create a new optional of the implementors' type<br/>
 * Additionally, every method annotated with {@link ExtensibleFluentChain} must be implemented
 * as described by the annotation's documentation.
 *
 * @param <ValueType> the type of optional value
 */
public interface Optional<ValueType> extends Iterable<ValueType> {

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

    /**
     * Returns the wrapped optional value.
     * Should not be called without checking for the presence of the value first
     * using {@link #isPresent()}.
     * If the value is not present, this method will always throw.
     * Equivalent of {@link java.util.Optional#get()}.
     *
     * @return the optional value if it is present
     */
    @NotNull
    ValueType get();

    /**
     * Equivalent of {@link java.util.Optional#isPresent()}.
     *
     * @return true if the wrapped optional value is present, false otherwise
     */
    boolean isPresent();

    /**
     * Allows creation of a new {@link Optional} from the given value.
     * This method is used by all methods marked with {@link ExtensibleFluentChain}
     * which require creating a new instance.
     *
     * @param anyValue          the optional value
     * @param <AnyValueType>    the type of the optional value
     * @param <NewOptionalType> the type of the new optional object instance
     *
     * @return a new instance of {@link Optional} object
     *
     * @see #retype()
     */
    @NotNull
    @ExtensibleFluentChain
    default <AnyValueType, NewOptionalType extends Optional<AnyValueType>> NewOptionalType createFrom(AnyValueType anyValue) {
        return optional(anyValue);
    }

    /**
     * Allows a type change of the current {@link Optional} object.
     * This method is used by methods marked with {@link ExtensibleFluentChain}
     * when creating a new instance is not necessary, but the current
     * object's type must be changed in order to comply with the generic type.
     * This method does not need to be reimplemented for a subtype to comply
     * with the {@link ExtensibleFluentChain} contract.
     *
     * @param <OptionalType> the type of the optional object instance
     *
     * @return the same instance of the {@link Optional} object with type specified by the generic argument
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <OptionalType extends Optional<ValueType>> OptionalType retype() {
        return (OptionalType) this;
    }

    /**
     * Calls the given {@link Consumer} with the wrapped value if it is present.
     * Fluent equivalent of {@link java.util.Optional#ifPresent(Consumer)}.
     *
     * @param optionalValueConsumer to consume the wrapped value
     * @param <OptionalType>        the type of the optional object instance
     *
     * @return the same instance of the {@link Optional} object with type specified by the generic argument
     */
    @NotNull
    @ExtensibleFluentChain
    default <OptionalType extends Optional<ValueType>> OptionalType onPresent(@NotNull Consumer<? super ValueType> optionalValueConsumer) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
        return retype();
    }

    /**
     * Calls the given {@link Runnable} when the wrapped value is absent.
     *
     * @param onEmptyAction  to be called when the wrapped value is absent
     * @param <OptionalType> the type of the optional object instance
     *
     * @return the same instance of the {@link Optional} object with type specified by the generic argument
     */
    @NotNull
    @ExtensibleFluentChain
    default <OptionalType extends Optional<ValueType>> OptionalType onEmpty(@NotNull Runnable onEmptyAction) {
        if (!isPresent()) {
            onEmptyAction.run();
        }
        return retype();
    }

    /**
     * Calls the given {@link Consumer} with the wrapped value if it is present.
     * Equivalent of {@link java.util.Optional#ifPresent(Consumer)}.
     *
     * @param optionalValueConsumer to consume the wrapped value
     */
    default void ifPresent(@NotNull Consumer<? super ValueType> optionalValueConsumer) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
    }

    /**
     * Calls the given {@link Runnable} when the wrapped value is absent.
     *
     * @param onEmptyAction to be called when the wrapped value is absent
     */
    default void ifEmpty(@NotNull Runnable onEmptyAction) {
        if (!isPresent()) {
            onEmptyAction.run();
        }
    }

    /**
     * A merge of {@link #ifPresent(Consumer)} and {@link #ifEmpty(Runnable)} methods.
     * The given {@link Consumer} is called when the wrapped value is present, and
     * the given {@link Runnable} when the wrapped value is absent.
     *
     * @param optionalValueConsumer to consume the wrapped value if it's present
     * @param onEmptyAction         to be called when the wrapped value is absent
     */
    default void ifPresentOrElse(@NotNull Consumer<? super ValueType> optionalValueConsumer, @NotNull Runnable onEmptyAction) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
        else {
            onEmptyAction.run();
        }
    }

    /**
     * Tests the wrapped optional value against given {@link Predicate}.
     * Equivalent of {@link java.util.Optional#filter(Predicate)}.
     *
     * @param predicate      to test the value against
     * @param <OptionalType> the type of the optional object instance
     *
     * @return the same instance of the {@link Optional} if the value is present
     * and matches the predicate, otherwise an {@link EmptyOptional}
     */
    @NotNull
    @ExtensibleFluentChain
    default <OptionalType extends Optional<ValueType>> OptionalType filter(@NotNull Predicate<ValueType> predicate) {
        if (isPresent() && predicate.test(get())) {
            return retype();
        }
        else {
            return empty();
        }
    }

    /**
     * Transforms the wrapped optional value using the provided function.
     * This method works just like the java.util version and therefore
     * is does not respect the first Monad Law.
     * This is not accidental. Users who are migrating from java.util
     * to this library would find their code breaking in many places
     * for seemingly no reason, which might be off-putting.
     * Equivalent of {@link java.util.Optional#map(Function)}.
     *
     * @param mappingFunction   to map the value with
     * @param <NewValueType>    the type of the mapping result
     * @param <NewOptionalType> the type of the optional object instance
     *
     * @return a new instance of the {@link Optional} with the mapped value
     * if the value is present, otherwise an {@link EmptyOptional}
     */
    @NotNull
    @ExtensibleFluentChain
    default <NewValueType, NewOptionalType extends Optional<NewValueType>> NewOptionalType map(@NotNull Function<? super ValueType, ? extends NewValueType> mappingFunction) {
        return isPresent() ? createFrom(mappingFunction.apply(get())) : empty();
    }

    /**
     * Transforms the wrapped optional value using the provided function.
     * Equivalent of {@link java.util.Optional#flatMap(Function)}.
     *
     * @param mappingFunction   to map the value with
     * @param <NewValueType>    the type of the mapping result
     * @param <NewOptionalType> the type of the optional object instance
     *
     * @return an instance of the {@link Optional} object provided by the
     * mapping function if the value is present, otherwise an {@link EmptyOptional}
     *
     * @throws NullPointerException if the result of the mapping function is null
     */
    @NotNull
    @ExtensibleFluentChain
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

    /**
     * Merges the values of this and another {@link Optional} together.
     *
     * @param other             other optional value
     * @param mergeFunction     to merge the values with
     * @param <OtherValueType>  the type of the other value
     * @param <NewValueType>    the type of the merge function result
     * @param <NewOptionalType> the type of the optional object instance
     *
     * @return an instance of the {@link Optional} created from the
     * merged value if both merge components are present,
     * otherwise an {@link EmptyOptional} instance
     */
    @NotNull
    @ExtensibleFluentChain
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

    /**
     * Merges the values of this and another {@link Optional} together.
     *
     * @param other             other optional value
     * @param mergeFallback     to use if the other optional is absent
     * @param mergeFunction     to merge the values with
     * @param <OtherValueType>  the type of the other value
     * @param <NewValueType>    the type of the merge function result
     * @param <NewOptionalType> the type of the optional object instance
     *
     * @return an instance of the {@link Optional} created from the
     * merged value if both merge components are present or
     * fallback value if the first component is present,
     * otherwise an {@link EmptyOptional} instance
     */
    @NotNull
    @ExtensibleFluentChain
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

    /**
     * Merges the values of this and another {@link Optional} together.
     *
     * @param other              other optional value
     * @param thisMergeFallback  to use if the other optional is absent
     * @param otherMergeFallback to use if the this optional is absent
     * @param mergeFunction      to merge the values with
     * @param <OtherValueType>   the type of the other value
     * @param <NewValueType>     the type of the merge function result
     * @param <NewOptionalType>  the type of the optional object instance
     *
     * @return an instance of the {@link Optional} created from the
     * merged value if both merge components are present,
     * first fallback value if the first component is present or
     * second fallback value if the second component is present,
     * otherwise an {@link EmptyOptional} instance
     */
    @NotNull
    @ExtensibleFluentChain
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

    /**
     * Switches the wrapped optional value if it is absent with
     * the provided value.
     *
     * @param alternativeValue to switch the optional value with
     * @param <OptionalType>   the type of the optional object instance
     *
     * @return the same {@link Optional} instance if the value is present or
     * a new instance containing the alternative value otherwise
     *
     * @see #orGet(Supplier) a lazy-evaluating alternative
     */
    @ExtensibleFluentChain
    default <OptionalType extends Optional<ValueType>> OptionalType or(ValueType alternativeValue) {
        return isPresent() ? retype() : createFrom(alternativeValue);
    }

    /**
     * Switches the wrapped optional value if it is absent with
     * the provided value.
     *
     * @param alternativeValueSupplier the {@link Supplier} providing a value to switch the optional value with
     * @param <OptionalType>           the type of the optional object instance
     *
     * @return the same {@link Optional} instance if the value is present or
     * a new instance containing the supplied alternative value otherwise
     */
    @ExtensibleFluentChain
    default <OptionalType extends Optional<ValueType>> OptionalType orGet(@NotNull Supplier<? extends ValueType> alternativeValueSupplier) {
        return isPresent() ? retype() : createFrom(alternativeValueSupplier.get());
    }

    /**
     * Throws the provided exception if the wrapped optional value is absent.
     *
     * @param exceptionSupplier the {@link Supplier} that provides the exception to be thrown
     * @param <ExceptionType>   the type of the exception to be thrown
     * @param <OptionalType>    the type of the optional object instance
     *
     * @return the same {@link Optional} instance
     *
     * @throws ExceptionType if the optional value is absent
     */
    @ExtensibleFluentChain
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

    /**
     * Equivalent of {@link java.util.Optional#orElse(Object)} called with null as an argument.
     *
     * @return the wrapped optional value or null if the value is absent
     */
    default ValueType orElseNull() {
        return orElse(null);
    }

    /**
     * Equivalent of {@link java.util.Optional#orElse(Object)}.
     *
     * @param alternativeValue to be returned if the optional value is absent
     *
     * @return the wrapped optional value or the alternative value if the wrapped value is absent
     */
    @Contract(value = "null -> null", pure = true)
    default ValueType orElse(ValueType alternativeValue) {
        return isPresent() ? get() : alternativeValue;
    }

    /**
     * Equivalent of {@link java.util.Optional#orElseGet(Supplier)}.
     *
     * @param alternativeValueSupplier the {@link Supplier} that provides the alternative value to be returned if the wrapped value is absent
     *
     * @return the wrapped optional value or the alternative value if the wrapped value is absent
     */
    default ValueType orElseGet(@NotNull Supplier<? extends ValueType> alternativeValueSupplier) {
        return isPresent() ? get() : alternativeValueSupplier.get();
    }

    /**
     * Equivalent of {@link java.util.Optional#orElseThrow(Supplier)}.
     *
     * @param exceptionSupplier the {@link Supplier} that provides the exception to be thrown
     * @param <ExceptionType>   the type of the exception to be thrown
     *
     * @return the wrapped optional value
     *
     * @throws ExceptionType if the wrapped optional value is absent
     */
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

    /**
     * @return a {@link java.util.Optional} equivalent of this {@link Optional} instance
     */
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
