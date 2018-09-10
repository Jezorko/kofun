package io.kofun;

import io.kofun.prototypes.TryPrototype;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Try<SuccessType> extends TryPrototype<SuccessType, Try> {

    @NotNull
    static <SuccessType> Try<SuccessType> trySupplier(@NotNull Supplier<SuccessType> supplier) {
        return tryOf(supplier::get);
    }

    @NotNull
    static <SuccessType> Try<SuccessType> tryCallable(@NotNull Callable<SuccessType> callable) {
        return tryOf(callable::call);
    }

    @NotNull
    static Try<Void> tryRunnable(@NotNull Runnable runnable) {
        return tryRun(runnable::run);
    }

    @NotNull
    static Try<Void> tryRun(@NotNull CheckedRunnable runnable) {
        return tryOf(() -> {
            runnable.run();
            return null;
        });
    }

    @NotNull
    static <SuccessType> Try<SuccessType> tryOf(@NotNull CheckedSupplier<SuccessType, ? extends Throwable> supplier) {
        try {
            return new SuccessTry<>(supplier.get());
        } catch (Throwable error) {
            return new ErrorTry<>(error);
        }
    }

    @NotNull
    static <SuccessType> Try<SuccessType> success(SuccessType success) {
        return new SuccessTry<>(success);
    }

    @NotNull
    static <SuccessType> Try<SuccessType> error(Throwable error) {
        return new ErrorTry<>(error);
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
    default Try<SuccessType> onSuccessTryConsumer(@NotNull Consumer<SuccessType> consumer) {
        return TryPrototype.super.onSuccessTryConsumer(consumer);
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
    default Try<SuccessType> onSuccessTry(@NotNull CheckedConsumer<SuccessType, ? extends Throwable> consumer) {
        return TryPrototype.super.onSuccessTry(consumer);
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
}
