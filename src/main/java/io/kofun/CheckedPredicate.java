package io.kofun;

public interface CheckedPredicate<TestedType, ErrorType extends Throwable> {

    boolean test(TestedType argument) throws ErrorType;

}
