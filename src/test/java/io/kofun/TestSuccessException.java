package io.kofun;

class TestSuccessException extends RuntimeException {

    static <AnyReturnType> AnyReturnType throwIt(Object... anyArgs) {
        throw new TestSuccessException();
    }

    public TestSuccessException() {
        super("Test should pass if this exception was thrown");
    }
}
