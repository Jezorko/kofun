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
