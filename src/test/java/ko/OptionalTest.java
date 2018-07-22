package ko;

import ko.prototypes.OptionalPrototype;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static ko.ExtensibleFluentChainTestUtil.prototypeImplementation;
import static ko.ExtensibleFluentChainTestUtil.shouldReimplementAllExtensibleFluentChainMethods;
import static ko.Optional.optional;
import static org.junit.Assert.*;

public class OptionalTest {

    @Test
    public void shouldConformToExtensibleFluentChainContract() {
        shouldReimplementAllExtensibleFluentChainMethods(prototypeImplementation(OptionalPrototype.class, Optional.class));
    }

    @Test
    public void optional_fromNullShouldCreateAnEmptyOptional() {
        // when:
        Optional<?> optional = optional(null);

        // then:
        assertTrue(optional instanceof EmptyOptional);
    }

    @Test
    public void optional_fromValueShouldCreateAFullOptional() {
        // when:
        Object anyValue = new Object();
        Optional<?> optional = optional(anyValue);

        // then:
        assertTrue(optional instanceof FullOptional);
    }

    @Test
    public void empty_shouldAlwaysReturnTheSameEmptyOptional() {
        // expect:
        assertSame(Optional.empty(), Optional.empty());
        assertTrue(Optional.empty() instanceof EmptyOptional);
    }

    @Test
    public void fromJavaOptional_shouldYieldAnEmptyOptionalIfJavaOptionalWasEmpty() {
        // expect:
        assertTrue(Optional.fromJavaOptional(java.util.Optional.empty()) instanceof EmptyOptional);
    }

    @Test
    public void fromJavaOptional_shouldYieldAFullOptionalWithTheSameValueAsJavaOptional() {
        // given:
        final Object anyValue = new Object();
        java.util.Optional<?> javaOptional = java.util.Optional.of(anyValue);

        // when:
        Optional<?> optional = Optional.fromJavaOptional(javaOptional);

        // then:
        assertTrue(optional instanceof FullOptional);
        assertSame(javaOptional.get(), optional.get());
    }

    @Test
    public void onEmpty_andNot_onPresent_shouldExecuteIfOptionalIsEmpty() {
        // setup:
        AtomicBoolean executedOnEmpty = new AtomicBoolean(false);
        AtomicBoolean executedIfEmpty = new AtomicBoolean(false);

        // given:
        Optional<?> optional = Optional.empty();

        // when:
        assertSame(optional, optional.onPresent(v -> fail()));
        optional.ifPresent(v -> fail());
        assertSame(optional, optional.onEmpty(() -> executedOnEmpty.set(true)));
        optional.ifEmpty(() -> executedIfEmpty.set(true));

        // then:
        assertTrue(executedOnEmpty.get());
        assertTrue(executedIfEmpty.get());
    }

    @Test
    public void onPresent_andNot_onEmpty_shouldExecuteIfOptionalIsFull() {
        // setup:
        AtomicBoolean executedOnPresent = new AtomicBoolean(false);
        AtomicBoolean executedIfPresent = new AtomicBoolean(false);

        // given:
        final Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        assertSame(optional, optional.onEmpty(Assert::fail));
        optional.ifEmpty(Assert::fail);
        assertSame(optional, optional.onPresent(v -> {
            assertSame(anyValue, v);
            executedOnPresent.set(true);
        }));
        optional.ifPresent(v -> {
            assertSame(anyValue, v);
            executedIfPresent.set(true);
        });

        // then:
        assertTrue(executedOnPresent.get());
        assertTrue(executedIfPresent.get());
    }

    @Test
    public void ifPresentOrElse_shouldExecuteSecondAndNotExecuteFirstIfEmpty() {
        // setup:
        AtomicBoolean executedOnEmpty = new AtomicBoolean(false);

        // given:
        Optional<?> optional = Optional.empty();

        // when:
        optional.ifPresentOrElse(v -> Assert.fail(), () -> executedOnEmpty.set(true));

        // then:
        assertTrue(executedOnEmpty.get());
    }

    @Test
    public void ifPresentOrElse_shouldExecuteFirstAndNotExecuteSecondIfPresent() {
        // setup:
        AtomicBoolean executedOnPresent = new AtomicBoolean(false);

        // given:
        final Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        optional.ifPresentOrElse(v -> {
            assertSame(anyValue, v);
            executedOnPresent.set(true);
        }, Assert::fail);

        // then:
        assertTrue(executedOnPresent.get());
    }

    @Test
    public void filter_shouldReturnEmptyOptionalWhenPredicateDoesNotMatch() {
        // given:
        final Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> result = optional.filter(Objects::isNull);

        // then:
        assertFalse(result.isPresent());
        assertTrue(result instanceof EmptyOptional);
    }

    @Test
    public void filter_shouldReturnSameValueWhenPredicateMatches() {
        // given:
        final Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> result = optional.filter(Objects::nonNull);

        // then:
        assertTrue(result.isPresent());
        assertSame(anyValue, result.get());
    }

    @Test
    public void map_shouldReturnEmptyOptionalIfValueIsNotPresent() {
        // given:
        Optional<?> optional = Optional.empty();

        // when:
        Optional<String> result = optional.map(Objects::toString);

        // then:
        assertFalse(result.isPresent());
        assertTrue(result instanceof EmptyOptional);
    }

    @Test
    public void map_shouldReturnANewOptionalWithMappedValueIfValueIsPresent() {
        // given:
        final Object anyValue = new Object();
        final Object anyOtherValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> result = optional.map(value -> anyOtherValue);

        // then:
        assertTrue(result.isPresent());
        assertSame(anyOtherValue, result.get());
    }

    @Test
    public void flatMap_shouldNotThrowIfWasEmptyAndMappingFunctionReturnsNull() {
        // given:
        Optional<?> optional = Optional.empty();

        // when:
        Optional<?> result = optional.flatMap(v -> null);

        // then:
        assertFalse(result.isPresent());
        assertTrue(result instanceof EmptyOptional);
    }

    @Test(expected = NullPointerException.class)
    public void flatMap_shouldThrowIfWasFullAndMappingFunctionReturnsNull() {
        // given:
        final Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // expect:
        optional.flatMap(v -> null);
    }

    @Test
    public void flatMap_shouldReturnEmptyOptionalIfValueIsNotPresent() {
        // given:
        Optional<?> optional = Optional.empty();

        // when:
        Optional<String> result = optional.flatMap(v -> Optional.optional("test"));

        // then:
        assertFalse(result.isPresent());
        assertTrue(result instanceof EmptyOptional);
    }

    @Test
    public void flatMap_shouldReturnMappedOptionalIfValueIsPresent() {
        // given:
        final Object anyValue = new Object();
        final Object otherValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);
        Optional<?> mappedOptional = Optional.optional(otherValue);

        // when:
        Optional<?> result = optional.flatMap(v -> mappedOptional);

        // then:
        assertTrue(result.isPresent());
        assertSame(otherValue, result.get());
    }

    @Test
    public void mergeWith_shouldNotMergeIfBothValuesAreAbsentAndNoFallbackIsProvided() {
        // given:
        Optional<?> firstOptional = Optional.empty();
        Optional<?> secondOptional = Optional.empty();

        // when:
        Optional<?> firstMergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                               (o1, o2) -> {
                                                                   Assert.fail();
                                                                   return null;
                                                               });

        Optional<?> secondMergeResult = secondOptional.mergeWith(firstOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                                 (o1, o2) -> {
                                                                     Assert.fail();
                                                                     return null;
                                                                 });

        // then:
        assertFalse(firstMergeResult.isPresent());
        assertFalse(secondMergeResult.isPresent());
    }

    @Test
    public void mergeWith_shouldNotMergeIfFirstValueIsAbsentAndNoFallbackIsProvided() {
        // given:
        Optional<?> firstOptional = Optional.empty();
        Object anyValue = new Object();
        Optional<?> secondOptional = Optional.optional(anyValue);

        // when:
        Optional<?> mergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                          (o1, o2) -> {
                                                              Assert.fail();
                                                              return null;
                                                          });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void mergeWith_shouldMergeIfOtherValueIsAbsentButFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> firstOptional = Optional.optional(anyValue);
        Optional<?> secondOptional = Optional.empty();

        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                          first -> expectedResult,
                                                          (o1, o2) -> {
                                                              Assert.fail();
                                                              return null;
                                                          });

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void mergeWith_shouldMergeIfOtherValueIsAbsentButBothFallbacksAreProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> firstOptional = Optional.optional(anyValue);
        Optional<?> secondOptional = Optional.empty();

        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                          first -> expectedResult,
                                                          second -> {
                                                              Assert.fail();
                                                              return null;
                                                          },
                                                          (o1, o2) -> {
                                                              Assert.fail();
                                                              return null;
                                                          });

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void mergeWith_shouldMergeIfFirstValueIsAbsentButBothFallbacksAreProvided() {
        // given:
        Optional<?> firstOptional = Optional.empty();
        Object anyValue = new Object();
        Optional<?> secondOptional = Optional.optional(anyValue);

        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                          first -> {
                                                              Assert.fail();
                                                              return null;
                                                          },
                                                          second -> expectedResult,
                                                          (o1, o2) -> {
                                                              Assert.fail();
                                                              return null;
                                                          });

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void mergeWith_shouldMergeIfOtherValueIsPresentAndFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> firstOptional = Optional.optional(anyValue);
        Object anyOtherValue = new Object();
        Optional<?> secondOptional = Optional.optional(anyOtherValue);

        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                          first -> {
                                                              Assert.fail();
                                                              return null;
                                                          },
                                                          (first, second) -> expectedResult);

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void mergeWith_shouldNotMergeIfSecondValueIsAbsentAndNoFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> firstOptional = Optional.optional(anyValue);
        Optional<?> secondOptional = Optional.empty();

        // when:
        Optional<?> mergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                          (o1, o2) -> {
                                                              Assert.fail();
                                                              return null;
                                                          });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void mergeWith_shouldMergeUsingProvidedFunctionIfBothValuesArePresent() {
        // given:
        Object anyValue = new Object();
        Optional<?> firstOptional = Optional.optional(anyValue);
        Object anyOtherValue = new Object();
        Optional<?> secondOptional = Optional.optional(anyOtherValue);

        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                          (first, second) -> {
                                                              assertSame(anyValue, first);
                                                              assertSame(anyOtherValue, second);
                                                              return expectedResult;
                                                          });

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void mergeWith_shouldNotMergeIfBothFallbacksAreProvidedButBothValuesAreAbsent() {
        // given:
        Optional<?> firstOptional = Optional.empty();
        Optional<?> secondOptional = Optional.empty();

        // when:
        Optional<?> mergeResult = firstOptional.mergeWith(secondOptional, // TODO: for some reason doesn't work without wildcard (?)
                                                          first -> {
                                                              Assert.fail();
                                                              return null;
                                                          },
                                                          second -> {
                                                              Assert.fail();
                                                              return null;
                                                          },
                                                          (first, second) -> {
                                                              Assert.fail();
                                                              return null;
                                                          });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void explodeAndMerge_shouldNotMergeIfOptionalIsEmpty() {
        // given:
        Optional<?> optional = Optional.empty();

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> { // TODO: for some reason doesn't work without wildcard (?)
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           second -> {
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void explodeAndMerge_shouldNotMergeIfExplodedComponentsAreAbsentAndNoFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> null, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> null,
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void explodeAndMerge_shouldNotMergeIfFirstExplodedComponentIsAbsentAndNoFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> null, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> new Object(),
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void explodeAndMerge_shouldNotMergeIfSecondExplodedComponentIsAbsentAndNoFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> new Object(), // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> null,
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void explodeAndMerge_shouldMergeIfBothExplodedComponentsArePresentsEvenIfNoFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        Object firstComponent = new Object();
        Object secondComponent = new Object();
        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> firstComponent, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> secondComponent,
                                                           (first, second) -> expectedResult);

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void explodeAndMerge_shouldNotMergeIfFirstExplodedComponentIsAbsentAndOneFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> null, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> new Object(),
                                                           firstFallback -> {
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void explodeAndMerge_shouldMergeIfFirstExplodedComponentIsPresentAndSecondExplodedComponentIsAbsentAndOneFallbackIsProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        Object firstComponent = new Object();
        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> firstComponent, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> null,
                                                           firstFallback -> expectedResult,
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void explodeAndMerge_shouldNotMergeIfBothExplodedComponentsAreAbsentAndTwoFallbacksAreProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> null, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> null,
                                                           firstFallback -> {
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           secondFallback -> {
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertFalse(mergeResult.isPresent());
    }

    @Test
    public void explodeAndMerge_shouldMergeIfFirstExplodedComponentsIsPresentAndSecondExplodedComponentIsAbsentAndTwoFallbacksAreProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        Object firstComponent = new Object();
        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> firstComponent, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> null,
                                                           firstFallback -> expectedResult,
                                                           secondFallback -> {
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void explodeAndMerge_shouldMergeIfFirstExplodedComponentsIsAbsentAndSecondExplodedComponentIsPresentAndTwoFallbacksAreProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        Object secondComponent = new Object();
        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> null, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> secondComponent,
                                                           firstFallback -> {
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           secondFallback -> expectedResult,
                                                           (first, second) -> {
                                                               Assert.fail();
                                                               return null;
                                                           });

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void explodeAndMerge_shouldMergeIfBothExplodedComponentsArePresentsAndTwoFallbacksAreProvided() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        Object firstComponent = new Object();
        Object secondComponent = new Object();
        Object expectedResult = new Object();

        // when:
        Optional<?> mergeResult = optional.explodeAndMerge(first -> firstComponent, // TODO: for some reason doesn't work without wildcard (?)
                                                           second -> secondComponent,
                                                           firstFallback -> {
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           secondFallback -> {
                                                               Assert.fail();
                                                               return null;
                                                           },
                                                           (first, second) -> expectedResult);

        // then:
        assertTrue(mergeResult.isPresent());
        assertSame(expectedResult, mergeResult.get());
    }

    @Test
    public void or_shouldAddValueIfWasAbsent() {
        //given:
        Optional<Object> optional = Optional.empty(); // TODO: for some reason doesn't work with wildcard (?)
        Object anyValue = new Object();

        // when:
        Optional<?> result = optional.or(anyValue);

        // then:
        assertTrue(result.isPresent());
        assertSame(anyValue, result.get());
    }

    @Test
    public void or_shouldNotAddValueIfWasPresent() {
        //given:
        Object anyValue = new Object();
        Optional<Object> optional = Optional.optional(anyValue); // TODO: for some reason doesn't work with wildcard (?)
        Object anyOtherValue = new Object();

        // when:
        Optional<?> result = optional.or(anyOtherValue);

        // then:
        assertTrue(result.isPresent());
        assertSame(anyValue, result.get());
        assertNotSame(anyOtherValue, result.get());
    }

    @Test
    public void orGet_shouldAddValueIfWasAbsent() {
        //given:
        Optional<Object> optional = Optional.empty(); // TODO: for some reason doesn't work with wildcard (?)
        Object anyValue = new Object();

        // when:
        Optional<?> result = optional.orGet(() -> anyValue);

        // then:
        assertTrue(result.isPresent());
        assertSame(anyValue, result.get());
    }

    @Test
    public void orGet_shouldNotAddValueIfWasPresent() {
        //given:
        Object anyValue = new Object();
        Optional<Object> optional = Optional.optional(anyValue); // TODO: for some reason doesn't work with wildcard (?)
        Object anyOtherValue = new Object();

        // when:
        Optional<?> result = optional.orGet(() -> anyOtherValue);

        // then:
        assertTrue(result.isPresent());
        assertSame(anyValue, result.get());
        assertNotSame(anyOtherValue, result.get());
    }

    @Test(expected = RuntimeException.class)
    public void orThrow_shouldThrowIfValueIsAbsent() {
        // given:
        Optional<?> optional = Optional.empty();

        // expect:
        optional.orThrow(RuntimeException::new);
    }

    @Test(expected = NullPointerException.class)
    public void orThrow_shouldThrowIfExceptionIsNull() {
        // given:
        Optional<?> optional = Optional.empty();

        // expect:
        optional.orThrow(() -> (RuntimeException) null);
    }

    @Test
    public void orThrow_shouldNotThrowIfValueIsPresent() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Optional<?> result = optional.orThrow(RuntimeException::new);

        // then:
        assertSame(optional, result);
    }

    @Test
    public void orElseNull_shouldReturnNullIfEmpty() {
        // given:
        Optional<?> optional = Optional.empty();

        // when:
        Object result = optional.orElseNull();

        // then:
        assertNull(result);
    }

    @Test
    public void orElseNull_shouldReturnValueIfPresent() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        Object result = optional.orElseNull();

        // then:
        assertSame(anyValue, result);
    }

    @Test
    public void orElse_shouldReturnAlternativeValueIfEmpty() {
        // given:
        Object anyOtherValue = new Object();
        Optional<Object> optional = Optional.empty(); // TODO: for some reason doesn't work with wildcard (?)

        // when:
        Object result = optional.orElse(anyOtherValue);

        // then:
        assertSame(anyOtherValue, result);
    }

    @Test
    public void orElse_shouldReturnValueIfPresent() {
        // given:
        Object anyValue = new Object();
        Optional<Object> optional = Optional.optional(anyValue); // TODO: for some reason doesn't work with wildcard (?)
        Object anyOtherValue = new Object();

        // when:
        Object result = optional.orElse(anyOtherValue);

        // then:
        assertSame(anyValue, result);
    }

    @Test
    public void orElseGet_shouldReturnAlternativeValueIfEmpty() {
        // given:
        Object anyOtherValue = new Object();
        Optional<Object> optional = Optional.empty(); // TODO: for some reason doesn't work with wildcard (?)

        // when:
        Object result = optional.orElseGet(() -> anyOtherValue);

        // then:
        assertSame(anyOtherValue, result);
    }

    @Test
    public void orElseGet_shouldReturnValueIfPresent() {
        // given:
        Object anyValue = new Object();
        Optional<Object> optional = Optional.optional(anyValue); // TODO: for some reason doesn't work with wildcard (?)
        Object anyOtherValue = new Object();

        // when:
        Object result = optional.orElseGet(() -> anyOtherValue);

        // then:
        assertSame(anyValue, result);
    }

    @Test(expected = RuntimeException.class)
    public void orElseThrow_shouldThrowIfValueIsAbsent() {
        // given:
        Optional<?> optional = Optional.empty();

        // expect:
        optional.orElseThrow(RuntimeException::new);
    }

    @Test
    public void orElseThrow_shouldNotThrowIfValueIsPresent() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // expect:
        assertSame(anyValue, optional.orElseThrow(RuntimeException::new));
    }

    @Test(expected = NullPointerException.class)
    public void orElseThrow_shouldThrowIfExceptionIsNull() {
        // given:
        Optional<?> optional = Optional.empty();

        // expect:
        optional.orElseThrow(() -> (RuntimeException) null);
    }

    @Test
    public void toJavaOptional_shouldReturnEmptyOptionalIfValueIsAbsent() {
        // given:
        Optional<?> optional = Optional.empty();

        // when:
        final java.util.Optional<?> javaOptional = optional.toJavaOptional();

        // then:
        assertFalse(javaOptional.isPresent());
    }

    @Test
    public void toJavaOptional_shouldReturnNonEmptyOptionalIfValueIsPresent() {
        // given:
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // when:
        final java.util.Optional<?> javaOptional = optional.toJavaOptional();

        // then:
        assertTrue(javaOptional.isPresent());
        assertSame(anyValue, javaOptional.get());
    }

    @Test
    public void iterator_shouldNotIterateIfOptionalIsEmpty() {
        // given:
        Optional<?> optional = Optional.empty();

        // expect:
        for (Object ignored : optional) {
            Assert.fail();
        }
    }

    @Test
    public void iterator_shouldIterateOverSingleElementIfOptionalIsFull() {
        // given:
        int iterations = 0;
        Object anyValue = new Object();
        Optional<?> optional = Optional.optional(anyValue);

        // expect:
        for (Object object : optional) {
            ++iterations;
            assertSame(anyValue, object);
        }

        assertEquals(1, iterations);
    }

}