package io.kofun;

/**
 * A {@link java.util.function.Predicate} equivalent that may throw a checked exception.
 *
 * @param <TestedType> the type of the value tested by the predicate
 * @param <ErrorType>  the possible checked exception to be thrown
 */
public interface CheckedPredicate<TestedType, ErrorType extends Throwable> {

    boolean test(TestedType argument) throws ErrorType;

}
