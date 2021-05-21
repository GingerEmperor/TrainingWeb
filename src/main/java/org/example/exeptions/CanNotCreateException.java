package org.example.exeptions;

public class CanNotCreateException extends RuntimeException {

    public CanNotCreateException(final String message) {
        super("Невозможно создать - " + message);
    }

}
