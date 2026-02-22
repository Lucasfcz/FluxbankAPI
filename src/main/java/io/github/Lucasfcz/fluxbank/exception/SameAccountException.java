package io.github.Lucasfcz.fluxbank.exception;

public class SameAccountException extends RuntimeException {
    public SameAccountException(String message) {
        super(message);
    }
}
