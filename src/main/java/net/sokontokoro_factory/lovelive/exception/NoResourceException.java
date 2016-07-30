package net.sokontokoro_factory.lovelive.exception;

public class NoResourceException extends Exception{
    public NoResourceException(String message){
        super(message);
    }
    public NoResourceException(String message, Throwable t){
        super(message, t);
    }
}
