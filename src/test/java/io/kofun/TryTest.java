package io.kofun;

import io.kofun.exception.ErrorNotPresentException;
import io.kofun.exception.PredicateNotMatchingException;
import io.kofun.prototypes.TryPrototype;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.kofun.ExtensibleFluentChainTestUtil.prototypeImplementation;
import static io.kofun.ExtensibleFluentChainTestUtil.shouldReimplementAllExtensibleFluentChainMethods;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TryTest {

    @Test
    public void shouldConformToExtensibleFluentChainContract() {
        shouldReimplementAllExtensibleFluentChainMethods(prototypeImplementation(TryPrototype.class, Try.class));
    }

    @Test
    public void trySupplier_shouldCreateSuccessIfSupplierDoesNotThrow() {
        // given:
        Object anyValue = new Object();

        // when:
        final Try<Object> result = Try.trySupplier(() -> anyValue);

        // then:
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void trySupplier_shouldCreateErrorIfSupplierThrows() {
        // given:
        RuntimeException error = new RuntimeException();

        // when:
        final Try<Object> result = Try.trySupplier(() -> {throw error;});

        // then:
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void tryCallable_shouldCreateSuccessIfCallableDoesNotThrow() {
        // given:
        Object anyValue = new Object();

        // when:
        final Try<Object> result = Try.tryCallable(() -> anyValue);

        // then:
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void tryCallable_shouldCreateErrorIfCallableThrows() {
        // given:
        RuntimeException error = new RuntimeException();

        // when:
        final Try<Object> result = Try.tryCallable(() -> {throw error;});

        // then:
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void tryRunnable_shouldCreateSuccessIfRunnableDoesNotThrow() {
        // when:
        final Try<Void> result = Try.tryRunnable(() -> {});

        // then:
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
    }

    @Test
    public void tryRunnable_shouldCreateErrorIfRunnableThrows() {
        // given:
        RuntimeException error = new RuntimeException();

        // when:
        final Try<Void> result = Try.tryRunnable(() -> {throw error;});

        // then:
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void tryRun_shouldCreateSuccessIfRunnableDoesNotThrow() {
        // when:
        final Try<Void> result = Try.tryRun(() -> {});

        // then:
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        Assert.assertNull(result.getSuccess());
    }

    @Test
    public void tryRun_shouldCreateErrorIfRunnableThrows() {
        // given:
        RuntimeException error = new RuntimeException();

        // when:
        final Try<Void> result = Try.tryRun(() -> {throw error;});

        // then:
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void tryOf_shouldCreateSuccessIfSupplierDoesNotThrow() {
        // given:
        Object anyValue = new Object();

        // when:
        final Try<Object> result = Try.tryOf(() -> anyValue);

        // then:
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void tryOf_shouldCreateErrorIfSupplierThrows() {
        // given:
        Throwable error = new Throwable();

        // when:
        final Try<Object> result = Try.tryOf(() -> {throw error;});

        // then:
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void success_shouldCreateASuccessTry() {
        // given:
        Object anyValue = new Object();

        // when:
        final Try<Object> result = Try.success(anyValue);

        // then:
        assertTrue(result.isSuccess());
        assertFalse(result.isError());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void error_shouldCreateAnErrorTry() {
        // given:
        Throwable error = new Throwable();

        // when:
        final Try<Object> result = Try.error(error);

        // then:
        assertFalse(result.isSuccess());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test(expected = Error.class)
    public void error_shouldThrowIfErrorIsInstanceOfJavaUtilError() {
        // given:
        Error error = new Error();

        // expect:
        Try.error(error);
    }

    @Test
    public void onSuccess_shouldBeCalledForASuccessTry() {
        // given:
        AtomicBoolean executedOnSuccess = new AtomicBoolean(false);
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        successTry.onSuccess(s -> {
            executedOnSuccess.set(true);
            assertSame(anyValue, s);
        });

        // then:
        assertTrue(executedOnSuccess.get());
    }

    @Test
    public void onSuccess_shouldNotBeCalledForAnErrorTry() {
        // given:
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // then:
        errorTry.onSuccess(s -> fail());
    }

    @Test(expected = TestSuccessException.class)
    public void onSuccess_shouldThrowForSuccessTryIfConsumerThrows() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // then:
        successTry.onSuccess(TestSuccessException::throwIt);
    }

    @Test
    public void onSuccess_shouldNotThrowForErrorTryIfConsumerThrows() {
        // given:
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // then:
        errorTry.onSuccess(TestSuccessException::throwIt);
    }

    @Test
    public void onSuccessTryConsumer_shouldNotThrowForSuccessTryIfConsumerThrows() {
        // given:
        AtomicBoolean wasOnSuccessTryConsumerExecuted = new AtomicBoolean(false);
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        RuntimeException error = new RuntimeException();

        // when:
        Try<Object> result = successTry.onSuccessTryConsumer(success -> {
            assertSame(anyValue, success);
            wasOnSuccessTryConsumerExecuted.set(true);
            throw error;
        });

        // then:
        assertTrue(wasOnSuccessTryConsumerExecuted.get());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void onSuccessTryRunnable_shouldNotThrowForSuccessTryIfRunnableThrows() {
        // given:
        AtomicBoolean wasOnSuccessTryRunnableExecuted = new AtomicBoolean(false);
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        RuntimeException error = new RuntimeException();

        // when:
        Try<Object> result = successTry.onSuccessTryRunnable(() -> {
            wasOnSuccessTryRunnableExecuted.set(true);
            throw error;
        });

        // then:
        assertTrue(wasOnSuccessTryRunnableExecuted.get());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void onSuccessTryRun_shouldNotThrowForSuccessTryIfCheckedRunnableThrows() {
        // given:
        AtomicBoolean wasOnSuccessTryRunExecuted = new AtomicBoolean(false);
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable error = new Throwable();

        // when:
        Try<Object> result = successTry.onSuccessTryRun(() -> {
            wasOnSuccessTryRunExecuted.set(true);
            throw error;
        });

        // then:
        assertTrue(wasOnSuccessTryRunExecuted.get());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void onSuccessTry_shouldNotThrowForSuccessTryIfCheckedConsumerThrows() {
        // given:
        AtomicBoolean wasOnSuccessTryExecuted = new AtomicBoolean(false);
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable error = new Throwable();

        // when:
        Try<Object> result = successTry.onSuccessTry(success -> {
            assertSame(anyValue, success);
            wasOnSuccessTryExecuted.set(true);
            throw error;
        });

        // then:
        assertTrue(wasOnSuccessTryExecuted.get());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void onSuccessTry_tryShouldNotBeAffectedIfConsumerDoesNotThrow() {
        // given:
        AtomicBoolean wasOnSuccessTryExecuted = new AtomicBoolean(false);
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.onSuccessTry(success -> {
            assertSame(anyValue, success);
            wasOnSuccessTryExecuted.set(true);
        });

        // then:
        assertTrue(wasOnSuccessTryExecuted.get());
        assertTrue(result.isSuccess());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void onSuccessTry_shouldNotThrowOrAffectTryForErrorTryIfCheckedConsumerThrows() {
        // given:
        AtomicBoolean wasOnSuccessTryExecuted = new AtomicBoolean(false);
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        Throwable consumerError = new Throwable();

        // when:
        Try<Object> result = errorTry.onSuccessTry(success -> {
            wasOnSuccessTryExecuted.set(true);
            throw consumerError;
        });

        // then:
        assertFalse(wasOnSuccessTryExecuted.get());
        assertTrue(result.isError());
        assertSame(error, result.getError());
    }

    @Test
    public void onError_shouldNotBeCalledForASuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // then:
        successTry.onError(e -> fail());
    }

    @Test
    public void onError_shouldBeCalledForAnErrorTry() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onError(e -> {
            executedOnError.set(true);
            assertSame(error, e);
        });

        // then:
        assertTrue(executedOnError.get());
    }

    @Test(expected = TestSuccessException.class)
    public void onError_shouldThrowForErrorTryIfConsumerThrows() {
        // given:
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // then:
        errorTry.onError(TestSuccessException::throwIt);
    }

    @Test
    public void onError_shouldNotThrowForSuccessTryIfConsumerThrows() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // then:
        successTry.onError(TestSuccessException::throwIt);
    }

    @Test
    public void onErrorTryConsumer_shouldNotThrowForErrorTryIfConsumerThrows() {
        // given:
        AtomicBoolean wasOnErrorTryConsumerExecuted = new AtomicBoolean(false);
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        RuntimeException errorThrown = new RuntimeException();

        // when:
        Try<Object> result = errorTry.onErrorTryConsumer(error -> {
            assertSame(error, anyError);
            wasOnErrorTryConsumerExecuted.set(true);
            throw errorThrown;
        });

        // then:
        assertTrue(wasOnErrorTryConsumerExecuted.get());
        assertTrue(result.isError());
        assertSame(errorThrown, result.getError());
    }

    @Test
    public void onErrorTryRunnable_shouldNotThrowForErrorTryIfRunnableThrows() {
        // given:
        AtomicBoolean wasOnErrorTryConsumerExecuted = new AtomicBoolean(false);
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        RuntimeException errorThrown = new RuntimeException();

        // when:
        Try<Object> result = errorTry.onErrorTryRunnable(() -> {
            wasOnErrorTryConsumerExecuted.set(true);
            throw errorThrown;
        });

        // then:
        assertTrue(wasOnErrorTryConsumerExecuted.get());
        assertTrue(result.isError());
        assertSame(errorThrown, result.getError());
    }

    @Test
    public void onErrorTryRun_shouldNotThrowForErrorTryIfCheckedRunnableThrows() {
        // given:
        AtomicBoolean wasOnErrorTryConsumerExecuted = new AtomicBoolean(false);
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        Throwable errorThrown = new Throwable();

        // when:
        Try<Object> result = errorTry.onErrorTryRun(() -> {
            wasOnErrorTryConsumerExecuted.set(true);
            throw errorThrown;
        });

        // then:
        assertTrue(wasOnErrorTryConsumerExecuted.get());
        assertTrue(result.isError());
        assertSame(errorThrown, result.getError());
    }

    @Test
    public void onErrorTry_shouldNotThrowForErrorTryIfCheckedConsumerThrows() {
        // given:
        AtomicBoolean wasOnErrorTryConsumerExecuted = new AtomicBoolean(false);
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        Throwable errorThrown = new Throwable();

        // when:
        Try<Object> result = errorTry.onErrorTry(error -> {
            assertSame(anyError, error);
            wasOnErrorTryConsumerExecuted.set(true);
            throw errorThrown;
        });

        // then:
        assertTrue(wasOnErrorTryConsumerExecuted.get());
        assertTrue(result.isError());
        assertSame(errorThrown, result.getError());
    }

    @Test
    public void onErrorTry_tryShouldNotBeAffectedIfConsumerDoesNotThrow() {
        // given:
        AtomicBoolean wasOnErrorTryConsumerExecuted = new AtomicBoolean(false);
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        Try<Object> result = errorTry.onErrorTry(error -> {
            assertSame(anyError, error);
            wasOnErrorTryConsumerExecuted.set(true);
        });

        // then:
        assertTrue(wasOnErrorTryConsumerExecuted.get());
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void onErrorTry_shouldNotThrowOrAffectTryForSuccessTryIfCheckedConsumerThrows() {
        // given:
        AtomicBoolean wasOnErrorTryConsumerExecuted = new AtomicBoolean(false);
        Object anyValue = new Object();
        Try<Object> errorTry = Try.success(anyValue);

        Throwable errorThrown = new Throwable();

        // when:
        Try<Object> result = errorTry.onErrorTry(error -> {
            wasOnErrorTryConsumerExecuted.set(true);
            throw errorThrown;
        });

        // then:
        assertFalse(wasOnErrorTryConsumerExecuted.get());
        assertTrue(result.isSuccess());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void onError_shouldBeCalledForTheSameExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onError(IllegalArgumentException.class, e -> {
            executedOnError.set(true);
            assertSame(error, e);
        });

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onError_shouldBeCalledForParentExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onError(Throwable.class, e -> {
            executedOnError.set(true);
            assertSame(error, e);
        });

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onError_shouldNotBeCalledForChildExceptionClass() {
        // given:
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // then:
        errorTry.onError(IllegalArgumentException.class, e -> fail());
    }

    @Test
    public void onErrorTryConsumer_shouldBeCalledForTheSameExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onErrorTryConsumer(IllegalArgumentException.class, e -> {
            executedOnError.set(true);
            assertSame(error, e);
        });

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onErrorTryConsumer_shouldBeCalledForParentExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onErrorTryConsumer(Throwable.class, e -> {
            executedOnError.set(true);
            assertSame(error, e);
        });

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onErrorTryConsumer_shouldNotBeCalledForChildExceptionClass() {
        // given:
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // then:
        errorTry.onErrorTryConsumer(IllegalArgumentException.class, e -> fail());
    }

    @Test
    public void onErrorTryRunnable_shouldBeCalledForTheSameExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onErrorTryRunnable(IllegalArgumentException.class, () -> executedOnError.set(true));

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onErrorTryRunnable_shouldBeCalledForParentExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onErrorTryRunnable(Throwable.class, () -> executedOnError.set(true));

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onErrorTryRunnable_shouldNotBeCalledForChildExceptionClass() {
        // given:
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // then:
        errorTry.onErrorTryRunnable(IllegalArgumentException.class, Assert::fail);
    }

    @Test
    public void onErrorTryRun_shouldBeCalledForTheSameExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onErrorTryRun(IllegalArgumentException.class, () -> executedOnError.set(true));

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onErrorTryRun_shouldBeCalledForParentExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onErrorTryRun(Throwable.class, () -> executedOnError.set(true));

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onErrorTryRun_shouldNotBeCalledForChildExceptionClass() {
        // given:
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // then:
        errorTry.onErrorTryRun(IllegalArgumentException.class, Assert::fail);
    }

    @Test
    public void onErrorTry_shouldBeCalledForTheSameExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onErrorTry(IllegalArgumentException.class, e -> {
            executedOnError.set(true);
            assertSame(error, e);
        });

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onErrorTry_shouldBeCalledForParentExceptionClass() {
        // given:
        AtomicBoolean executedOnError = new AtomicBoolean(false);
        Throwable error = new IllegalArgumentException();
        Try<Object> errorTry = Try.error(error);

        // when:
        errorTry.onErrorTry(Throwable.class, e -> {
            executedOnError.set(true);
            assertSame(error, e);
        });

        // then:
        assertTrue(executedOnError.get());
    }

    @Test
    public void onErrorTry_shouldNotBeCalledForChildExceptionClass() {
        // given:
        Throwable error = new Throwable();
        Try<Object> errorTry = Try.error(error);

        // then:
        errorTry.onErrorTry(IllegalArgumentException.class, e -> fail());
    }

    @Test
    public void collect_shouldReturnANewListForSuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        List<Object> result = successTry.collect(toList());

        // then:
        assertEquals(1, result.size());
        assertSame(anyValue, result.get(0));
    }

    @Test
    public void collect_shouldReturnANewEmptyListForErrorTry() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        List<Object> result = errorTry.collect(toList());

        // then:
        assertEquals(0, result.size());
    }

    @Test
    public void collect_shouldAllowCreationOfMutableCollectionForSuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        List<Object> result = successTry.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // then:
        assertEquals(1, result.size());
        assertSame(anyValue, result.get(0));
    }

    @Test
    public void collect_shouldNotAllowCreationOfMutableCollectionForErrorTry() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        List<Object> result = errorTry.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        // then:
        assertEquals(0, result.size());
    }

    @Test
    public void iterator_shouldAllowIterationOverSuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);
        int iterations = 0;

        // when:
        for (Object object : successTry) {
            ++iterations;
            assertSame(object, anyValue);
        }

        // then:
        assertEquals(1, iterations);
    }

    @Test
    public void iterator_shouldNowAllowIterationOverErrorTry() {
        // given:
        Try<Object> errorTry = Try.error(new Throwable());

        // when:
        for (Object ignored : errorTry) {
            fail();
        }
    }

    @Test
    public void switchWithError_shouldCreateANewSuccessTryFromAnErrorTry() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        final Try<Throwable> result = errorTry.switchWithError();

        // then:
        assertTrue(result.isSuccess());
        assertSame(anyError, result.getSuccess());
    }

    @Test
    public void switchWithError_shouldCreateANewErrorTryFromASuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        final Try<Throwable> result = successTry.switchWithError();

        // then:
        assertTrue(result.isError());
        assertTrue(result.getError() instanceof ErrorNotPresentException);
    }

    @Test
    public void filter_shouldReturnTheSameTryIfTryIsSuccessAndPredicateMatches() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.filter(anyValue::equals);

        // then:
        assertSame(successTry, result);
    }

    @Test
    public void filter_shouldReturnErrorTryIfTryIsSuccessAndPredicateDoesNotMatch() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.filter(Objects::isNull);

        // then:
        assertNotSame(result, successTry);
        assertTrue(result.isError());
        assertTrue(result.getError() instanceof PredicateNotMatchingException);
    }

    @Test
    public void filter_shouldReturnTheSameTryIfTryIsError() {
        // given:
        Try<Object> errorTry = Try.error(new Throwable());

        // when:
        Try<Object> result = errorTry.filter(Objects::nonNull);

        // then:
        assertSame(errorTry, result);
    }

    @Test
    public void filterGet_shouldReturnTheSameTryIfTryIsSuccessAndPredicateMatches() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.filterGet(anyValue::equals, Throwable::new);

        // then:
        assertSame(successTry, result);
    }

    @Test
    public void filterGet_shouldReturnANewErrorTryWithProvidedExceptionIfPredicateDoesNotMatch() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = successTry.filterGet(Objects::isNull, () -> anyError);

        // then:
        assertNotSame(result, successTry);
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterGet_shouldReturnTheSameTryIfTryIsError() {
        // given:
        final Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        Try<Object> result = errorTry.filterGet(Objects::nonNull, Throwable::new);

        // then:
        assertSame(errorTry, result);
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterMap_shouldReturnTheSameTryIfTryIsSuccessAndPredicateMatches() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.filterMap(anyValue::equals, success -> new Throwable());

        // then:
        assertSame(successTry, result);
    }

    @Test
    public void filterMap_shouldReturnANewErrorTryWithProvidedExceptionIfPredicateDoesNotMatch() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = successTry.filterMap(Objects::isNull, success -> {
            assertSame(anyValue, success);
            return anyError;
        });

        // then:
        assertNotSame(result, successTry);
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterMap_shouldReturnTheSameTryIfTryIsError() {
        // given:
        final Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        Try<Object> result = errorTry.filterMap(Objects::nonNull, success -> new Throwable());

        // then:
        assertSame(errorTry, result);
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTry_shouldReturnTheSameTryIfTryIsSuccessAndPredicateMatches() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.filterTry(anyValue::equals);

        // then:
        assertSame(successTry, result);
    }

    @Test
    public void filterTry_shouldReturnErrorTryIfTryIsSuccessAndPredicateDoesNotMatch() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.filterTry(Objects::isNull);

        // then:
        assertNotSame(result, successTry);
        assertTrue(result.isError());
        assertTrue(result.getError() instanceof PredicateNotMatchingException);
    }

    @Test
    public void filterTry_shouldReturnTheSameTryIfTryIsError() {
        // given:
        Try<Object> errorTry = Try.error(new Throwable());

        // when:
        Try<Object> result = errorTry.filterTry(Objects::nonNull);

        // then:
        assertSame(errorTry, result);
    }

    @Test
    public void filterTry_shouldCatchExceptionThrownByPredicateForSuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> errorTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = errorTry.filterTry(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTryGet_shouldReturnTheSameTryIfTryIsSuccessAndPredicateMatches() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.filterTryGet(anyValue::equals, Throwable::new);

        // then:
        assertSame(successTry, result);
    }

    @Test
    public void filterTryGet_shouldReturnANewErrorTryWithProvidedExceptionIfPredicateDoesNotMatch() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = successTry.filterTryGet(Objects::isNull, () -> anyError);

        // then:
        assertNotSame(result, successTry);
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTryGet_shouldReturnTheSameTryIfTryIsError() {
        // given:
        final Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        Try<Object> result = errorTry.filterTryGet(Objects::nonNull, Throwable::new);

        // then:
        assertSame(errorTry, result);
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTryGet_shouldCatchExceptionThrownByPredicateForSuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> errorTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = errorTry.filterTryGet(ignore -> {throw anyError;}, RuntimeException::new);

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTryGet_shouldCatchExceptionThrownBySupplierForSuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> errorTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = errorTry.filterTryGet(ignore -> false, () -> {throw anyError;});

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTryMap_shouldReturnTheSameTryIfTryIsSuccessAndPredicateMatches() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.filterTryMap(anyValue::equals, success -> new Throwable());

        // then:
        assertSame(successTry, result);
    }

    @Test
    public void filterTryMap_shouldReturnANewErrorTryWithProvidedExceptionIfPredicateDoesNotMatch() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = successTry.filterTryMap(Objects::isNull, success -> {
            assertSame(anyValue, success);
            return anyError;
        });

        // then:
        assertNotSame(result, successTry);
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTryMap_shouldReturnTheSameTryIfTryIsError() {
        // given:
        final Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        Try<Object> result = errorTry.filterTryMap(Objects::nonNull, ignore -> new Throwable());

        // then:
        assertSame(errorTry, result);
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTryMap_shouldCatchExceptionThrownByPredicateForSuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = successTry.filterTryMap(ignore -> {throw anyError;}, ignore -> new Throwable());

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void filterTryMap_shouldCatchExceptionThrownBySupplierForSuccessTry() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable anyError = new Throwable();

        // when:
        Try<Object> result = successTry.filterTryMap(ignore -> false, ignore -> {throw anyError;});

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test(expected = TestSuccessException.class)
    public void map_shouldThrowIfMapperThrowsAndWasSuccessBefore() {
        // given:
        Try<Object> successTry = Try.success(new Object());

        TestSuccessException anyError = new TestSuccessException();

        // expect:
        successTry.map(ignore -> {throw anyError;});
    }

    @Test
    public void map_shouldNotThrowIfMapperThrowsButWasErrorBefore() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        RuntimeException thrownError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.map(ignore -> {throw thrownError;});

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void map_shouldMapToNewSuccessIfWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Object newValue = new Object();

        // when:
        Try<Object> result = successTry.map(ignore -> newValue);

        // then:
        assertTrue(result.isSuccess());
        assertSame(newValue, result.getSuccess());
    }

    @Test
    public void map_shouldReturnItselfIfWasErrorBefore() {
        // given:
        TestSuccessException anyError = new TestSuccessException();
        Try<Object> errorTry = Try.error(anyError);

        Object newValue = new Object();

        // when:
        Try<Object> result = errorTry.map(ignore -> newValue);

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), anyError);
    }

    @Test(expected = TestSuccessException.class)
    public void mapError_shouldThrowIfMapperThrowsAndWasErrorBefore() {
        // given:
        Try<Object> errorTry = Try.error(new Throwable());

        TestSuccessException anyError = new TestSuccessException();

        // expect:
        errorTry.mapError(ignore -> {throw anyError;});
    }

    @Test
    public void mapError_shouldNotThrowIfMapperThrowsButWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        TestSuccessException anyError = new TestSuccessException();

        // when:
        Try<Object> result = successTry.mapError(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isSuccess());
        assertSame(result.getSuccess(), anyValue);
    }

    @Test
    public void mapError_shouldMapToNewErrorIfWasErrorBefore() {
        // given:
        Throwable anyError = new RuntimeException();
        Try<Object> errorTry = Try.error(anyError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.mapError(ignore -> newError);

        // then:
        assertTrue(result.isError());
        assertSame(newError, result.getError());
    }

    @Test
    public void mapError_shouldReturnItselfIfWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = successTry.mapError(ignore -> newError);

        // then:
        assertTrue(result.isSuccess());
        assertSame(result.getSuccess(), anyValue);
    }

    @Test
    public void mapError_shouldMapIfErrorClassMatches() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.mapError(Throwable.class, ignore -> newError);

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), newError);
    }

    @Test
    public void mapError_shouldNotMapIfErrorClassDoesNotMatch() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.mapError(AssertionError.class, ignore -> newError);

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), givenError);
    }

    @Test
    public void mapTry_shouldNotThrowIfMapperThrowsAndWasSuccessBefore() {
        // given:
        Try<Object> successTry = Try.success(new Object());

        TestSuccessException anyError = new TestSuccessException();

        // when:
        Try<Object> result = successTry.mapTry(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), anyError);
    }

    @Test
    public void mapTry_shouldNotThrowIfMapperThrowsButWasErrorBefore() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        RuntimeException thrownError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.mapTry(ignore -> {throw thrownError;});

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void mapTry_shouldMapToNewSuccessIfWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Object newValue = new Object();

        // when:
        Try<Object> result = successTry.mapTry(ignore -> newValue);

        // then:
        assertTrue(result.isSuccess());
        assertSame(newValue, result.getSuccess());
    }

    @Test
    public void mapTry_shouldReturnItselfIfWasErrorBefore() {
        // given:
        TestSuccessException anyError = new TestSuccessException();
        Try<Object> errorTry = Try.error(anyError);

        Object newValue = new Object();

        // when:
        Try<Object> result = errorTry.mapTry(ignore -> newValue);

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), anyError);
    }

    @Test
    public void mapTryError_shouldNotThrowIfMapperThrowsAndWasErrorBefore() {
        // given:
        Try<Object> errorTry = Try.error(new Throwable());

        TestSuccessException anyError = new TestSuccessException();

        // when:
        Try<Object> result = errorTry.mapTryError(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), anyError);
    }

    @Test
    public void mapTryError_shouldNotThrowIfMapperThrowsButWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        TestSuccessException anyError = new TestSuccessException();

        // when:
        Try<Object> result = successTry.mapTryError(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isSuccess());
        assertSame(result.getSuccess(), anyValue);
    }

    @Test
    public void mapTryError_shouldMapToNewErrorIfWasErrorBefore() {
        // given:
        Throwable anyError = new RuntimeException();
        Try<Object> errorTry = Try.error(anyError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.mapTryError(ignore -> newError);

        // then:
        assertTrue(result.isError());
        assertSame(newError, result.getError());
    }

    @Test
    public void mapTryError_shouldReturnItselfIfWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = successTry.mapTryError(ignore -> newError);

        // then:
        assertTrue(result.isSuccess());
        assertSame(result.getSuccess(), anyValue);
    }

    @Test
    public void mapTryError_shouldMapIfErrorClassMatches() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.mapTryError(Throwable.class, ignore -> newError);

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), newError);
    }

    @Test
    public void mapTryError_shouldNotMapIfErrorClassDoesNotMatch() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.mapTryError(AssertionError.class, ignore -> newError);

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), givenError);
    }

    // TODO: remove separator ==================

    @Test(expected = TestSuccessException.class)
    public void flatMap_shouldThrowIfMapperThrowsAndWasSuccessBefore() {
        // given:
        Try<Object> successTry = Try.success(new Object());

        TestSuccessException anyError = new TestSuccessException();

        // expect:
        successTry.flatMap(ignore -> {throw anyError;});
    }

    @Test
    public void flatMap_shouldNotThrowIfMapperThrowsButWasErrorBefore() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        RuntimeException thrownError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.flatMap(ignore -> {throw thrownError;});

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void flatMap_shouldMapToNewSuccessIfWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Object newValue = new Object();

        // when:
        Try<Object> result = successTry.flatMap(ignore -> Try.success(newValue));

        // then:
        assertTrue(result.isSuccess());
        assertSame(newValue, result.getSuccess());
    }

    @Test
    public void flatMap_shouldReturnItselfIfWasErrorBefore() {
        // given:
        TestSuccessException anyError = new TestSuccessException();
        Try<Object> errorTry = Try.error(anyError);

        Object newValue = new Object();

        // when:
        Try<Object> result = errorTry.flatMap(ignore -> Try.success(newValue));

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), anyError);
    }

    @Test(expected = NullPointerException.class)
    public void flatMap_shouldThrowIfMappingResultIsNull() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // expect:
        successTry.flatMap(ignore -> null);
    }

    @Test(expected = TestSuccessException.class)
    public void flatMapError_shouldThrowIfMapperThrowsAndWasErrorBefore() {
        // given:
        Try<Object> errorTry = Try.error(new Throwable());

        TestSuccessException anyError = new TestSuccessException();

        // expect:
        errorTry.flatMapError(ignore -> {throw anyError;});
    }

    @Test
    public void flatMapError_shouldNotThrowIfMapperThrowsButWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        TestSuccessException anyError = new TestSuccessException();

        // when:
        Try<Object> result = successTry.flatMapError(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isSuccess());
        assertSame(result.getSuccess(), anyValue);
    }

    @Test
    public void flatMapError_shouldMapToNewErrorIfWasErrorBefore() {
        // given:
        Throwable anyError = new RuntimeException();
        Try<Object> errorTry = Try.error(anyError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.flatMapError(ignore -> Try.error(newError));

        // then:
        assertTrue(result.isError());
        assertSame(newError, result.getError());
    }

    @Test
    public void flatMapError_shouldReturnItselfIfWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = successTry.flatMapError(ignore -> Try.error(newError));

        // then:
        assertTrue(result.isSuccess());
        assertSame(result.getSuccess(), anyValue);
    }

    @Test
    public void flatMapError_shouldMapIfErrorClassMatches() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.flatMapError(Throwable.class, ignore -> Try.error(newError));

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), newError);
    }

    @Test
    public void flatMapError_shouldNotMapIfErrorClassDoesNotMatch() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.flatMapError(AssertionError.class, ignore -> Try.error(newError));

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), givenError);
    }

    @Test(expected = NullPointerException.class)
    public void flatMapError_shouldThrowIfMappingResultIsNull() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // expect:
        errorTry.flatMapError(ignore -> null);
    }

    @Test(expected = NullPointerException.class)
    public void flatMapError_shouldThrowIfErrorClassMatchesAndMappingResultIsNull() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // expect:
        errorTry.flatMapError(Throwable.class, ignore -> null);
    }

    @Test
    public void flatMapTry_shouldNotThrowIfMapperThrowsAndWasSuccessBefore() {
        // given:
        Try<Object> successTry = Try.success(new Object());

        TestSuccessException anyError = new TestSuccessException();

        // when:
        Try<Object> result = successTry.flatMapTry(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), anyError);
    }

    @Test
    public void flatMapTry_shouldNotThrowIfMapperThrowsButWasErrorBefore() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        RuntimeException thrownError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.flatMapTry(ignore -> {throw thrownError;});

        // then:
        assertTrue(result.isError());
        assertSame(anyError, result.getError());
    }

    @Test
    public void flatMapTry_shouldMapToNewSuccessIfWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Object newValue = new Object();

        // when:
        Try<Object> result = successTry.flatMapTry(ignore -> Try.success(newValue));

        // then:
        assertTrue(result.isSuccess());
        assertSame(newValue, result.getSuccess());
    }

    @Test
    public void flatMapTry_shouldReturnItselfIfWasErrorBefore() {
        // given:
        TestSuccessException anyError = new TestSuccessException();
        Try<Object> errorTry = Try.error(anyError);

        Object newValue = new Object();

        // when:
        Try<Object> result = errorTry.flatMapTry(ignore -> Try.success(newValue));

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), anyError);
    }

    @Test(expected = NullPointerException.class)
    public void flatMapTry_shouldThrowIfMappingResultIsNull() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // expect:
        successTry.flatMapTry(ignore -> null);
    }

    @Test
    public void flatMapTryError_shouldNotThrowIfMapperThrowsAndWasErrorBefore() {
        // given:
        Try<Object> errorTry = Try.error(new Throwable());

        TestSuccessException anyError = new TestSuccessException();

        // when:
        Try<Object> result = errorTry.flatMapTryError(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), anyError);
    }

    @Test
    public void flatMapTryError_shouldNotThrowIfMapperThrowsButWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        TestSuccessException anyError = new TestSuccessException();

        // when:
        Try<Object> result = successTry.flatMapTryError(ignore -> {throw anyError;});

        // then:
        assertTrue(result.isSuccess());
        assertSame(result.getSuccess(), anyValue);
    }

    @Test
    public void flatMapTryError_shouldMapToNewErrorIfWasErrorBefore() {
        // given:
        Throwable anyError = new RuntimeException();
        Try<Object> errorTry = Try.error(anyError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.flatMapTryError(ignore -> Try.error(newError));

        // then:
        assertTrue(result.isError());
        assertSame(newError, result.getError());
    }

    @Test
    public void flatMapTryError_shouldReturnItselfIfWasSuccessBefore() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = successTry.flatMapTryError(ignore -> Try.error(newError));

        // then:
        assertTrue(result.isSuccess());
        assertSame(result.getSuccess(), anyValue);
    }

    @Test
    public void flatMapTryError_shouldMapIfErrorClassMatches() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.flatMapTryError(Throwable.class, ignore -> Try.error(newError));

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), newError);
    }

    @Test
    public void flatMapTryError_shouldNotMapIfErrorClassDoesNotMatch() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        Throwable newError = new RuntimeException();

        // when:
        Try<Object> result = errorTry.flatMapTryError(AssertionError.class, ignore -> Try.error(newError));

        // then:
        assertTrue(result.isError());
        assertSame(result.getError(), givenError);
    }

    @Test(expected = NullPointerException.class)
    public void flatMapTryError_shouldThrowIfMappingResultIsNull() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        // expect:
        errorTry.flatMapTryError(ignore -> null);
    }

    @Test(expected = NullPointerException.class)
    public void flatMapTryError_shouldThrowIfClassMatchesAndMappingResultIsNull() {
        // given:
        Throwable givenError = new Throwable();
        Try<Object> errorTry = Try.error(givenError);

        // expect:
        errorTry.flatMapTryError(Throwable.class, ignore -> null);
    }

    @Test
    public void or_shouldReturnSameTryIfWasSuccess() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.or(Try.error(new Throwable()));

        // then:
        assertTrue(result.isSuccess());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void or_shouldReturnAlternativeTryIfError() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> successTry = Try.error(anyError);

        Object alternativeValue = new Object();
        Try<Object> alternativeTry = Try.success(alternativeValue);

        // when:
        Try<Object> result = successTry.or(alternativeTry);

        // then:
        assertTrue(result.isSuccess());
        assertSame(alternativeValue, result.getSuccess());
    }

    @Test
    public void or_shouldReturnAlternativeErrorTryIfError() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> successTry = Try.error(anyError);

        Throwable alternativeError = new Throwable();
        Try<Object> alternativeTry = Try.error(alternativeError);

        // when:
        Try<Object> result = successTry.or(alternativeTry);

        // then:
        assertTrue(result.isError());
        assertSame(alternativeError, result.getError());
    }

    @Test
    public void orElse_shouldReturnSameTryIfWasSuccess() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.orElse(Try.errors(new Throwable()));

        // then:
        assertTrue(result.isSuccess());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void orElse_shouldReturnAlternativeTryIfError() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> successTry = Try.error(anyError);

        Object alternativeValue = new Object();

        // when:
        Try<Object> result = successTry.orElse(Try.successes(alternativeValue));

        // then:
        assertTrue(result.isSuccess());
        assertSame(alternativeValue, result.getSuccess());
    }

    @Test
    public void orElse_shouldReturnAlternativeErrorTryIfError() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> successTry = Try.error(anyError);

        Throwable alternativeError = new Throwable();

        // when:
        Try<Object> result = successTry.orElse(Try.errors(alternativeError));

        // then:
        assertTrue(result.isError());
        assertSame(alternativeError, result.getError());
    }

    @Test(expected = NullPointerException.class)
    public void orElse_shouldThrowIfSupplierResultIsNull() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> successTry = Try.error(anyError);

        // expect:
        successTry.orElse(() -> null);
    }

    @Test
    public void recover_shouldReturnTheSameTryIfWasSuccess() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.recover(new Object());

        // then:
        assertTrue(result.isSuccess());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void recover_shouldReturnTheAlternativeIfWasError() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        Object alternativeValue = new Object();

        // when:
        Try<Object> result = errorTry.recover(alternativeValue);

        // then:
        assertTrue(result.isSuccess());
        assertSame(alternativeValue, result.getSuccess());
    }

    @Test
    public void recover_shouldReturnTheNullAlternativeIfWasError() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        Try<Object> result = errorTry.recover(null);

        // then:
        assertTrue(result.isSuccess());
        assertNull(result.getSuccess());
    }

    @Test
    public void recoverGet_shouldReturnTheSameTryIfWasSuccess() {
        // given:
        Object anyValue = new Object();
        Try<Object> successTry = Try.success(anyValue);

        // when:
        Try<Object> result = successTry.recoverGet(Object::new);

        // then:
        assertTrue(result.isSuccess());
        assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void recoverGet_shouldReturnTheAlternativeIfWasError() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        Object alternativeValue = new Object();

        // when:
        Try<Object> result = errorTry.recoverGet(() -> alternativeValue);

        // then:
        assertTrue(result.isSuccess());
        assertSame(alternativeValue, result.getSuccess());
    }

    @Test
    public void recoverGet_shouldReturnTheNullAlternativeIfWasError() {
        // given:
        Throwable anyError = new Throwable();
        Try<Object> errorTry = Try.error(anyError);

        // when:
        Try<Object> result = errorTry.recoverGet(() -> null);

        // then:
        assertTrue(result.isSuccess());
        assertNull(result.getSuccess());
    }

}
