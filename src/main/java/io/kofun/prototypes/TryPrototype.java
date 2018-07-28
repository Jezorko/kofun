package io.kofun.prototypes;

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

}
