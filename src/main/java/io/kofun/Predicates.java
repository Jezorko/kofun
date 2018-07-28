package io.kofun;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

import static io.kofun.Predicate.alwaysFalse;
import static io.kofun.Predicate.alwaysTrue;

/**
 * A collection of helpful static methods relevant to {@link Predicate}.
 */
public final class Predicates extends StaticMethodsCollection {

    /**
     * @throws InstantiationOfStaticMethodsCollectionException on each call
     */
    @Contract(value = "-> fail", pure = true)
    private Predicates() throws InstantiationOfStaticMethodsCollectionException {
        super();
    }

    /**
     * Negates given predicate using {@link java.util.function.Predicate#negate()} method.
     *
     * @param predicate to be negated
     * @param <T>       type tested by the predicate
     *
     * @return negated predicate
     */
    @NotNull
    public static <T> Predicate<T> not(@NotNull java.util.function.Predicate<T> predicate) {
        final java.util.function.Predicate<T> negatedPredicate = predicate.negate();
        Objects.requireNonNull(negatedPredicate, "Predicate negation resulted in a null object");
        return negatedPredicate::test;
    }

    /**
     * Applies boolean function AND to each given predicate.
     * If the array of predicates is empty, a predicate that is always true is returned.
     * The resulting predicate is short-circuiting and therefore will not perform tests if they are not necessary.
     *
     * @param predicates      array of predicates to be chained with AND
     * @param <T>             type of value tested
     * @param <PredicateType> type of predicates to be combined
     *
     * @return a new predicate
     */
    @SafeVarargs
    public static <T, PredicateType extends java.util.function.Predicate<? extends T>> Predicate<T> and(@NotNull PredicateType... predicates) {
        if (predicates.length == 0) {
            return alwaysTrue();
        }
        return value -> {
            for (java.util.function.Predicate predicate : predicates) {
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
     * @param predicates      array of predicates to be chained with OR
     * @param <T>             type of value tested
     * @param <PredicateType> type of predicates to be combined
     *
     * @return a new predicate
     */
    @SafeVarargs
    public static <T, PredicateType extends java.util.function.Predicate<? extends T>> Predicate<T> or(@NotNull PredicateType... predicates) {
        if (predicates.length == 0) {
            return alwaysTrue();
        }
        return value -> {
            for (java.util.function.Predicate predicate : predicates) {
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
    public static <T> Predicate<Iterable<? extends T>> eachElement(@NotNull java.util.function.Predicate<T> predicate) {
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
    public static <T> Predicate<Iterable<T>> neitherElement(@NotNull java.util.function.Predicate<T> predicate) {
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
    @SafeVarargs
    public static <T> Predicate<T> isIn(@NotNull T... testedValues) {
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
    @SafeVarargs
    public static <T> Predicate<T> isNotIn(@NotNull T... testedValues) {
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
    public static <T, FieldOfT> Predicate<T> compose(@NotNull Function<? super T, ? extends FieldOfT> fieldGetter,
                                                     @NotNull java.util.function.Predicate<? super FieldOfT> fieldPredicate) {
        return t -> fieldPredicate.test(fieldGetter.apply(t));
    }

    private static <T> boolean testAgainstPredicate(T valueToTest, @NotNull java.util.function.Predicate<T> predicate) {
        return predicate.test(valueToTest);
    }

}
