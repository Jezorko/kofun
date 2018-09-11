package io.kofun;

/**
 * A {@link java.util.function.Function} equivalent that may throw a checked exception.
 *
 * @param <ArgumentType> the type of the function's argument
 * @param <ResultType>   the result type of the function
 * @param <ErrorType>    the possible checked exception to be thrown
 */
public interface CheckedFunction<ArgumentType, ResultType, ErrorType extends Throwable> {

    ResultType apply(ArgumentType argument) throws ErrorType;

}
