package org.example.exeptions;

public class MuscleGroupNotFoundException extends RuntimeException {

    public MuscleGroupNotFoundException(final String message) {
        super(message);
    }
}
