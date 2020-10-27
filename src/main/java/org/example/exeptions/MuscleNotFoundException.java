package org.example.exeptions;

public class MuscleNotFoundException extends RuntimeException {

    public MuscleNotFoundException(String message) {
        super(message);
    }
}
