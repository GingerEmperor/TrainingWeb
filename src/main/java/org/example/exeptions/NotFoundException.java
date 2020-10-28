package org.example.exeptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super("Невозможно найти в базе данных - "+message);
    }
}
