package net.sokontokoro_factory.lovelive.exception;

public class InvalidArgumentException extends Exception{
    public InvalidArgumentException(String message){
        super(message);
    }
    public InvalidArgumentException(String message, Throwable t){
        super(message, t);
    }
}
