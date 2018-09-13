package io.kofun;

/**
 * A {@link java.util.function.Consumer} equivalent that may throw a checked exception.
 *
 * @param <T>         the type of value to be consumed
 * @param <ErrorType> the possible checked exception to be thrown
 */
public interface CheckedConsumer<T, ErrorType extends Throwable> {

    void accept(T value) throws ErrorType;

}
