package io.kofun;

import io.kofun.prototypes.TryPrototype;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
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


}
