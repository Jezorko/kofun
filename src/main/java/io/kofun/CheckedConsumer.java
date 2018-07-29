package io.kofun;

public interface CheckedConsumer<T, ErrorType extends Throwable> {

    void accept(T value) throws ErrorType;

}
