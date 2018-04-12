package com.github.jezorko.kofun;

import org.jetbrains.annotations.NotNull;

import javax.naming.OperationNotSupportedException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.jezorko.kofun.AdvancedPredicate.alwaysFalse;
import static com.github.jezorko.kofun.AdvancedPredicate.alwaysTrue;

public final class Predicates {

    private Predicates() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Class " + Predicates.class.toString() + " is not meant to be instantiated");
    }

    /**
     * Negates given predicate using {@link Predicate#negate()} method.
     *
     * @param predicate to be negated
     * @param <T>       type tested by the predicate
     *
     * @return negated predicate
     */
    public static <T> AdvancedPredicate<T> not(@NotNull Predicate<? super T> predicate) {
        return value -> predicate.negate()
                                 .test(value);
    }

    /**
     * Applies boolean function AND to each given predicate.
     * If the array of predicates is empty, a predicate that is always true is returned.
     * The resulting predicate is short-circuiting and therefore will not perform tests if they are not necessary.
     *
     * @param predicates array of predicates to be chained with AND
     * @param <T>        type of value tested
     *
     * @return a new predicate
     */
    @SafeVarargs
    public static <T> AdvancedPredicate<T> and(@NotNull Predicate<? super T>... predicates) {
        if (predicates.length == 0) {
            return alwaysTrue();
        }
        return value -> {
            for (Predicate predicate : predicates) {
                @SuppressWarnings("unchecked") final boolean testResult = testAgainstPredicate(value, predicate);
                if (!testResult) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Applies boolean function OR to each given predicate.
     * If the array of predicates is empty, a predicate that is always true is returned.
     * The resulting predicate is short-circuiting and therefore will not perform tests if they are not necessary.
     *
     * @param predicates array of predicates to be chained with OR
     * @param <T>        type of value tested
     *
     * @return a new predicate
     */
    @SafeVarargs
    public static <T> AdvancedPredicate<T> or(@NotNull Predicate<? super T>... predicates) {
        if (predicates.length == 0) {
            return alwaysTrue();
        }
        return value -> {
            for (Predicate predicate : predicates) {
                @SuppressWarnings("unchecked") final boolean testResult = testAgainstPredicate(value, predicate);
                if (testResult) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Applies given predicate to multiple elements of the same type.
     * If test for any of the elements returns false, the resulting predicate will also return false.
     *
     * @param predicate to be applied to each element
     * @param <T>       type of elements to be tested
     *
     * @return a new predicate that tests each element
     */
    public static <T> AdvancedPredicate<Iterable<? extends T>> eachElement(@NotNull Predicate<? super T> predicate) {
        return values -> {
            if (values == null) {
                return true;
            }
            for (T t : values) {
                if (!predicate.test(t)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Applies given predicate to multiple elements of the same type.
     * If test for any of the elements returns true, the resulting predicate will return false.
     *
     * @param predicate to be applied to each element
     * @param <T>       type of elements to be tested
     *
     * @return a new predicate that tests each element
     */
    public static <T> AdvancedPredicate<Iterable<T>> neitherElement(@NotNull Predicate<? super T> predicate) {
        return ts -> {
            if (ts == null) {
                return false;
            }
            for (T t : ts) {
                if (predicate.test(t)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Creates a predicate that tests object against an array of values.
     * Equality is tested with the {@link Objects#equals(Object, Object)} method.
     * If object is equal to either one of values provided, the resulting predicate will return true.
     * If the array of values is empty, the resulting predicate will always return false.
     *
     * @param testedValues array of values that object is tested against
     * @param <T>          type of the object to test
     *
     * @return a predicate to test against given values
     */
    public static <T> AdvancedPredicate<T> isIn(@NotNull T... testedValues) {
        if (testedValues.length == 0) {
            return alwaysFalse();
        }
        return t -> {
            for (T expectedT : testedValues) {
                if (Objects.equals(t, expectedT)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Creates a predicate that tests object against an array of values.
     * Equality is tested with the {@link Objects#equals(Object, Object)} method.
     * If object is equal to either one of values provided, the resulting predicate will return false.
     * If the array of values is empty, the resulting predicate will always return true.
     *
     * @param testedValues array of values that object is tested against
     * @param <T>          type of the object to test
     *
     * @return a predicate to test against given values
     */
    public static <T> AdvancedPredicate<T> isNotIn(@NotNull T... testedValues) {
        if (testedValues.length == 0) {
            return alwaysTrue();
        }
        return t -> {
            for (T expectedT : testedValues) {
                if (Objects.equals(t, expectedT)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Builds a predicate that tests object's field.
     *
     * @param fieldGetter    a getter method used to obtain the target field
     * @param fieldPredicate predicate to be applied to the target field
     * @param <T>            type of outer object
     * @param <FieldOfT>     type of object's field
     *
     * @return a composed predicate that can be applied to the outer object
     */
    public static <T, FieldOfT> AdvancedPredicate<T> compose(@NotNull Function<? super T, ? extends FieldOfT> fieldGetter,
                                                             @NotNull Predicate<? super FieldOfT> fieldPredicate) {
        return t -> fieldPredicate.test(fieldGetter.apply(t));
    }

    private static <T> boolean testAgainstPredicate(T valueToTest, @NotNull Predicate<T> predicate) {
        return predicate.test(valueToTest);
    }

}
