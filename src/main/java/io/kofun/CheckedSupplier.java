package io.kofun;

/**
 * A {@link java.util.function.Supplier} equivalent that may throw a checked exception.
 *
 * @param <T>         the type of result value
 * @param <ErrorType> the possible checked exception to be thrown
 */
public interface CheckedSupplier<T, ErrorType extends Throwable> {

    T get() throws ErrorType;

}
