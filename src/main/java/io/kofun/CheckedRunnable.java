package io.kofun;

/**
 * A {@link Runnable} equivalent that may throw a checked exception.
 *
 * @param <ErrorType> the possible checked exception to be thrown
 */
public interface CheckedRunnable<ErrorType extends Throwable> {

    void run() throws ErrorType;

}
