package org.example.exeptions;

public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(final String message) {
        super("Уже есть в базе данных - "+message);
    }
}
