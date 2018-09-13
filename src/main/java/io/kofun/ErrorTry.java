package io.kofun;

import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Objects;

final class ErrorTry<SuccessType> implements Try<SuccessType> {

    private final Throwable error;

    ErrorTry(Throwable error) {
        Objects.requireNonNull(error, "Null error given to ErrorTry");
        rethrowIfNotRecoverable(error);
        this.error = error;
    }

    @Override
    public SuccessType getSuccess() {
        throw new NoSuchElementException("No success present");
    }

    @NotNull
    @Override
    public Throwable getError() {
        return error;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    private static void rethrowIfNotRecoverable(Throwable error) {
        if (error instanceof Error) {
            throw (Error) error;
        }
    }

}
