package io.kofun;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

final class SuccessTry<SuccessType> implements Try<SuccessType> {

    private final SuccessType success;

    SuccessTry(SuccessType success) {
        this.success = success;
    }

    @Override
    public SuccessType getSuccess() {
        return success;
    }

    @NotNull
    @Override
    public Throwable getError() {
        throw new NoSuchElementException("No error present");
    }

    @Override
    public boolean isSuccess() {
        return true;
    }

}
