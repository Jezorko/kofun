package io.kofun;

public interface CheckedFunction<ArgumentType, ResultType, ErrorType extends Throwable> {

    ResultType apply(ArgumentType argument) throws ErrorType;

}
