package org.example.exeptions;

public class MuscleAlreadyExistsException extends RuntimeException {
    public MuscleAlreadyExistsException(final String message) {
        super(message);
    }
}
