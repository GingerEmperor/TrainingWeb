package org.example.exeptions;

public class InvalidNameException extends RuntimeException {

    public InvalidNameException(final String message) {
        super(message);
    }
}
