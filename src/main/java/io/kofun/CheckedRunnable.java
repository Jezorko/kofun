package io.kofun;

public interface CheckedRunnable<ErrorType extends Throwable> {

    void run() throws ErrorType;

}
