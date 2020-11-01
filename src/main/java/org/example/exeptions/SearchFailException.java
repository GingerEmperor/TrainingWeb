package org.example.exeptions;

public class SearchFailException extends RuntimeException {

    public SearchFailException(final String message) {
        super("Ошибка поиска - " + message);
    }
}
