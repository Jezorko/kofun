package io.kofun;

import io.kofun.prototypes.TryPrototype;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static io.kofun.ExtensibleFluentChainTestUtil.prototypeImplementation;
import static io.kofun.ExtensibleFluentChainTestUtil.shouldReimplementAllExtensibleFluentChainMethods;
import static org.junit.Assert.*;

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

}
