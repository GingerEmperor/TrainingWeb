package org.example.exeptions;

public class MuscleGroupAlreadyExistsException extends RuntimeException {
    public MuscleGroupAlreadyExistsException(final String message) {
        super(message);
    }
}
