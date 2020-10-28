package org.example.exeptions;

public class CanNotDeleteException extends RuntimeException{
    public CanNotDeleteException(String message){
        super("Невозможно удалить - "+message);
    }
}
