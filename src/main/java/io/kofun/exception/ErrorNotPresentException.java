package io.kofun.exception;

public class ErrorNotPresentException extends RuntimeException {
    public ErrorNotPresentException() {
        super("action cannot be performed because no error was present");
    }
}
