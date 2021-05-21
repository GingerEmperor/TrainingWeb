package org.example.exeptions;

public class CanNotAccessException extends RuntimeException {

    public CanNotAccessException(final String message) {
        super("Невозможно попасть на страницу - " + message);
    }
}
