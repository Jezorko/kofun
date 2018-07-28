package io.kofun;

public interface CheckedSupplier<T, ErrorType extends Throwable> {

    T get() throws ErrorType;

}
