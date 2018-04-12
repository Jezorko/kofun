package com.github.jezorko.kofun;

import com.github.jezorko.kofun.ExampleObject.ExampleInnerObject;
import org.junit.Test;

import java.util.Objects;
import java.util.function.Predicate;

import static com.github.jezorko.kofun.AdvancedPredicate.alwaysFalse;
import static com.github.jezorko.kofun.AdvancedPredicate.alwaysTrue;
import static com.github.jezorko.kofun.AssertionUtils.*;
import static com.github.jezorko.kofun.Predicates.*;
import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.function.Function.identity;
import static org.junit.Assert.*;

public class PredicatesTest {

    private final static Integer PRIMITIVE_FIELD_VALUE = 10;
    private final static Integer PRIMITIVE_INNER_FIELD_VALUE = 5;
    private final ExampleObject exampleObject = new ExampleObject(PRIMITIVE_FIELD_VALUE, new ExampleInnerObject(PRIMITIVE_INNER_FIELD_VALUE));

    @Test
    public void not_shouldNegatePredicate() {
        // expect:
        assertNotPresent("negating simple true predicate",
                         of(1).filter(not(value -> true))
                        );

        assertPresent("negating simple false predicate",
                      of(1).filter(not(value -> false))
                     );

        assertNotPresent("negating simple equals predicate",
                         of(1).filter(not(value -> value == 1))
                        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_shouldThrowWhenPredicateIsNull() {
        not(null);
    }

    @Test
    public void and_shouldBeTheSameAsApplyingLogicalAnd() {
        // given:
        Predicate<Integer> p1 = value -> value > 3;
        Predicate<Integer> p2 = value -> value <= 5;
        Predicate<Integer> p3 = value -> value == 5;
        Predicate<Integer> p4 = value -> value != 5;
        Predicate<Integer> p5 = Objects::nonNull;

        Integer value = 5;

        // expect:
        assertEquals(p1.test(value),
                     and(p1).test(5));
        assertEquals(p1.test(value) && p2.test(value) && p3.test(value),
                     and(p1, p2, p3).test(5));
        assertEquals(p1.test(value) && p2.test(value) && p3.test(value) && p4.test(value),
                     and(p1, p2, p3, p4).test(5));
        assertEquals(p1.test(value) && p2.test(value) && p3.test(value) && p5.test(value),
                     and(p1, p2, p3, p5).test(5));
        assertEquals(p4.test(value),
                     and(p4).test(5));
        assertEquals(p5.test(value),
                     and(p5).test(5));
    }

    @Test
    public void and_shouldBeShortCircuiting() {
        // given:
        Predicate<Integer> throwingPredicate = p -> {throw new AssertionError("should not be executed");};

        // expect:
        and(alwaysFalse(), throwingPredicate).test(null);
    }

    @Test
    public void and_shouldMatchTheTruthTable() {
        // expect:
        assertMatchesTruthTable(table -> table.withRow(0, 0, 0)
                                              .withRow(0, 1, 0)
                                              .withRow(1, 0, 0)
                                              .withRow(1, 1, 1),
                                Predicates::and);
    }

    @Test
    public void and_shouldAlwaysReturnTrueWhenNoPredicatesAreProvided() {
        // expect:
        assertTrue(and().test(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void and_shouldThrowIfPredicateArrayIsNull() {
        and(null);
    }

    @Test
    public void or_shouldBeTheSameAsApplyingLogicalOr() {
        // given:
        Predicate<Integer> p1 = value -> value > 3;
        Predicate<Integer> p2 = value -> value <= 5;
        Predicate<Integer> p3 = value -> value == 5;
        Predicate<Integer> p4 = value -> value != 5;
        Predicate<Integer> p5 = Objects::nonNull;

        Integer value = 5;

        // expect:
        assertEquals(p1.test(value),
                     or(p1).test(5));
        assertEquals(p1.test(value) || p2.test(value) || p3.test(value),
                     or(p1, p2, p3).test(5));
        assertEquals(p1.test(value) || p2.test(value) || p3.test(value) || p4.test(value),
                     or(p1, p2, p3, p4).test(5));
        assertEquals(p1.test(value) || p2.test(value) || p3.test(value) || p5.test(value),
                     or(p1, p2, p3, p5).test(5));
        assertEquals(p4.test(value),
                     or(p4).test(5));
        assertEquals(p5.test(value),
                     or(p5).test(5));
    }

    @Test
    public void or_shouldBeShortCircuiting() {
        // given:
        Predicate<Integer> throwingPredicate = p -> {throw new AssertionError("should not be executed");};

        // expect:
        or(alwaysTrue(), throwingPredicate).test(null);
    }

    @Test
    public void or_shouldMatchTheTruthTable() {
        // expect:
        assertMatchesTruthTable(table -> table.withRow(0, 0, 0)
                                              .withRow(0, 1, 1)
                                              .withRow(1, 0, 1)
                                              .withRow(1, 1, 1),
                                Predicates::or);
    }

    @Test
    public void or_shouldAlwaysReturnTrueWhenNoPredicatesAreProvided() {
        // expect:
        assertTrue(or().test(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void or_shouldThrowIfPredicateArrayIsNull() {
        or(null);
    }

    @Test
    public void eachElement_shouldReturnTrueIfNeitherTestReturnsFalse() {
        // expect:
        assertPresent("filtering with always true predicate",
                      of(asList(1, 2, 3)).filter(eachElement(alwaysTrue())));
        assertPresent("filtering matching list elements",
                      of(asList(1, 2, 3)).filter(eachElement(element -> element > 0)));
    }

    @Test
    public void eachElement_shouldReturnFalseIfAnyTestReturnsFalse() {
        // expect:
        assertNotPresent("filtering not matching list elements",
                         of(asList(1, 2, 3)).filter(eachElement(element -> element > 4)));
        assertNotPresent("only one element matching",
                         of(asList(1, 2, 3)).filter(eachElement(element -> element == 2)));
        assertNotPresent("one element not matching",
                         of(asList(1, null, 3)).filter(eachElement(Objects::nonNull)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void eachElement_shouldThrowWhenPredicateIsNull() {
        eachElement(null);
    }

    @Test
    public void neitherElement_shouldReturnTrueIfNeitherTestReturnsTrue() {
        // expect:
        assertPresent("filtering with always false predicate",
                      of(asList(1, 2, 3)).filter(neitherElement(alwaysFalse())));
        assertPresent("filtering not matching list elements",
                      of(asList(1, 2, 3)).filter(neitherElement(element -> element > 4)));
    }

    @Test
    public void neitherElement_shouldReturnFalseIfAnyTestReturnsTrue() {
        // expect:
        assertNotPresent("filtering matching list elements",
                         of(asList(1, 2, 3)).filter(neitherElement(element -> element > 0)));
        assertNotPresent("one element matching",
                         of(asList(1, 2, 3)).filter(neitherElement(element -> element == 2)));
        assertNotPresent("all elements matching but one",
                         of(asList(1, null, 3)).filter(neitherElement(Objects::nonNull)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void neitherElement_shouldThrowWhenPredicateIsNull() {
        neitherElement(null);
    }

    @Test
    public void compose_shouldFilterInnerObjectRetrievedWithAGetter() {
        // expect:
        assertPresent("filtering inner primitive field",
                      of(exampleObject).filter(compose(ExampleObject::getPrimitiveField,
                                                       PRIMITIVE_FIELD_VALUE::equals))
                     );

        assertPresent("filtering field of inner object",
                      of(exampleObject).filter(compose(ExampleObject::getInnerObjectField,
                                                       compose(ExampleInnerObject::getPrimitiveField,
                                                               PRIMITIVE_INNER_FIELD_VALUE::equals)))
                     );
    }

    @Test
    public void isIn_shouldReturnFalseIfNoElementsAreProvided() {
        // expect:
        assertFalse(isIn().test(new Object()));
    }

    @Test
    public void isIn_shouldReturnTrueIfElementHasOneOfTheValues() {
        // expect:
        assertTrue(isIn(1, 2, 3).test(1));
        assertTrue(isIn(1, 2, 3).test(2));
        assertTrue(isIn(1, 2, 3).test(3));
        assertTrue(isIn(null, 2, 3).test(null));
        assertTrue(isIn(1, null, 3).test(null));
        assertTrue(isIn(1, 2, null).test(null));
    }

    @Test
    public void isIn_shouldReturnFalseIfElementHasNeitherOfTheValues() {
        // expect:
        assertFalse(isIn(1, 2, 3).test(null));
        assertFalse(isIn(1, 2, 3).test(4));
        assertFalse(isIn(null, null, null).test(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isIn_shouldThrowIfValuesAreNull() {
        isIn(null);
    }

    @Test
    public void isNotIn_shouldReturnTrueIfNoElementsAreProvided() {
        // expect:
        assertTrue(isNotIn().test(new Object()));
    }

    @Test
    public void isNotIn_shouldReturnFalseIfElementHasOneOfTheValues() {
        // expect:
        assertTrue(isNotIn(1, 2, 3).test(null));
        assertTrue(isNotIn(1, 2, 3).test(4));
        assertTrue(isNotIn(null, null, null).test(2));
    }

    @Test
    public void isNotIn_shouldReturnTrueIfElementHasNeitherOfTheValues() {
        // expect:
        assertFalse(isNotIn(1, 2, 3).test(1));
        assertFalse(isNotIn(1, 2, 3).test(2));
        assertFalse(isNotIn(1, 2, 3).test(3));
        assertFalse(isNotIn(null, 2, 3).test(null));
        assertFalse(isNotIn(1, null, 3).test(null));
        assertFalse(isNotIn(1, 2, null).test(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNotIn_shouldThrowIfValuesAreNull() {
        isNotIn(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compose_shouldThrowWhenGetterIsNull() {
        compose(null, ignore -> true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compose_shouldThrowWhenPredicateIsNull() {
        compose(identity(), null);
    }

}