package io.kofun.exception;

public class PredicateNotMatchingException extends RuntimeException {

    private final Object tryResult;

    public PredicateNotMatchingException(Object tryResult) {
        super("given try result does not match the predicate");
        this.tryResult = tryResult;
    }
}
