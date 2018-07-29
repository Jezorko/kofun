package io.kofun.prototypes;

import io.kofun.CheckedConsumer;
import io.kofun.CheckedRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface TryPrototype<SuccessType, NewTryType extends TryPrototype> extends FluentPrototype<NewTryType> {

    SuccessType getSuccess();

    @NotNull
    Throwable getError();

    boolean isSuccess();

    default boolean isError() {
        return !isSuccess();
    }

    @NotNull
    @ExtensibleFluentChain
    <AnySuccessType> NewTryType recreateSuccess(AnySuccessType success);

    @NotNull
    @ExtensibleFluentChain
    <AnySuccessType> NewTryType recreateError(Throwable error);

    @NotNull
    @ExtensibleFluentChain
    default NewTryType onSuccess(@NotNull Consumer<? super SuccessType> successConsumer) {
        if (isSuccess()) {
            successConsumer.accept(getSuccess());
        }
        return retype();
    }

    @NotNull
    @ExtensibleFluentChain
    default NewTryType onSuccessTryConsumer(@NotNull Consumer<SuccessType> consumer) {
        return onSuccessTry(consumer::accept);
    }

    @NotNull
    @ExtensibleFluentChain
    default NewTryType onSuccessTryRunnable(@NotNull Runnable runnable) {
        return onSuccessTryRun(runnable::run);
    }

    @NotNull
    @ExtensibleFluentChain
    default NewTryType onSuccessTryRun(@NotNull CheckedRunnable runnable) {
        return onSuccessTry(success -> runnable.run());
    }

    @NotNull
    @ExtensibleFluentChain
    default NewTryType onSuccessTry(@NotNull CheckedConsumer<SuccessType, ? extends Throwable> consumer) {
        if (isSuccess()) {
            try {
                consumer.accept(getSuccess());
                return retype();
            } catch (Throwable error) {
                return recreateError(error);
            }
        }
        else {
            return retype();
        }
    }

    @NotNull
    @ExtensibleFluentChain
    default NewTryType onError(@NotNull Consumer<? super Throwable> errorConsumer) {
        if (isError()) {
            errorConsumer.accept(getError());
        }
        return retype();
    }

    @NotNull
    @ExtensibleFluentChain
    default <ErrorType extends Throwable> NewTryType onError(Class<ErrorType> errorClass, @NotNull Consumer<? super ErrorType> errorConsumer) {
        if (isError() && errorClass.isAssignableFrom(getError().getClass())) {
            errorConsumer.accept(errorClass.cast(getError()));
        }
        return retype();
    }

}
