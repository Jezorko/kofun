package io.kofun;

import io.kofun.prototypes.TryPrototype;
import org.junit.Assert;
import org.junit.Test;

import static io.kofun.ExtensibleFluentChainTestUtil.prototypeImplementation;
import static io.kofun.ExtensibleFluentChainTestUtil.shouldReimplementAllExtensibleFluentChainMethods;

public class TryTest {

    @Test
    public void shouldConformToExtensibleFluentChainContract() {
        shouldReimplementAllExtensibleFluentChainMethods(prototypeImplementation(TryPrototype.class, Try.class));
    }

    @Test
    public void ofSupplier_shouldCreateSuccessIfSupplierDoesNotThrow() {
        // given:
        Object anyValue = new Object();

        // when:
        final Try<Object> result = Try.ofSupplier(() -> anyValue);

        // then:
        Assert.assertTrue(result.isSuccess());
        Assert.assertFalse(result.isError());
        Assert.assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void ofSupplier_shouldCreateErrorIfSupplierThrows() {
        // given:
        RuntimeException error = new RuntimeException();

        // when:
        final Try<Object> result = Try.ofSupplier(() -> {throw error;});

        // then:
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.isError());
        Assert.assertSame(error, result.getError());
    }

    @Test
    public void ofCallable_shouldCreateSuccessIfCallableDoesNotThrow() {
        // given:
        Object anyValue = new Object();

        // when:
        final Try<Object> result = Try.ofCallable(() -> anyValue);

        // then:
        Assert.assertTrue(result.isSuccess());
        Assert.assertFalse(result.isError());
        Assert.assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void ofCallable_shouldCreateErrorIfCallableThrows() {
        // given:
        RuntimeException error = new RuntimeException();

        // when:
        final Try<Object> result = Try.ofCallable(() -> {throw error;});

        // then:
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.isError());
        Assert.assertSame(error, result.getError());
    }

    @Test
    public void runRunnable_shouldCreateSuccessIfRunnableDoesNotThrow() {
        // when:
        final Try<Void> result = Try.runRunnable(() -> {});

        // then:
        Assert.assertTrue(result.isSuccess());
        Assert.assertFalse(result.isError());
    }

    @Test
    public void runRunnable_shouldCreateErrorIfRunnableThrows() {
        // given:
        RuntimeException error = new RuntimeException();

        // when:
        final Try<Void> result = Try.runRunnable(() -> {throw error;});

        // then:
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.isError());
        Assert.assertSame(error, result.getError());
    }

    @Test
    public void run_shouldCreateSuccessIfRunnableDoesNotThrow() {
        // when:
        final Try<Void> result = Try.run(() -> {});

        // then:
        Assert.assertTrue(result.isSuccess());
        Assert.assertFalse(result.isError());
        Assert.assertNull(result.getSuccess());
    }

    @Test
    public void run_shouldCreateErrorIfRunnableThrows() {
        // given:
        RuntimeException error = new RuntimeException();

        // when:
        final Try<Void> result = Try.run(() -> {throw error;});

        // then:
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.isError());
        Assert.assertSame(error, result.getError());
    }

    @Test
    public void of_shouldCreateSuccessIfSupplierDoesNotThrow() {
        // given:
        Object anyValue = new Object();

        // when:
        final Try<Object> result = Try.of(() -> anyValue);

        // then:
        Assert.assertTrue(result.isSuccess());
        Assert.assertFalse(result.isError());
        Assert.assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void of_shouldCreateErrorIfSupplierThrows() {
        // given:
        Throwable error = new Throwable();

        // when:
        final Try<Object> result = Try.of(() -> {throw error;});

        // then:
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.isError());
        Assert.assertSame(error, result.getError());
    }

    @Test
    public void success_shouldCreateASuccessTry() {
        // given:
        Object anyValue = new Object();

        // when:
        final Try<Object> result = Try.success(anyValue);

        // then:
        Assert.assertTrue(result.isSuccess());
        Assert.assertFalse(result.isError());
        Assert.assertSame(anyValue, result.getSuccess());
    }

    @Test
    public void error_shouldCreateAnErrorTry() {
        // given:
        Throwable error = new Throwable();

        // when:
        final Try<Object> result = Try.error(error);

        // then:
        Assert.assertFalse(result.isSuccess());
        Assert.assertTrue(result.isError());
        Assert.assertSame(error, result.getError());
    }

    @Test(expected = Error.class)
    public void error_shouldThrowIfErrorIsInstanceOfJavaUtilError() {
        // given:
        Error error = new Error();

        // expect:
        Try.error(error);
    }

}
