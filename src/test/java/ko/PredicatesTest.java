package ko;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.function.Function.identity;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicatesTest {

    private final static Integer PRIMITIVE_FIELD_VALUE = 10;
    private final static Integer PRIMITIVE_INNER_FIELD_VALUE = 5;
    private final ExampleObject exampleObject = new ExampleObject(PRIMITIVE_FIELD_VALUE, new ExampleObject.ExampleInnerObject(PRIMITIVE_INNER_FIELD_VALUE));

    @Test
    public void not_shouldNegatePredicate() {
        // expect:
        AssertionUtils.assertNotPresent("negating simple true predicate",
                                        of(1).filter(Predicates.not(value -> true))
                                       );

        AssertionUtils.assertPresent("negating simple false predicate",
                                     of(1).filter(Predicates.not(value -> false))
                                    );

        AssertionUtils.assertNotPresent("negating simple equals predicate",
                                        of(1).filter(Predicates.not(value -> value == 1))
                                       );
    }

    @Test(expected = IllegalArgumentException.class)
    public void not_shouldThrowWhenPredicateIsNull() {
        Predicates.not(null);
    }

    @Test(expected = NullPointerException.class)
    public void not_shouldThrowWhenPredicateNegationIsNull() {
        Predicates.not(new Predicate<Object>() {
            @Override
            public boolean test(Object o) {
                return false;
            }

            @Override
            public Predicate<Object> negate() {
                return null;
            }
        });
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
        Assert.assertEquals(p1.test(value),
                            Predicates.and(p1)
                                      .test(5));
        Assert.assertEquals(p1.test(value) && p2.test(value) && p3.test(value),
                            Predicates.and(p1, p2, p3)
                                      .test(5));
        Assert.assertEquals(p1.test(value) && p2.test(value) && p3.test(value) && p4.test(value),
                            Predicates.and(p1, p2, p3, p4)
                                      .test(5));
        Assert.assertEquals(p1.test(value) && p2.test(value) && p3.test(value) && p5.test(value),
                            Predicates.and(p1, p2, p3, p5)
                                      .test(5));
        Assert.assertEquals(p4.test(value),
                            Predicates.and(p4)
                                      .test(5));
        Assert.assertEquals(p5.test(value),
                            Predicates.and(p5)
                                      .test(5));
    }

    @Test
    public void and_shouldBeShortCircuiting() {
        // given:
        Predicate<Integer> throwingPredicate = p -> {throw new AssertionError("should not be executed");};

        // expect:
        Predicates.and(ko.Predicate.alwaysFalse(), throwingPredicate)
                  .test(null);
    }

    @Test
    public void and_shouldMatchTheTruthTable() {
        // expect:
        AssertionUtils.assertMatchesTruthTable(table -> table.withRow(0, 0, 0)
                                                             .withRow(0, 1, 0)
                                                             .withRow(1, 0, 0)
                                                             .withRow(1, 1, 1),
                                               Predicates::and);
    }

    @Test
    public void and_shouldAlwaysReturnTrueWhenNoPredicatesAreProvided() {
        // expect:
        assertTrue(Predicates.and()
                             .test(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void and_shouldThrowIfPredicateArrayIsNull() {
        Predicates.and(null);
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
        Assert.assertEquals(p1.test(value),
                            Predicates.or(p1)
                                      .test(5));
        Assert.assertEquals(p1.test(value) || p2.test(value) || p3.test(value),
                            Predicates.or(p1, p2, p3)
                                      .test(5));
        Assert.assertEquals(p1.test(value) || p2.test(value) || p3.test(value) || p4.test(value),
                            Predicates.or(p1, p2, p3, p4)
                                      .test(5));
        Assert.assertEquals(p1.test(value) || p2.test(value) || p3.test(value) || p5.test(value),
                            Predicates.or(p1, p2, p3, p5)
                                      .test(5));
        Assert.assertEquals(p4.test(value),
                            Predicates.or(p4)
                                      .test(5));
        Assert.assertEquals(p5.test(value),
                            Predicates.or(p5)
                                      .test(5));
    }

    @Test
    public void or_shouldBeShortCircuiting() {
        // given:
        Predicate<Integer> throwingPredicate = p -> {throw new AssertionError("should not be executed");};

        // expect:
        Predicates.or(ko.Predicate.alwaysTrue(), throwingPredicate)
                  .test(null);
    }

    @Test
    public void or_shouldMatchTheTruthTable() {
        // expect:
        AssertionUtils.assertMatchesTruthTable(table -> table.withRow(0, 0, 0)
                                                             .withRow(0, 1, 1)
                                                             .withRow(1, 0, 1)
                                                             .withRow(1, 1, 1),
                                               Predicates::or);
    }

    @Test
    public void or_shouldAlwaysReturnTrueWhenNoPredicatesAreProvided() {
        // expect:
        assertTrue(Predicates.or()
                             .test(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void or_shouldThrowIfPredicateArrayIsNull() {
        Predicates.or(null);
    }

    @Test
    public void eachElement_shouldReturnTrueIfNeitherTestReturnsFalse() {
        // expect:
        AssertionUtils.assertPresent("filtering with always true predicate",
                                     of(asList(1, 2, 3)).filter(Predicates.eachElement(ko.Predicate.alwaysTrue())));
        AssertionUtils.assertPresent("filtering matching list elements",
                                     of(asList(1, 2, 3)).filter(Predicates.eachElement(element -> element > 0)));
    }

    @Test
    public void eachElement_shouldReturnFalseIfAnyTestReturnsFalse() {
        // expect:
        AssertionUtils.assertNotPresent("filtering not matching list elements",
                                        of(asList(1, 2, 3)).filter(Predicates.eachElement(element -> element > 4)));
        AssertionUtils.assertNotPresent("only one element matching",
                                        of(asList(1, 2, 3)).filter(Predicates.eachElement(element -> element == 2)));
        AssertionUtils.assertNotPresent("one element not matching",
                                        of(asList(1, null, 3)).filter(Predicates.eachElement(Objects::nonNull)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void eachElement_shouldThrowWhenPredicateIsNull() {
        Predicates.eachElement(null);
    }

    @Test
    public void neitherElement_shouldReturnTrueIfNeitherTestReturnsTrue() {
        // expect:
        AssertionUtils.assertPresent("filtering with always false predicate",
                                     of(asList(1, 2, 3)).filter(Predicates.neitherElement(ko.Predicate.alwaysFalse())));
        AssertionUtils.assertPresent("filtering not matching list elements",
                                     of(asList(1, 2, 3)).filter(Predicates.neitherElement(element -> element > 4)));
    }

    @Test
    public void neitherElement_shouldReturnFalseIfAnyTestReturnsTrue() {
        // expect:
        AssertionUtils.assertNotPresent("filtering matching list elements",
                                        of(asList(1, 2, 3)).filter(Predicates.neitherElement(element -> element > 0)));
        AssertionUtils.assertNotPresent("one element matching",
                                        of(asList(1, 2, 3)).filter(Predicates.neitherElement(element -> element == 2)));
        AssertionUtils.assertNotPresent("all elements matching but one",
                                        of(asList(1, null, 3)).filter(Predicates.neitherElement(Objects::nonNull)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void neitherElement_shouldThrowWhenPredicateIsNull() {
        Predicates.neitherElement(null);
    }

    @Test
    public void compose_shouldFilterInnerObjectRetrievedWithAGetter() {
        // expect:
        AssertionUtils.assertPresent("filtering inner primitive field",
                                     of(exampleObject).filter(Predicates.compose(ExampleObject::getPrimitiveField,
                                                                                 PRIMITIVE_FIELD_VALUE::equals))
                                    );

        AssertionUtils.assertPresent("filtering field of inner object",
                                     of(exampleObject).filter(Predicates.compose(ExampleObject::getInnerObjectField,
                                                                                 Predicates.compose(ExampleObject.ExampleInnerObject::getPrimitiveField,
                                                                                                    PRIMITIVE_INNER_FIELD_VALUE::equals)))
                                    );
    }

    @Test
    public void isIn_shouldReturnFalseIfNoElementsAreProvided() {
        // expect:
        assertFalse(Predicates.isIn()
                              .test(new Object()));
    }

    @Test
    public void isIn_shouldReturnTrueIfElementHasOneOfTheValues() {
        // expect:
        assertTrue(Predicates.isIn(1, 2, 3)
                             .test(1));
        assertTrue(Predicates.isIn(1, 2, 3)
                             .test(2));
        assertTrue(Predicates.isIn(1, 2, 3)
                             .test(3));
        assertTrue(Predicates.isIn(null, 2, 3)
                             .test(null));
        assertTrue(Predicates.isIn(1, null, 3)
                             .test(null));
        assertTrue(Predicates.isIn(1, 2, null)
                             .test(null));
    }

    @Test
    public void isIn_shouldReturnFalseIfElementHasNeitherOfTheValues() {
        // expect:
        assertFalse(Predicates.isIn(1, 2, 3)
                              .test(null));
        assertFalse(Predicates.isIn(1, 2, 3)
                              .test(4));
        assertFalse(Predicates.isIn(null, null, null)
                              .test(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isIn_shouldThrowIfValuesAreNull() {
        Predicates.isIn(null);
    }

    @Test
    public void isNotIn_shouldReturnTrueIfNoElementsAreProvided() {
        // expect:
        assertTrue(Predicates.isNotIn()
                             .test(new Object()));
    }

    @Test
    public void isNotIn_shouldReturnFalseIfElementHasOneOfTheValues() {
        // expect:
        assertTrue(Predicates.isNotIn(1, 2, 3)
                             .test(null));
        assertTrue(Predicates.isNotIn(1, 2, 3)
                             .test(4));
        assertTrue(Predicates.isNotIn(null, null, null)
                             .test(2));
    }

    @Test
    public void isNotIn_shouldReturnTrueIfElementHasNeitherOfTheValues() {
        // expect:
        assertFalse(Predicates.isNotIn(1, 2, 3)
                              .test(1));
        assertFalse(Predicates.isNotIn(1, 2, 3)
                              .test(2));
        assertFalse(Predicates.isNotIn(1, 2, 3)
                              .test(3));
        assertFalse(Predicates.isNotIn(null, 2, 3)
                              .test(null));
        assertFalse(Predicates.isNotIn(1, null, 3)
                              .test(null));
        assertFalse(Predicates.isNotIn(1, 2, null)
                              .test(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void isNotIn_shouldThrowIfValuesAreNull() {
        Predicates.isNotIn(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compose_shouldThrowWhenGetterIsNull() {
        Predicates.compose(null, ignore -> true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compose_shouldThrowWhenPredicateIsNull() {
        Predicates.compose(identity(), null);
    }

}