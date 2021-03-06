package io.kofun;

import io.kofun.prototypes.TryPrototype;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Try<SuccessType> extends TryPrototype<SuccessType, Try> {

    Try<?> EMPTY_SUCCESS = new SuccessTry<>(null);

    /**
     * A static constructor that creates a new Try from the supplied value.
     * If the supplier throws, a new error Try containing the thrown exception is created.
     *
     * @param supplier      to provide the result
     * @param <SuccessType> the type of the supplied result
     *
     * @return a new Try containing the supplied result or an error
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    static <SuccessType> Try<SuccessType> trySupplier(@NotNull Supplier<SuccessType> supplier) {
        return tryOf(supplier::get);
    }

    /**
     * A static constructor that creates a new Try from the supplied value.
     * If the callable throws, a new error Try containing the thrown exception is created.
     *
     * @param callable      to provide the result
     * @param <SuccessType> the type of the supplied result
     *
     * @return a new Try containing the supplied result or an error
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    static <SuccessType> Try<SuccessType> tryCallable(@NotNull Callable<SuccessType> callable) {
        return tryOf(callable::call);
    }

    /**
     * A static constructor that runs the runnable and creates a new empty Try.
     * If the runnable throws, a new error Try containing the thrown exception is created.
     *
     * @param runnable to be executed
     *
     * @return a new empty Try
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    static Try<Void> tryRunnable(@NotNull Runnable runnable) {
        return tryRun(runnable::run);
    }

    /**
     * A static constructor that runs the checked runnable and creates a new empty Try.
     * If the runnable throws, a new error Try containing the thrown exception is created.
     *
     * @param runnable to be executed
     *
     * @return a new empty Try
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    static Try<Void> tryRun(@NotNull CheckedRunnable runnable) {
        return tryOf(() -> {
            runnable.run();
            return null;
        });
    }

    /**
     * A static constructor that creates a new Try from the supplied value.
     * If the supplier throws, a new error Try containing the thrown exception is created.
     *
     * @param supplier      to provide the result
     * @param <SuccessType> the type of the supplied result
     *
     * @return a new Try containing the supplied result or an error
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    static <SuccessType> Try<SuccessType> tryOf(@NotNull CheckedSupplier<SuccessType, ? extends Throwable> supplier) {
        try {
            return success(supplier.get());
        } catch (Throwable error) {
            return error(error);
        }
    }

    /**
     * Returns an empty Try (with null for a result).
     *
     * @param <SuccessType> the type of the success result
     *
     * @return an empty Try
     */
    @NotNull
    @SuppressWarnings("unchecked")
    @Contract(value = "-> !null", pure = true)
    static <SuccessType> Try<SuccessType> emptySuccess() {
        return (Try<SuccessType>) EMPTY_SUCCESS;
    }

    /**
     * Creates a new success Try from given value.
     * Note that exceptions may indicate that operation was successful,
     * therefore they are allowed to be a value of success Try.
     * If you would like to create a Try that indicates an error,
     * use the {@link #error(Throwable)} method.
     * Also note that this method may not always create a new Try instance,
     * for example when the values (like null) are indistinguishable.
     *
     * @param success       the new Try value
     * @param <SuccessType> the type of the success result
     *
     * @return a new success Try instance
     */
    @NotNull
    @Contract(value = "null -> !null; !null -> new", pure = true)
    static <SuccessType> Try<SuccessType> success(SuccessType success) {
        return success == null ? emptySuccess() : new SuccessTry<>(success);
    }

    /**
     * Creates a new error Try from given value.
     *
     * @param error         the new Try value
     * @param <SuccessType> the type of the success result
     *
     * @return a new error Try instance
     */
    @NotNull
    @Contract(value = "null -> fail; !null -> new", pure = true)
    static <SuccessType> Try<SuccessType> error(@NotNull Throwable error) {
        return new ErrorTry<>(error);
    }

    /**
     * Creates a new {@link Supplier} that will emit success Try objects containing given value.
     *
     * @param success       to create new Try objects with
     * @param <SuccessType> the type of the success result
     *
     * @return a supplier emitting success Try objects
     */
    @NotNull
    static <SuccessType> Supplier<Try<SuccessType>> successes(SuccessType success) {
        Try<SuccessType> result = success(success);
        return () -> result;
    }

    /**
     * Creates a new {@link Supplier} that will emit error Try objects containing given value.
     *
     * @param error         to create new Try objects with
     * @param <SuccessType> the type of the success result
     *
     * @return a supplier emitting error Try objects
     */
    @NotNull
    static <SuccessType> Supplier<Try<SuccessType>> errors(Throwable error) {
        Try<SuccessType> result = error(error);
        return () -> result;
    }

    @NotNull
    @Override
    default <AnySuccessType> Try<AnySuccessType> recreateSuccess(AnySuccessType success) {
        return success(success);
    }

    @NotNull
    @Override
    default <AnySuccessType> Try<AnySuccessType> recreateError(Throwable error) {
        return error(error);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <TryType extends TryPrototype<AnySuccessType, ?>, AnySuccessType> Try<AnySuccessType> recreateOther(TryType tryPrototype) {
        Try<AnySuccessType> retypedPrototype = (Try<AnySuccessType>) tryPrototype.retype();
        return retypedPrototype.isSuccess() ? success(retypedPrototype.getSuccess()) : error(retypedPrototype.getError());
    }

    /* Reimplementing @ExtensibleFluentChain methods */

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onSuccess(@NotNull Consumer<? super SuccessType> successConsumer) {
        return TryPrototype.super.onSuccess(successConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onSuccessTryConsumer(@NotNull Consumer<SuccessType> successConsumer) {
        return TryPrototype.super.onSuccessTryConsumer(successConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onSuccessTryRunnable(@NotNull Runnable runnable) {
        return TryPrototype.super.onSuccessTryRunnable(runnable);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onSuccessTryRun(@NotNull CheckedRunnable runnable) {
        return TryPrototype.super.onSuccessTryRun(runnable);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onSuccessTry(@NotNull CheckedConsumer<SuccessType, ? extends Throwable> successConsumer) {
        return TryPrototype.super.onSuccessTry(successConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onError(@NotNull Consumer<? super Throwable> errorConsumer) {
        return TryPrototype.super.onError(errorConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onErrorTryConsumer(@NotNull Consumer<? super Throwable> errorConsumer) {
        return TryPrototype.super.onErrorTryConsumer(errorConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onErrorTryRunnable(@NotNull Runnable runnable) {
        return TryPrototype.super.onErrorTryRunnable(runnable);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onErrorTryRun(@NotNull CheckedRunnable runnable) {
        return TryPrototype.super.onErrorTryRun(runnable);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> onErrorTry(@NotNull CheckedConsumer<? super Throwable, ? extends Throwable> errorConsumer) {
        return TryPrototype.super.onErrorTry(errorConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> onError(Class<ErrorType> errorClass, @NotNull Consumer<? super ErrorType> errorConsumer) {
        return TryPrototype.super.onError(errorClass, errorConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> onErrorTryConsumer(@NotNull Class<ErrorType> errorClass, @NotNull Consumer<? super ErrorType> errorConsumer) {
        return TryPrototype.super.onErrorTryConsumer(errorClass, errorConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> onErrorTryRunnable(@NotNull Class<ErrorType> errorClass, @NotNull Runnable runnable) {
        return TryPrototype.super.onErrorTryRunnable(errorClass, runnable);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> onErrorTryRun(@NotNull Class<ErrorType> errorClass, @NotNull CheckedRunnable runnable) {
        return TryPrototype.super.onErrorTryRun(errorClass, runnable);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> onErrorTry(@NotNull Class<ErrorType> errorClass,
                                                                      @NotNull CheckedConsumer<? super ErrorType, ? extends Throwable> errorConsumer) {
        return TryPrototype.super.onErrorTry(errorClass, errorConsumer);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<ErrorType> switchWithError() {
        return TryPrototype.super.switchWithError();
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> filter(@NotNull Predicate<? super SuccessType> predicate) {
        return TryPrototype.super.filter(predicate);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> filterGet(@NotNull Predicate<? super SuccessType> predicate, @NotNull Supplier<? extends ErrorType> errorSupplier) {
        return TryPrototype.super.filterGet(predicate, errorSupplier);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> filterMap(@NotNull Predicate<? super SuccessType> predicate,
                                                                     @NotNull Function<? super SuccessType, ? extends ErrorType> errorMapper) {
        return TryPrototype.super.filterMap(predicate, errorMapper);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> filterTry(@NotNull CheckedPredicate<? super SuccessType, ? extends Throwable> predicate) {
        return TryPrototype.super.filterTry(predicate);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> filterTryGet(@NotNull CheckedPredicate<? super SuccessType, ? extends Throwable> predicate,
                                                                        @NotNull CheckedSupplier<? extends ErrorType, ? extends Throwable> errorSupplier) {
        return TryPrototype.super.filterTryGet(predicate, errorSupplier);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> filterTryMap(@NotNull CheckedPredicate<? super SuccessType, ? extends Throwable> predicate,
                                                                        @NotNull CheckedFunction<? super SuccessType, ? extends ErrorType, ? extends Throwable> errorMapper) {
        return TryPrototype.super.filterTryMap(predicate, errorMapper);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewSuccessType> Try<NewSuccessType> map(@NotNull Function<? super SuccessType, ? extends NewSuccessType> mappingFunction) {
        return TryPrototype.super.map(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewErrorType extends Throwable> Try<SuccessType> mapError(@NotNull Function<? super Throwable, ? extends NewErrorType> mappingFunction) {
        return TryPrototype.super.mapError(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewErrorType extends Throwable> Try<SuccessType> mapError(Class<? extends Throwable> errorClass,
                                                                       @NotNull Function<? super Throwable, ? extends NewErrorType> mappingFunction) {
        return TryPrototype.super.mapError(errorClass, mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewSuccessType, ErrorType extends Throwable> Try<NewSuccessType> mapTry(
            @NotNull CheckedFunction<? super SuccessType, ? extends NewSuccessType, ErrorType> mappingFunction) {
        return TryPrototype.super.mapTry(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewErrorType extends Throwable, ErrorType extends Throwable> Try<SuccessType> mapTryError(@NotNull CheckedFunction<? super Throwable, ? extends NewErrorType,
            ErrorType> mappingFunction) {
        return TryPrototype.super.mapTryError(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewErrorType extends Throwable, ErrorType extends Throwable> Try<SuccessType> mapTryError(Class<? extends Throwable> errorClass,
                                                                                                       @NotNull CheckedFunction<? super Throwable, ? extends NewErrorType,
                                                                                                               ErrorType> mappingFunction) {
        return TryPrototype.super.mapTryError(errorClass, mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewSuccessType> Try<NewSuccessType> flatMap(@NotNull Function<? super SuccessType, ? extends TryPrototype<NewSuccessType, ?>> mappingFunction) {
        return TryPrototype.super.flatMap(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> flatMapError(@NotNull Function<? super Throwable, ? extends TryPrototype<SuccessType, ?>> mappingFunction) {
        return TryPrototype.super.flatMapError(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> flatMapError(Class<? extends Throwable> errorClass, @NotNull Function<? super Throwable, ? extends TryPrototype<SuccessType, ?>> mappingFunction) {
        return TryPrototype.super.flatMapError(errorClass, mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <NewSuccessType, ErrorType extends Throwable> Try<NewSuccessType> flatMapTry(
            @NotNull CheckedFunction<? super SuccessType, ? extends TryPrototype<NewSuccessType, ?>, ErrorType> mappingFunction) {
        return TryPrototype.super.flatMapTry(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> flatMapTryError(
            @NotNull CheckedFunction<? super Throwable, ? extends TryPrototype<SuccessType, ?>, ErrorType> mappingFunction) {
        return TryPrototype.super.flatMapTryError(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> flatMapTryError(@NotNull Class<? extends Throwable> errorClass,
                                                                           @NotNull CheckedFunction<? super Throwable, ? extends TryPrototype<SuccessType, ?>, ErrorType> mappingFunction) {
        return TryPrototype.super.flatMapTryError(errorClass, mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <OtherTry extends TryPrototype<SuccessType, ?>> Try<SuccessType> or(@NotNull OtherTry other) {
        return TryPrototype.super.or(other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <OtherTry extends TryPrototype<SuccessType, ?>> Try<SuccessType> orElse(@NotNull Supplier<? extends OtherTry> otherSupplier) {
        return TryPrototype.super.orElse(otherSupplier);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> recover(SuccessType other) {
        return TryPrototype.super.recover(other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> recover(@NotNull Class<ErrorType> errorClass, SuccessType other) {
        return TryPrototype.super.recover(errorClass, other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> recoverGet(@NotNull Supplier<SuccessType> otherSupplier) {
        return TryPrototype.super.recoverGet(otherSupplier);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> recoverGet(@NotNull Class<ErrorType> errorClass, @NotNull Supplier<SuccessType> otherSupplier) {
        return TryPrototype.super.recoverGet(errorClass, otherSupplier);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> recoverMap(@NotNull Function<? super Throwable, SuccessType> mappingFunction) {
        return TryPrototype.super.recoverMap(mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> recoverMap(@NotNull Class<ErrorType> errorClass, @NotNull Function<ErrorType, SuccessType> mappingFunction) {
        return TryPrototype.super.recoverMap(errorClass, mappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> recoverFlat(@NotNull TryPrototype<SuccessType, ?> other) {
        return TryPrototype.super.recoverFlat(other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> recoverFlat(@NotNull Class<ErrorType> errorClass, @NotNull TryPrototype<SuccessType, ?> other) {
        return TryPrototype.super.recoverFlat(errorClass, other);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> recoverFlatGet(@NotNull Supplier<? extends TryPrototype<SuccessType, ?>> otherSupplier) {
        return TryPrototype.super.recoverFlatGet(otherSupplier);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> recoverFlatGet(@NotNull Class<ErrorType> errorClass,
                                                                          @NotNull Supplier<? extends TryPrototype<SuccessType, ?>> otherSupplier) {
        return TryPrototype.super.recoverFlatGet(errorClass, otherSupplier);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default Try<SuccessType> recoverFlatMap(@NotNull Function<? super Throwable, ? extends TryPrototype<SuccessType, ?>> errorMappingFunction) {
        return TryPrototype.super.recoverFlatMap(errorMappingFunction);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    default <ErrorType extends Throwable> Try<SuccessType> recoverFlatMap(@NotNull Class<ErrorType> errorClass,
                                                                          @NotNull Function<? super Throwable, ? extends TryPrototype<SuccessType, ?>> errorMappingFunction) {
        return TryPrototype.super.recoverFlatMap(errorClass, errorMappingFunction);
    }

}