package io.kofun.prototypes;

import io.kofun.Iterators;
import io.kofun.Optional;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.*;

/**
 * Prototype of extensible fluent Optional monad that provides implementations to all transformation methods.
 * Implementors of this class should respect the following contract:<br/>
 * * {@link #get()} method should return a value only if it exists<br/>
 * * {@link #get()} method should throw if value does not exist<br/>
 * * {@link #isPresent()} method should return true if value exists and false if value is absent<br/>
 * * {@link #recreateFull(Object)} method should create a full optional of the implementors' type<br/>
 * * {@link #recreateEmpty()} method should create an empty optional of the implementors' type<br/>
 * Every method annotated with {@link ExtensibleFluentChain} must be implemented
 * as described by the annotation's documentation to retain generic type information
 * needed for chaining.
 * For example implementation see {@link Optional} and its' two variants: {@link io.kofun.FullOptional} and {@link io.kofun.EmptyOptional}.
 *
 * @param <ValueType>       the type of the wrapped optional value
 * @param <NewOptionalType> the type representing the result of chaining transformation methods
 */
public interface OptionalPrototype<ValueType, NewOptionalType extends OptionalPrototype> extends FluentPrototype<NewOptionalType>, Iterable<ValueType> {

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
     * Allows creation of a new {@link OptionalPrototype} from the given value.
     * This method may be used by all methods marked with {@link ExtensibleFluentChain}
     * which require creating a new instance.
     *
     * @param anyValue       the optional value
     * @param <AnyValueType> the type of the optional value
     *
     * @return a new instance of {@link OptionalPrototype} object
     *
     * @see #recreateEmpty()
     * @see #retype()
     */
    @NotNull
    @ExtensibleFluentChain
    <AnyValueType> NewOptionalType recreateFull(AnyValueType anyValue);

    /**
     * Allows creation of a new empty {@link OptionalPrototype}.
     * This method may be used by all methods marked with {@link ExtensibleFluentChain}
     * which require creating a new empty instance.
     *
     * @param <AnyValueType> the type of the optional value
     *
     * @return a new instance of empty {@link OptionalPrototype} object
     *
     * @see #recreateFull(Object)
     * @see #retype()
     */
    @NotNull
    @ExtensibleFluentChain
    <AnyValueType> NewOptionalType recreateEmpty();

    /**
     * Calls the given {@link Consumer} with the wrapped value if it is present.
     * Fluent equivalent of {@link java.util.Optional#ifPresent(Consumer)}.
     *
     * @param optionalValueConsumer to consume the wrapped value
     *
     * @return the same instance of the {@link OptionalPrototype} object with type specified by the generic argument
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    @ExtensibleFluentChain
    default NewOptionalType onPresent(@NotNull Consumer<? super ValueType> optionalValueConsumer) {
        if (isPresent()) {
            optionalValueConsumer.accept(get());
        }
        return retype();
    }

    /**
     * Calls the given {@link Runnable} when the wrapped value is absent.
     *
     * @param onEmptyAction to be called when the wrapped value is absent
     *
     * @return the same instance of the {@link OptionalPrototype} object with type specified by the generic argument
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    @ExtensibleFluentChain
    default NewOptionalType onEmpty(@NotNull Runnable onEmptyAction) {
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
     * @param predicate to test the value against
     *
     * @return the same instance of the {@link OptionalPrototype} if the value is present
     * and matches the predicate, otherwise an empty optional
     */
    @NotNull
    @ExtensibleFluentChain
    default NewOptionalType filter(@NotNull Predicate<ValueType> predicate) {
        if (isPresent() && predicate.test(get())) {
            return retype();
        }
        else {
            return recreateEmpty();
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
     * @param mappingFunction to map the value with
     * @param <NewValueType>  the type of the mapping result
     *
     * @return a new instance of the {@link OptionalPrototype} with the mapped value
     * if the value is present, otherwise an empty optional
     */
    @NotNull
    @ExtensibleFluentChain
    default <NewValueType> NewOptionalType map(@NotNull Function<? super ValueType, ? extends NewValueType> mappingFunction) {
        return isPresent() ? recreateFull(mappingFunction.apply(get())) : recreateEmpty();
    }

    /**
     * Transforms the wrapped optional value using the provided function.
     * Equivalent of {@link java.util.Optional#flatMap(Function)}.
     *
     * @param mappingFunction to map the value with
     * @param <NewValueType>  the type of the mapping result
     *
     * @return an instance of the {@link OptionalPrototype} object provided by the
     * mapping function if the value is present, otherwise an empty optional
     *
     * @throws NullPointerException if the result of the mapping function is null
     */
    @NotNull
    @ExtensibleFluentChain
    default <NewValueType> NewOptionalType flatMap(@NotNull Function<? super ValueType, ? extends OptionalPrototype<NewValueType, ?>> mappingFunction) {
        if (isPresent()) {
            final OptionalPrototype newOptional = mappingFunction.apply(get());
            Objects.requireNonNull(newOptional, "Optional flat mapping resulted in a null object");
            return newOptional.isPresent() ? recreateFull(newOptional.get()) : recreateEmpty();
        }
        else {
            return recreateEmpty();
        }
    }

    /**
     * Merges the values of this and another {@link OptionalPrototype} together.
     *
     * @param other            other optional value
     * @param mergeFunction    to merge the values with
     * @param <OtherValueType> the type of the other value
     * @param <NewValueType>   the type of the merge function result
     *
     * @return an instance of the {@link OptionalPrototype} created from the
     * merged value if both merge components are present,
     * otherwise an empty optional
     */
    @NotNull
    @ExtensibleFluentChain
    default <OtherValueType, NewValueType>
    NewOptionalType mergeWith(@NotNull OptionalPrototype<OtherValueType, ?> other,
                              @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent() && other.isPresent()) {
            return recreateFull(mergeFunction.apply(get(), other.get()));
        }
        else {
            return recreateEmpty();
        }
    }

    /**
     * Merges the values of this and another {@link OptionalPrototype} together.
     *
     * @param other            other optional value
     * @param mergeFallback    to use if the other optional is absent
     * @param mergeFunction    to merge the values with
     * @param <OtherValueType> the type of the other value
     * @param <NewValueType>   the type of the merge function result
     *
     * @return an instance of the {@link OptionalPrototype} created from the
     * merged value if both merge components are present or
     * fallback value if the first component is present,
     * otherwise an empty optional
     */
    @NotNull
    @ExtensibleFluentChain
    default <OtherValueType, NewValueType>
    NewOptionalType mergeWith(@NotNull OptionalPrototype<OtherValueType, ?> other,
                              @NotNull Function<? super ValueType, ? extends NewValueType> mergeFallback,
                              @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent() && other.isPresent()) {
            return recreateFull(mergeFunction.apply(get(), other.get()));
        }
        else if (isPresent()) {
            return recreateFull(mergeFallback.apply(get()));
        }
        else {
            return recreateEmpty();
        }
    }

    /**
     * Merges the values of this and another {@link OptionalPrototype} together.
     *
     * @param other              other optional value
     * @param thisMergeFallback  to use if the other optional is absent
     * @param otherMergeFallback to use if the this optional is absent
     * @param mergeFunction      to merge the values with
     * @param <OtherValueType>   the type of the other value
     * @param <NewValueType>     the type of the merge function result
     *
     * @return an instance of the {@link OptionalPrototype} created from the
     * merged value if both merge components are present,
     * first fallback value if the first component is present or
     * second fallback value if the second component is present,
     * otherwise an empty optional
     */
    @NotNull
    @ExtensibleFluentChain
    default <OtherValueType, NewValueType>
    NewOptionalType mergeWith(@NotNull OptionalPrototype<OtherValueType, ?> other,
                              @NotNull Function<? super ValueType, ? extends NewValueType> thisMergeFallback,
                              @NotNull Function<? super OtherValueType, ? extends NewValueType> otherMergeFallback,
                              @NotNull BiFunction<? super ValueType, ? super OtherValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent() && other.isPresent()) {
            return recreateFull(mergeFunction.apply(get(), other.get()));
        }
        else if (isPresent()) {
            return recreateFull(thisMergeFallback.apply(get()));
        }
        else if (other.isPresent()) {
            return recreateFull(otherMergeFallback.apply(other.get()));
        }
        else {
            return recreateEmpty();
        }
    }

    /**
     * Extract two components of the wrapped optional value and if both are present,
     * merges them using the provided function.
     *
     * @param firstComponentExtractor  to be used for extracting the first component
     * @param secondComponentExtractor to be used for extracting the second component
     * @param mergeFunction            to merge the values with
     * @param <FirstValueType>         the type of the first component
     * @param <SecondValueType>        the type of the second component
     * @param <NewValueType>           the type of the merge function result
     *
     * @return if the wrapped {@link OptionalPrototype} value is present and
     * neither component is null, a new optional instance of the merge
     * function result, otherwise an empty optional
     */
    @NotNull
    @ExtensibleFluentChain
    default <FirstValueType, SecondValueType, NewValueType>
    NewOptionalType explodeAndMerge(@NotNull Function<? super ValueType, ? extends FirstValueType> firstComponentExtractor,
                                    @NotNull Function<? super ValueType, ? extends SecondValueType> secondComponentExtractor,
                                    @NotNull BiFunction<? super FirstValueType, ? super SecondValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent()) {
            final FirstValueType firstValue = firstComponentExtractor.apply(get());
            final SecondValueType secondValue = secondComponentExtractor.apply(get());
            if (firstValue != null && secondValue != null) {
                return recreateFull(mergeFunction.apply(firstValue, secondValue));
            }
            else {
                return recreateEmpty();
            }
        }
        else {
            return recreateEmpty();
        }
    }

    /**
     * Extract two components of the wrapped optional value and if both are present,
     * merges them using the provided function.
     *
     * @param firstComponentExtractor  to be used for extracting the first component
     * @param secondComponentExtractor to be used for extracting the second component
     * @param mergeFallback            to use if the second component is null
     * @param mergeFunction            to merge the values with
     * @param <FirstValueType>         the type of the first component
     * @param <SecondValueType>        the type of the second component
     * @param <NewValueType>           the type of the merge function result
     *
     * @return if the wrapped {@link OptionalPrototype} value is present and
     * neither component is null, a new optional instance of the merge
     * function result or if the second component is absent,
     * a new optional instance of the fallback function,
     * otherwise an empty optional
     */
    @NotNull
    @ExtensibleFluentChain
    default <FirstValueType, SecondValueType, NewValueType>
    NewOptionalType explodeAndMerge(@NotNull Function<? super ValueType, ? extends FirstValueType> firstComponentExtractor,
                                    @NotNull Function<? super ValueType, ? extends SecondValueType> secondComponentExtractor,
                                    @NotNull Function<? super FirstValueType, ? extends NewValueType> mergeFallback,
                                    @NotNull BiFunction<? super FirstValueType, ? super SecondValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent()) {
            final FirstValueType firstValue = firstComponentExtractor.apply(get());
            final SecondValueType secondValue = secondComponentExtractor.apply(get());
            if (firstValue != null && secondValue != null) {
                return recreateFull(mergeFunction.apply(firstValue, secondValue));
            }
            else if (firstValue != null) {
                return recreateFull(mergeFallback.apply(firstValue));
            }
            else {
                return recreateEmpty();
            }
        }
        else {
            return recreateEmpty();
        }
    }

    /**
     * Extract two components of the wrapped optional value and if both are present,
     * merges them using the provided function.
     *
     * @param firstComponentExtractor      to be used for extracting the first component
     * @param secondComponentExtractor     to be used for extracting the second component
     * @param firstComponentMergeFallback  to use if the second component is null
     * @param secondComponentMergeFallback to use if the first component is null
     * @param mergeFunction                to merge the values with
     * @param <FirstValueType>             the type of the first component
     * @param <SecondValueType>            the type of the second component
     * @param <NewValueType>               the type of the merge function result
     *
     * @return if the wrapped {@link OptionalPrototype} value is present and
     * neither component is null, a new optional instance of the merge
     * function result or if the second component is absent,
     * a new optional instance of the first fallback function or
     * if the first component is absent, a new optional instance of
     * the second fallback function, otherwise an empty optional
     */
    @NotNull
    @ExtensibleFluentChain
    default <FirstValueType, SecondValueType, NewValueType>
    NewOptionalType explodeAndMerge(@NotNull Function<? super ValueType, ? extends FirstValueType> firstComponentExtractor,
                                    @NotNull Function<? super ValueType, ? extends SecondValueType> secondComponentExtractor,
                                    @NotNull Function<? super FirstValueType, ? extends NewValueType> firstComponentMergeFallback,
                                    @NotNull Function<? super SecondValueType, ? extends NewValueType> secondComponentMergeFallback,
                                    @NotNull BiFunction<? super FirstValueType, ? super SecondValueType, ? extends NewValueType> mergeFunction) {
        if (isPresent()) {
            final FirstValueType firstValue = firstComponentExtractor.apply(get());
            final SecondValueType secondValue = secondComponentExtractor.apply(get());
            if (firstValue != null && secondValue != null) {
                return recreateFull(mergeFunction.apply(firstValue, secondValue));
            }
            else if (firstValue != null) {
                return recreateFull(firstComponentMergeFallback.apply(firstValue));
            }
            else if (secondValue != null) {
                return recreateFull(secondComponentMergeFallback.apply(secondValue));
            }
            else {
                return recreateEmpty();
            }
        }
        else {
            return recreateEmpty();
        }
    }

    /**
     * Switches the wrapped optional value if it is absent with
     * the provided value.
     *
     * @param alternativeValue to switch the optional value with
     *
     * @return the same {@link OptionalPrototype} instance if the value is present or
     * a new instance containing the alternative value otherwise
     *
     * @see #orGet(Supplier) a lazy-evaluating alternative
     */
    @NotNull
    @ExtensibleFluentChain
    default NewOptionalType or(ValueType alternativeValue) {
        return isPresent() ? retype() : recreateFull(alternativeValue);
    }

    /**
     * Switches the wrapped optional value if it is absent with
     * the provided value.
     *
     * @param alternativeValueSupplier the {@link Supplier} providing a value to switch the optional value with
     *
     * @return the same {@link OptionalPrototype} instance if the value is present or
     * a new instance containing the supplied alternative value otherwise
     */
    @NotNull
    @ExtensibleFluentChain
    default NewOptionalType orGet(@NotNull Supplier<? extends ValueType> alternativeValueSupplier) {
        return isPresent() ? retype() : recreateFull(alternativeValueSupplier.get());
    }

    /**
     * Throws the provided exception if the wrapped optional value is absent.
     *
     * @param exceptionSupplier the {@link Supplier} that provides the exception to be thrown
     * @param <ExceptionType>   the type of the exception to be thrown
     *
     * @return the same {@link OptionalPrototype} instance
     *
     * @throws ExceptionType if the optional value is absent
     */
    @NotNull
    @ExtensibleFluentChain
    default <ExceptionType extends Throwable> NewOptionalType orThrow(@NotNull Supplier<? extends ExceptionType> exceptionSupplier) throws ExceptionType {
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
    @NotNull
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
     * @return a {@link java.util.Optional} equivalent of this {@link OptionalPrototype} instance
     */
    default java.util.Optional<ValueType> toJavaOptional() {
        return isPresent() ? java.util.Optional.of(get()) : java.util.Optional.empty();
    }

    @NotNull
    @Override
    default Iterator<ValueType> iterator() {
        return isPresent() ? Iterators.singleton(get()) : Iterators.emptyIterator();
    }

}
