package io.kofun.prototypes;

import io.kofun.CheckedConsumer;
import io.kofun.CheckedFunction;
import io.kofun.CheckedPredicate;
import io.kofun.CheckedRunnable;
import io.kofun.CheckedSupplier;
import io.kofun.Iterators;
import io.kofun.exception.ErrorNotPresentException;
import io.kofun.exception.PredicateNotMatchingException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Contract:<br/>
 * * methods with {@code try} in their name can throw only {@link Error} if their parameters are not null<br/>
 * * methods may not have any side effects
 *
 * @param <SuccessType> the type of the result if this Try is a success
 * @param <NewTryType>  the type of the new try for fluent chaining
 */
public interface TryPrototype<SuccessType, NewTryType extends TryPrototype> extends FluentPrototype<NewTryType>,
                                                                                    Iterable<SuccessType> {

    /**
     * Returns the wrapped success result.
     * Should not be called without checking for the presence of the value first
     * using {@link #isSuccess()}.
     * If this Try is not success, this method will always throw.
     *
     * @return the success result if it is present
     */
    SuccessType getSuccess();

    /**
     * Returns the wrapped error result.
     * Should not be called without checking for the presence of the value first
     * using {@link #isError()}.
     * If this Try is not error, this method will always throw.
     *
     * @return the error result if it is present
     */
    @NotNull
    Throwable getError();

    /**
     * @return true if this Try contains a success result, otherwise false
     */
    boolean isSuccess();

    /**
     * @return true if this Try contains an error result, otherwise false
     */
    default boolean isError() {
        return !isSuccess();
    }

    /**
     * Allows creation of a new {@link TryPrototype} from the given success result.
     * This method may be used by all methods marked with {@link ExtensibleFluentChain}
     * which require creating a new instance.
     *
     * @param success          the success result value
     * @param <AnySuccessType> the type of the success result value
     *
     * @return a new instance of {@link OptionalPrototype} object
     *
     * @see #recreateError(Throwable)
     * @see #recreateOther(TryPrototype)
     * @see #retype()
     */
    @NotNull
    @Contract("_ -> !null")
    @ExtensibleFluentChain
    <AnySuccessType> NewTryType recreateSuccess(AnySuccessType success);

    /**
     * Allows creation of a new {@link TryPrototype} from the given error result.
     * This method may be used by all methods marked with {@link ExtensibleFluentChain}
     * which require creating a new instance.
     *
     * @param error            the error result value
     * @param <AnySuccessType> the type of the success result (for generics to be properly resolved)
     *
     * @return a new instance of {@link OptionalPrototype} object
     *
     * @see #recreateSuccess(Object)
     * @see #recreateOther(TryPrototype)
     * @see #retype()
     */
    @NotNull
    @Contract("_ -> !null")
    @ExtensibleFluentChain
    <AnySuccessType> NewTryType recreateError(Throwable error);

    /**
     * Allows creation of a new {@link TryPrototype} copied from the given object.
     * This method may be used by all methods marked with {@link ExtensibleFluentChain}
     * which require creating a new instance.
     *
     * @param tryPrototype     the object to be copied
     * @param <AnySuccessType> the type of the success result
     *
     * @return a new instance of {@link OptionalPrototype} object
     *
     * @see #recreateSuccess(Object)
     * @see #recreateError(Throwable)
     * @see #retype()
     */
    @NotNull
    @ExtensibleFluentChain
    <TryType extends TryPrototype<AnySuccessType, ?>, AnySuccessType> NewTryType recreateOther(TryType tryPrototype);

    /**
     * If this Try is success, it is passed to the given {@link Consumer}.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param successConsumer to use the success result
     *
     * @return the same Try instance
     *
     * @throws RuntimeException and any subclass of it thrown by the consumer
     * @see #onSuccessTryConsumer(Consumer) exception-safe version of this method
     * @see #onSuccessTry(CheckedConsumer) exception-safe version of this method that allows for checked exceptions
     */
    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onSuccess(@NotNull Consumer<? super SuccessType> successConsumer) {
        if (isSuccess()) {
            successConsumer.accept(getSuccess());
        }
        return retype();
    }

    /**
     * If this Try is success, it is passed to the given {@link Consumer}.
     * This is a exception-safe version of {@link #onSuccess(Consumer)},
     * if the consumer throws, a new Try with thrown error will be returned.
     *
     * @param successConsumer to use the success result
     *
     * @return the same Try instance or, if consumer threw, a new error Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onSuccessTryConsumer(@NotNull Consumer<SuccessType> successConsumer) {
        return onSuccessTry(successConsumer::accept);
    }

    /**
     * If this Try is success, the provided {@link Runnable} will be executed.
     * This is a exception-safe method, if the runnable throws,
     * a new Try with thrown error will be returned.
     *
     * @param runnable to be executed
     *
     * @return the same Try instance or, if runnable threw, a new error Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onSuccessTryRunnable(@NotNull Runnable runnable) {
        return onSuccessTryRun(runnable::run);
    }

    /**
     * If this Try is success, the provided {@link CheckedRunnable} will be executed.
     * This is a exception-safe method, if the runnable throws,
     * a new Try with thrown error will be returned.
     *
     * @param runnable to be executed
     *
     * @return the same Try instance or, if runnable threw, a new error Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onSuccessTryRun(@NotNull CheckedRunnable runnable) {
        return onSuccessTry(success -> runnable.run());
    }

    /**
     * If this Try is success, it is passed to the given {@link CheckedConsumer}.
     * This is a exception-safe version of {@link #onSuccess(Consumer)},
     * if the consumer throws, a new Try with thrown error will be returned.
     *
     * @param successConsumer to use the success result
     *
     * @return the same Try instance or, if consumer threw, a new error Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onSuccessTry(@NotNull CheckedConsumer<SuccessType, ? extends Throwable> successConsumer) {
        if (isSuccess()) {
            try {
                successConsumer.accept(getSuccess());
            } catch (Throwable error) {
                return recreateError(error);
            }
        }
        return retype();
    }

    /**
     * If this Try is error, it is passed to the given {@link Consumer}.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param errorConsumer to use the error result
     *
     * @return the same Try instance
     *
     * @throws RuntimeException and any subclass of it thrown by the consumer
     * @see #onErrorTryConsumer(Consumer) exception-safe version of this method
     * @see #onErrorTry(CheckedConsumer) exception-safe version of this method that allows for checked exceptions
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onError(@NotNull Consumer<? super Throwable> errorConsumer) {
        if (isError()) {
            errorConsumer.accept(getError());
        }
        return retype();
    }

    /**
     * If this Try is error, it is passed to the given {@link Consumer}.
     * This is a exception-safe version of {@link #onError(Consumer)},
     * if the consumer throws, a new Try with thrown error will be returned.
     *
     * @param errorConsumer to use the error result
     *
     * @return the same Try instance or, if consumer threw, a new error Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onErrorTryConsumer(@NotNull Consumer<? super Throwable> errorConsumer) {
        return onErrorTry(errorConsumer::accept);
    }

    /**
     * If this Try is error, the provided {@link Runnable} will be executed.
     * This is a exception-safe method, if the runnable throws,
     * a new Try with thrown error will be returned.
     *
     * @param runnable to be executed
     *
     * @return the same Try instance or, if runnable threw, a new error Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onErrorTryRunnable(@NotNull Runnable runnable) {
        return onErrorTryRun(runnable::run);
    }

    /**
     * If this Try is error, the provided {@link CheckedRunnable} will be executed.
     * This is a exception-safe method, if the runnable throws,
     * a new Try with thrown error will be returned.
     *
     * @param runnable to be executed
     *
     * @return the same Try instance or, if runnable threw, a new error Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onErrorTryRun(@NotNull CheckedRunnable runnable) {
        return onErrorTry(error -> runnable.run());
    }

    /**
     * If this Try is error, it is passed to the given {@link CheckedConsumer}.
     * This is a exception-safe version of {@link #onError(Consumer)},
     * if the consumer throws, a new Try with thrown error will be returned.
     *
     * @param errorConsumer to use the error result
     *
     * @return the same Try instance or, if consumer threw, a new error Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType onErrorTry(@NotNull CheckedConsumer<? super Throwable, ? extends Throwable> errorConsumer) {
        if (isError()) {
            try {
                errorConsumer.accept(getError());
            } catch (Throwable error) {
                return recreateError(error);
            }
        }
        return retype();
    }

    /**
     * If this Try is an error with a matching class, it is passed to the given {@link Consumer}.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param errorClass    to be compared with this Try's error
     * @param errorConsumer to use the error result
     *
     * @return the same Try instance
     *
     * @throws RuntimeException and any subclass of it thrown by the consumer
     * @see #onErrorTryConsumer(Class, Consumer)
     * @see #onErrorTry(Class, CheckedConsumer)
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onError(@NotNull Class<ErrorType> errorClass, @NotNull Consumer<? super ErrorType> errorConsumer) {
        return onError(error -> {
            if (isErrorTypeOf(errorClass)) {
                errorConsumer.accept(errorClass.cast(error));
            }
        });
    }

    /**
     * If this Try is an error with a matching class, it is passed to the given {@link Consumer}.
     * This is a exception-safe version of {@link #onError(Class, Consumer)},
     * if the consumer throws, a new Try with thrown error will be returned.
     *
     * @param errorClass    to be compared with this Try's error
     * @param errorConsumer to use the error result
     *
     * @return the same Try instance or, if consumer threw, a new error Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onErrorTryConsumer(@NotNull Class<ErrorType> errorClass, @NotNull Consumer<? super ErrorType> errorConsumer) {
        return onErrorTry(errorClass, errorConsumer::accept);
    }

    /**
     * If this Try is an error with a matching class, the provided {@link Runnable} will be executed.
     * This is a exception-safe method, if the runnable throws,
     * a new Try with thrown error will be returned.
     *
     * @param errorClass to be compared with this Try's error
     * @param runnable   to be executed
     *
     * @return the same Try instance or, if runnable threw, a new error Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onErrorTryRunnable(@NotNull Class<ErrorType> errorClass, @NotNull Runnable runnable) {
        return onErrorTryRun(errorClass, runnable::run);
    }

    /**
     * If this Try is an error with a matching class, the provided {@link CheckedRunnable} will be executed.
     * This is a exception-safe method, if the runnable throws,
     * a new Try with thrown error will be returned.
     *
     * @param errorClass to be compared with this Try's error
     * @param runnable   to be executed
     *
     * @return the same Try instance or, if runnable threw, a new error Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onErrorTryRun(@NotNull Class<ErrorType> errorClass, @NotNull CheckedRunnable runnable) {
        return onErrorTry(errorClass, error -> runnable.run());
    }

    /**
     * If this Try is an error with a matching class, it is passed to the given {@link CheckedConsumer}.
     * This is a exception-safe version of {@link #onError(Class, Consumer)},
     * if the consumer throws, a new Try with thrown error will be returned.
     *
     * @param errorClass    to be compared with this Try's error
     * @param errorConsumer to use the error result
     *
     * @return the same Try instance or, if consumer threw, a new error Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onErrorTry(@NotNull Class<ErrorType> errorClass,
                                                                @NotNull CheckedConsumer<? super ErrorType, ? extends Throwable> errorConsumer) {
        return onErrorTry(error -> {
            if (isErrorTypeOf(errorClass)) {
                errorConsumer.accept(errorClass.cast(error));
            }
        });
    }

    /**
     * This method is an equivalent of {@link Stream#collect(Collector)} method.
     *
     * @param collector         the {@link Collector} describing the collection method
     * @param <AccumulatorType> the intermediate accumulator type
     * @param <CollectionType>  the type of the resulting collection
     *
     * @return the resulting collection
     */
    @Contract(value = "null -> fail", pure = true)
    default <AccumulatorType, CollectionType> CollectionType collect(@NotNull Collector<? super SuccessType, AccumulatorType, CollectionType> collector) {
        return stream().collect(collector);
    }

    /**
     * This method is an equivalent of {@link Stream#collect(Supplier, BiConsumer, BiConsumer)} method.
     *
     * @param collectionSupplier a function that creates a mutable container for values
     * @param valueAdder         a function that adds a given element to the container
     * @param collectionsMerger  a function that merges two containers together, the values of second consumed container should be put into the first container
     * @param <CollectionType>   the type of the resulting collection
     *
     * @return the resulting collection
     */
    @Contract(value = "null, null, null -> fail; _, null, null -> fail; _, _, null -> fail; null, null, _ -> fail; null, _, _ -> fail; _, null, _ -> fail", pure = true)
    default <CollectionType> CollectionType collect(@NotNull Supplier<CollectionType> collectionSupplier,
                                                    @NotNull BiConsumer<CollectionType, ? super SuccessType> valueAdder,
                                                    @NotNull BiConsumer<CollectionType, CollectionType> collectionsMerger) {
        return stream().collect(collectionSupplier, valueAdder, collectionsMerger);
    }

    /**
     * If this is a success Try, the success result will be the content of the resulting {@link Stream}.
     * Otherwise, the resulting Stream will be empty.
     *
     * @return a new Stream with the success result
     */
    @NotNull
    default Stream<SuccessType> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * If this is a success Try, a new {@link Iterator} with the success result will be returned.
     * Otherwise, the resulting iterator will be empty.
     *
     * @return a new Iterator with the success result
     */
    @NotNull
    @Override
    default Iterator<SuccessType> iterator() {
        return isSuccess() ? Iterators.singleton(getSuccess()) : Iterators.emptyIterator();
    }

    /**
     * Switches the flow to use the error result instead of the success result.
     * If this Try is an error, a new success Try is returned and the error becomes the success result.
     * Otherwise, a new error Try is returned with {@link ErrorNotPresentException} as the error result.
     *
     * @param <ErrorType> to be used as the new success type
     *
     * @return a new Try with previous error assigned as a success result
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "-> new", pure = true)
    default <ErrorType extends Throwable> NewTryType switchWithError() {
        if (isError()) {
            return recreateSuccess(getError());
        }
        else {
            return recreateError(new ErrorNotPresentException());
        }
    }

    /**
     * Filters the success result and if it does not match, the Try will become an error and hold a {@link PredicateNotMatchingException}.
     * If this Try is an error, the same Try will be returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param predicate to match the success against
     *
     * @return the same Try if success matches, otherwise a new error Try
     *
     * @throws RuntimeException and any subclass of it thrown by the predicate
     * @see #filterTry exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default NewTryType filter(@NotNull Predicate<? super SuccessType> predicate) {
        return filterMap(predicate, PredicateNotMatchingException::new);
    }

    /**
     * Filters the success result and if it does not match, the Try will become an error and hold a supplied error.
     * If this Try is an error, the same Try will be returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param predicate     to match the success against
     * @param errorSupplier to supply an error used if the success does not match
     *
     * @return the same Try if success matches, otherwise a new error Try
     *
     * @throws RuntimeException and any subclass of it thrown by the predicate or the supplier
     * @see #filterTryGet exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType filterGet(@NotNull Predicate<? super SuccessType> predicate, @NotNull Supplier<? extends ErrorType> errorSupplier) {
        return filterMap(predicate, success -> errorSupplier.get());
    }

    /**
     * Filters the success result and if it does not match, the Try will become an error and hold a mapped error.
     * If this Try is an error, the same Try will be returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param predicate   to match the success against
     * @param errorMapper to create an error from the not matching success result
     *
     * @return the same Try if success matches, otherwise a new error Try
     *
     * @throws RuntimeException and any subclass of it thrown by the predicate or the mapper
     * @see #filterTryMap exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType filterMap(@NotNull Predicate<? super SuccessType> predicate,
                                                               @NotNull Function<? super SuccessType, ? extends ErrorType> errorMapper) {
        if (isError()) {
            return retype();
        }
        else if (isSuccess() && predicate.test(getSuccess())) {
            return retype();
        }
        else {
            return recreateError(errorMapper.apply(getSuccess()));
        }
    }

    /**
     * Filters the success result and if it does not match, the Try will become an error and hold a {@link PredicateNotMatchingException}.
     * If this Try is an error, the same Try will be returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param predicate to match the success against
     *
     * @return the same Try if success matches, otherwise a new error Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default NewTryType filterTry(@NotNull CheckedPredicate<? super SuccessType, ? extends Throwable> predicate) {
        return filterTryMap(predicate, PredicateNotMatchingException::new);
    }

    /**
     * Filters the success result and if it does not match, the Try will become an error and hold a supplied error.
     * If this Try is an error, the same Try will be returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param predicate     to match the success against
     * @param errorSupplier to supply an error used if the success does not match
     *
     * @return the same Try if success matches, otherwise a new error Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType filterTryGet(@NotNull CheckedPredicate<? super SuccessType, ? extends Throwable> predicate,
                                                                  @NotNull CheckedSupplier<? extends ErrorType, ? extends Throwable> errorSupplier) {
        return filterTryMap(predicate, success -> errorSupplier.get());
    }

    /**
     * Filters the success result and if it does not match, the Try will become an error and hold a mapped error.
     * If this Try is an error, the same Try will be returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param predicate   to match the success against
     * @param errorMapper to create an error from the not matching success result
     *
     * @return the same Try if success matches, otherwise a new error Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType filterTryMap(@NotNull CheckedPredicate<? super SuccessType, ? extends Throwable> predicate,
                                                                  @NotNull CheckedFunction<? super SuccessType, ? extends ErrorType, ? extends Throwable> errorMapper) {
        if (isError()) {
            return retype();
        }
        try {
            if (predicate.test(getSuccess())) {
                return retype();
            }
            else {
                return recreateError(errorMapper.apply(getSuccess()));
            }
        } catch (Throwable throwable) {
            return recreateError(throwable);
        }
    }

    /**
     * If this Try is a success, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param mappingFunction  to be used for transforming the success result
     * @param <NewSuccessType> the type of new success value
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the mapper
     * @see #mapTry(CheckedFunction)  exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default <NewSuccessType> NewTryType map(@NotNull Function<? super SuccessType, ? extends NewSuccessType> mappingFunction) {
        if (isSuccess()) {
            NewSuccessType newSuccess = mappingFunction.apply(getSuccess());
            return recreateSuccess(newSuccess);
        }
        else {
            return retype();
        }
    }

    /**
     * If this Try is an error, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param mappingFunction to be used for transforming the error result
     * @param <NewErrorType>  the type of new error value
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the mapper
     * @see #mapTryError(CheckedFunction)  exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default <NewErrorType extends Throwable> NewTryType mapError(@NotNull Function<? super Throwable, ? extends NewErrorType> mappingFunction) {
        if (isError()) {
            NewErrorType newError = mappingFunction.apply(getError());
            return recreateError(newError);
        }
        else {
            return retype();
        }
    }

    /**
     * If this Try is an error with a matching class, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param errorClass      to be compared with this Try's error
     * @param mappingFunction to be used for transforming the error result
     * @param <NewErrorType>  the type of new error value
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the mapper
     * @see #mapTryError(Class, CheckedFunction)  exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <NewErrorType extends Throwable> NewTryType mapError(Class<? extends Throwable> errorClass,
                                                                 @NotNull Function<? super Throwable, ? extends NewErrorType> mappingFunction) {
        return mapError(error -> {
            if (isErrorTypeOf(errorClass)) {
                return mappingFunction.apply(error);
            }
            else {
                return error;
            }
        });
    }

    /**
     * If this Try is a success, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param mappingFunction  to be used for transforming the success result
     * @param <NewSuccessType> the type of new success value
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default <NewSuccessType, ErrorType extends Throwable> NewTryType mapTry(@NotNull CheckedFunction<? super SuccessType, ? extends NewSuccessType, ErrorType> mappingFunction) {
        if (isSuccess()) {
            try {
                NewSuccessType newSuccess = mappingFunction.apply(getSuccess());
                return recreateSuccess(newSuccess);
            } catch (Throwable error) {
                return recreateError(error);
            }
        }
        else {
            return retype();
        }
    }

    /**
     * If this Try is an error, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param mappingFunction to be used for transforming the error result
     * @param <NewErrorType>  the type of new error value
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default <NewErrorType extends Throwable, ErrorType extends Throwable> NewTryType mapTryError(@NotNull CheckedFunction<? super Throwable, ? extends NewErrorType,
            ErrorType> mappingFunction) {
        if (isError()) {
            try {
                NewErrorType newError = mappingFunction.apply(getError());
                return recreateError(newError);
            } catch (Throwable error) {
                return recreateError(error);
            }
        }
        else {
            return retype();
        }
    }

    /**
     * If this Try is an error with a matching class, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param errorClass      to be compared with this Try's error
     * @param mappingFunction to be used for transforming the error result
     * @param <NewErrorType>  the type of new error value
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <NewErrorType extends Throwable, ErrorType extends Throwable> NewTryType mapTryError(Class<? extends Throwable> errorClass,
                                                                                                 @NotNull CheckedFunction<? super Throwable, ? extends NewErrorType, ErrorType> mappingFunction) {
        return mapTryError(error -> {
            if (isErrorTypeOf(errorClass)) {
                return mappingFunction.apply(error);
            }
            else {
                return error;
            }
        });
    }

    /**
     * If this Try is a success, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param mappingFunction  to be used for transforming the success result
     * @param <NewSuccessType> the type of new success value
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the mapper
     * @see #flatMapTry(CheckedFunction)  exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default <NewSuccessType> NewTryType flatMap(@NotNull Function<? super SuccessType, ? extends TryPrototype<NewSuccessType, ?>> mappingFunction) {
        if (isSuccess()) {
            TryPrototype<NewSuccessType, ?> newTry = mappingFunction.apply(getSuccess());
            Objects.requireNonNull(newTry, "Try flat mapping resulted in a null object");
            return recreateOther(newTry);
        }
        else {
            return retype();
        }
    }

    /**
     * If this Try is an error, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param mappingFunction to be used for transforming the error result
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the mapper
     * @see #flatMapTryError(CheckedFunction)  exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default NewTryType flatMapError(@NotNull Function<? super Throwable, ? extends TryPrototype<SuccessType, ?>> mappingFunction) {
        if (isError()) {
            TryPrototype<SuccessType, ?> newTry = mappingFunction.apply(getError());
            Objects.requireNonNull(newTry, "Try flat mapping resulted in a null object");
            return recreateOther(newTry);
        }
        else {
            return retype();
        }
    }

    /**
     * If this Try is an error with a matching class, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param errorClass      to be compared with this Try's error
     * @param mappingFunction to be used for transforming the error result
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the mapper
     * @see #mapTryError(Class, CheckedFunction)  exception-safe version of this method
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default NewTryType flatMapError(Class<? extends Throwable> errorClass, @NotNull Function<? super Throwable, ? extends TryPrototype<SuccessType, ?>> mappingFunction) {
        return flatMapError(error -> blaFlatMap(errorClass, mappingFunction::apply, error));
    }

    /**
     * If this Try is a success, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param mappingFunction  to be used for transforming the success result
     * @param <NewSuccessType> the type of new success value
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default <NewSuccessType, ErrorType extends Throwable> NewTryType flatMapTry(
            @NotNull CheckedFunction<? super SuccessType, ? extends TryPrototype<NewSuccessType, ?>, ErrorType> mappingFunction) {
        if (isSuccess()) {
            TryPrototype<NewSuccessType, ?> newTry;
            try {
                newTry = mappingFunction.apply(getSuccess());
            } catch (Throwable error) {
                return recreateError(error);
            }
            Objects.requireNonNull(newTry, "Try flat mapping resulted in a null object");
            return recreateOther(newTry);
        }
        else {
            return retype();
        }
    }

    /**
     * If this Try is an error, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param mappingFunction to be used for transforming the error result
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType flatMapTryError(
            @NotNull CheckedFunction<? super Throwable, ? extends TryPrototype<SuccessType, ?>, ErrorType> mappingFunction) {
        if (isError()) {
            TryPrototype<SuccessType, ?> newTry;
            try {
                newTry = mappingFunction.apply(getError());
            } catch (Throwable error) {
                return recreateError(error);
            }
            Objects.requireNonNull(newTry, "Try flat mapping resulted in a null object");
            return recreateOther(newTry);
        }
        else {
            return retype();
        }
    }

    /**
     * If this Try is an error with a matching class, the result is mapped using the function provided.
     * Otherwise, no action is performed and the same Try is returned.
     * This is a exception-safe method, if the predicate throws,
     * a new Try with thrown error will be returned.
     *
     * @param errorClass      to be compared with this Try's error
     * @param mappingFunction to be used for transforming the error result
     *
     * @return a new Try if mapping was performed, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType flatMapTryError(@NotNull Class<? extends Throwable> errorClass,
                                                                     @NotNull CheckedFunction<? super Throwable, ? extends TryPrototype<SuccessType, ?>, ErrorType> mappingFunction) {
        return flatMapTryError(error -> blaFlatMap(errorClass, mappingFunction, error));
    }

    /**
     * For internal use only!
     *
     * @deprecated
     */
    @Nullable
    // TODO: better name for the method & consider duplicating code
    default <ErrorType extends Throwable> TryPrototype<SuccessType, ?> blaFlatMap(Class<? extends Throwable> errorClass,
                                                                                  @NotNull CheckedFunction<? super Throwable, ? extends TryPrototype<SuccessType,
                                                                                          ?>, ErrorType> mappingFunction,
                                                                                  Throwable error) throws ErrorType {
        if (isErrorTypeOf(errorClass)) {
            TryPrototype<SuccessType, ?> newTry = mappingFunction.apply(error);
            return newTry == null ? null : recreateOther(newTry);
        }
        else {
            return recreateError(error);
        }
    }

    /**
     * Swaps this Try with another Try if this Try is an error.
     * Otherwise returns the same Try.
     *
     * @param other      to be swapped with
     * @param <OtherTry> the type of the other Try
     *
     * @return the other Try if this Try is error, otherwise this Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default <OtherTry extends TryPrototype<SuccessType, ?>> NewTryType or(@NotNull OtherTry other) {
        if (isError()) {
            return recreateOther(other);
        }
        else {
            return retype();
        }
    }

    /**
     * Swaps this Try with another Try provided by the given {@link Supplier} if this Try is an error.
     * Otherwise returns the same Try.
     *
     * @param otherSupplier to supply the Try to be swapped with
     * @param <OtherTry>    the type of the other Try
     *
     * @return the other Try if this Try is error, otherwise this Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default <OtherTry extends TryPrototype<SuccessType, ?>> NewTryType orElse(@NotNull Supplier<? extends OtherTry> otherSupplier) {
        if (isError()) {
            OtherTry other = otherSupplier.get();
            Objects.requireNonNull(other, "Try alternative supplier result is a null object");
            return recreateOther(other);
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with the provided value.
     * If this Try is an error, a new Try with the given value is created.
     * Otherwise the same Try is returned.
     *
     * @param other to recover with
     *
     * @return a new Try with the provided value if this is an error, otherwise the same Try
     */
    @NotNull
    @Contract(pure = true)
    @ExtensibleFluentChain
    default NewTryType recover(SuccessType other) {
        if (isError()) {
            return recreateSuccess(other);
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with the provided value.
     * If this Try is an error with a matching class, a new Try with the given value is created.
     * Otherwise the same Try is returned.
     *
     * @param errorClass to be compared with this Try's error
     * @param other      to recover with
     *
     * @return a new Try with the provided value if this is an error, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType recover(@NotNull Class<ErrorType> errorClass, SuccessType other) {
        if (isError() && isErrorTypeOf(errorClass)) {
            return recreateSuccess(other);
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with the value provided by a given {@link Supplier}.
     * If this Try is an error, a new Try with the given value is created.
     * Otherwise the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param otherSupplier to provide the value to recover with
     *
     * @return a new Try with the provided value if this is an error, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the supplier
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default NewTryType recoverGet(@NotNull Supplier<SuccessType> otherSupplier) {
        if (isError()) {
            return recreateSuccess(otherSupplier.get());
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with the value provided by a given {@link Supplier}.
     * If this Try is an error with a matching class, a new Try with the given value is created.
     * Otherwise the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param errorClass    to be compared with this Try's error
     * @param otherSupplier to provide the value to recover with
     *
     * @return a new Try with the provided value if this is an error, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the supplier
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType recoverGet(@NotNull Class<ErrorType> errorClass, @NotNull Supplier<SuccessType> otherSupplier) {
        if (isError() && isErrorTypeOf(errorClass)) {
            return recreateSuccess(otherSupplier.get());
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with the value provided by a given {@link Function}.
     * If this Try is an error, a new Try with the given value is created.
     * Otherwise the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param mappingFunction to provide the value to recover with
     *
     * @return a new Try with the provided value if this is an error, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the function
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default NewTryType recoverMap(@NotNull Function<? super Throwable, SuccessType> mappingFunction) {
        if (isError()) {
            return recreateSuccess(mappingFunction.apply(getError()));
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with the value provided by a given {@link Function}.
     * If this Try is an error with a matching class, a new Try with the given value is created.
     * Otherwise the same Try is returned.
     * This method is not exception-safe. Methods with "try" as a part of their names
     * are exception-safe.
     *
     * @param errorClass      to be compared with this Try's error
     * @param mappingFunction to provide the value to recover with
     *
     * @return a new Try with the provided value if this is an error, otherwise the same Try
     *
     * @throws RuntimeException and any subclass of it thrown by the function
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType recoverMap(@NotNull Class<ErrorType> errorClass, @NotNull Function<ErrorType, SuccessType> mappingFunction) {
        if (isError() && isErrorTypeOf(errorClass)) {
            return recreateSuccess(mappingFunction.apply(errorClass.cast(getError())));
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with the provided value.
     * If this Try is an error, a new Try with the same value as the provided Try is returned.
     * Otherwise the same Try is returned.
     *
     * @param other to recover with
     *
     * @return a provided Try if this is an error, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default NewTryType recoverFlat(@NotNull TryPrototype<SuccessType, ?> other) {
        if (isError()) {
            return recreateOther(other);
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with the provided value.
     * If this Try is an error with a matching class, a new Try with the same value as the provided Try is returned.
     * Otherwise the same Try is returned.
     *
     * @param errorClass to be compared with this Try's error
     * @param other      to recover with
     *
     * @return a provided Try if this is an error, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType recoverFlat(@NotNull Class<ErrorType> errorClass, @NotNull TryPrototype<SuccessType, ?> other) {
        if (isError() && isErrorTypeOf(errorClass)) {
            return recreateOther(other);
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with value provided by the given {@link Supplier}.
     * If this Try is an error, a new Try with the same value as the provided Try is returned.
     * Otherwise the same Try is returned.
     *
     * @param otherSupplier to recover with
     *
     * @return a provided Try if this is an error, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default NewTryType recoverFlatGet(@NotNull Supplier<? extends TryPrototype<SuccessType, ?>> otherSupplier) {
        if (isError()) {
            TryPrototype<SuccessType, ?> result = otherSupplier.get();
            Objects.requireNonNull(result, "Try alternative supplier result is a null object");
            return recreateOther(result);
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with value provided by the given {@link Supplier}.
     * If this Try is an error with a matching class, a new Try with the same value as the provided Try is returned.
     * Otherwise the same Try is returned.
     *
     * @param errorClass    to be compared with this Try's error
     * @param otherSupplier to recover with
     *
     * @return a provided Try if this is an error, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType recoverFlatGet(@NotNull Class<ErrorType> errorClass, @NotNull Supplier<? extends TryPrototype<SuccessType, ?>> otherSupplier) {
        if (isError() && isErrorTypeOf(errorClass)) {
            TryPrototype<SuccessType, ?> result = otherSupplier.get();
            Objects.requireNonNull(result, "Try alternative supplier result is a null object");
            return recreateOther(result);
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with value provided by the given {@link Function}.
     * If this Try is an error with a matching class, a new Try with the same value as the provided Try is returned.
     * Otherwise the same Try is returned.
     *
     * @param errorMappingFunction to recover with
     *
     * @return a provided Try if this is an error, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null -> fail", pure = true)
    default NewTryType recoverFlatMap(@NotNull Function<? super Throwable, ? extends TryPrototype<SuccessType, ?>> errorMappingFunction) {
        if (isError()) {
            TryPrototype<SuccessType, ?> result = errorMappingFunction.apply(getError());
            Objects.requireNonNull(result, "Try alternative error mapper result is a null object");
            return recreateOther(result);
        }
        else {
            return retype();
        }
    }

    /**
     * Recovers from an error with value provided by the given {@link Function}.
     * If this Try is an error with a matching class, a new Try with the same value as the provided Try is returned.
     * Otherwise the same Try is returned.
     *
     * @param errorClass           to be compared with this Try's error
     * @param errorMappingFunction to recover with
     *
     * @return a provided Try if this is an error, otherwise the same Try
     */
    @NotNull
    @ExtensibleFluentChain
    @Contract(value = "null, null -> fail; _, null -> fail; null, _ -> fail", pure = true)
    default <ErrorType extends Throwable> NewTryType recoverFlatMap(@NotNull Class<ErrorType> errorClass,
                                                                    @NotNull Function<? super Throwable, ? extends TryPrototype<SuccessType, ?>> errorMappingFunction) {
        if (isError() && isErrorTypeOf(errorClass)) {
            TryPrototype<SuccessType, ?> result = errorMappingFunction.apply(getError());
            Objects.requireNonNull(result, "Try alternative error mapper result is a null object");
            return recreateOther(result);
        }
        else {
            return retype();
        }
    }

    /**
     * Note that this method does not check if this Try is an error.
     * Therefore it might throw if this is a success Try.
     *
     * @param errorClass to be compared with this Try's error
     *
     * @return true if the class of this error is equal or is a subclass of the given class
     */
    default boolean isErrorTypeOf(@NotNull Class<? extends Throwable> errorClass) {
        Class<? extends Throwable> tryErrorClass = getError().getClass();
        return errorClass.equals(tryErrorClass) || errorClass.isAssignableFrom(tryErrorClass);
    }

}
