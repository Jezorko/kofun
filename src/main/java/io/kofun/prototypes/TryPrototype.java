package io.kofun.prototypes;

import io.kofun.CheckedConsumer;
import io.kofun.CheckedRunnable;
import io.kofun.Iterators;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Contract:<br/>
 * * methods with {@code try} in their name can throw only {@link Error} if their parameters are not null<br/>
 * * methods may not have any side effects
 *
 * @param <SuccessType> the type of the result if this Try is a success
 * @param <NewTryType>  the type of the new try for fluent chaining
 */
public interface TryPrototype<SuccessType, NewTryType extends TryPrototype> extends FluentPrototype<NewTryType>,
                                                                                    Iterable<SuccessType> {

    SuccessType getSuccess();

    @NotNull
    Throwable getError();

    boolean isSuccess();

    default boolean isError() {
        return !isSuccess();
    }

    @NotNull
    @ExtensibleFluentChain
    <AnySuccessType> NewTryType recreateSuccess(AnySuccessType success);

    @NotNull
    @ExtensibleFluentChain
    <AnySuccessType> NewTryType recreateError(Throwable error);

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onSuccess(@NotNull Consumer<? super SuccessType> successConsumer) {
        if (isSuccess()) {
            successConsumer.accept(getSuccess());
        }
        return retype();
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onSuccessTryConsumer(@NotNull Consumer<SuccessType> consumer) {
        return onSuccessTry(consumer::accept);
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onSuccessTryRunnable(@NotNull Runnable runnable) {
        return onSuccessTryRun(runnable::run);
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onSuccessTryRun(@NotNull CheckedRunnable runnable) {
        return onSuccessTry(success -> runnable.run());
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onSuccessTry(@NotNull CheckedConsumer<SuccessType, ? extends Throwable> consumer) {
        if (isSuccess()) {
            try {
                consumer.accept(getSuccess());
            } catch (Throwable error) {
                return recreateError(error);
            }
        }
        return retype();
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onError(@NotNull Consumer<? super Throwable> errorConsumer) {
        if (isError()) {
            errorConsumer.accept(getError());
        }
        return retype();
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onErrorTryConsumer(@NotNull Consumer<? super Throwable> errorConsumer) {
        return onErrorTry(errorConsumer::accept);
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onErrorTryRunnable(@NotNull Runnable runnable) {
        return onErrorTryRun(runnable::run);
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onErrorTryRun(@NotNull CheckedRunnable runnable) {
        return onErrorTry(error -> runnable.run());
    }

    @NotNull
    @Contract("_ -> this")
    @ExtensibleFluentChain
    default NewTryType onErrorTry(@NotNull CheckedConsumer<? super Throwable, ? extends Throwable> errorConsumer) {
        if (isError()) {
            try {
                errorConsumer.accept(getError());
            } catch (Throwable error) {
                return recreateError(error);
            }
        }
        return retype();
    }

    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onError(Class<ErrorType> errorClass, @NotNull Consumer<? super ErrorType> errorConsumer) {
        return onError(error -> {
            if (errorClass.isAssignableFrom(error.getClass())) {
                errorConsumer.accept(errorClass.cast(error));
            }
        });
    }

    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onErrorTryConsumer(@NotNull Class<ErrorType> errorClass, @NotNull Consumer<? super ErrorType> errorConsumer) {
        return onErrorTry(errorClass, errorConsumer::accept);
    }

    @NotNull
    @ExtensibleFluentChain
    default <ErrorType extends Throwable> NewTryType onErrorTryRunnable(@NotNull Class<ErrorType> errorClass, @NotNull Runnable runnable) {
        return onErrorTryRun(errorClass, runnable::run);
    }

    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onErrorTryRun(@NotNull Class<ErrorType> errorClass, @NotNull CheckedRunnable runnable) {
        return onErrorTry(errorClass, error -> runnable.run());
    }

    @NotNull
    @ExtensibleFluentChain
    @Contract("_, _ -> this")
    default <ErrorType extends Throwable> NewTryType onErrorTry(@NotNull Class<ErrorType> errorClass,
                                                                @NotNull CheckedConsumer<? super ErrorType, ? extends Throwable> errorConsumer) {
        return onErrorTry(error -> {
            if (errorClass.isAssignableFrom(error.getClass())) {
                errorConsumer.accept(errorClass.cast(error));
            }
        });
    }

    default <AccumulatorType, CollectionType> CollectionType collect(@NotNull Collector<? super SuccessType, AccumulatorType, CollectionType> collector) {
        return stream().collect(collector);
    }

    default <CollectionType> CollectionType collect(@NotNull Supplier<CollectionType> collectionSupplier,
                                                    @NotNull BiConsumer<CollectionType, ? super SuccessType> valueAdder,
                                                    @NotNull BiConsumer<CollectionType, CollectionType> collectionsMerger) {
        return stream().collect(collectionSupplier, valueAdder, collectionsMerger);
    }

    @NotNull
    default Stream<SuccessType> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @NotNull
    @Override
    default Iterator<SuccessType> iterator() {
        return isSuccess() ? Iterators.singleton(getSuccess()) : Iterators.emptyIterator();
    }

}
