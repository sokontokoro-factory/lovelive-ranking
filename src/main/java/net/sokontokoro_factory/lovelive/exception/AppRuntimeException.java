package net.sokontokoro_factory.lovelive.exception;

public class AppRuntimeException extends RuntimeException{
    public AppRuntimeException(String message){
        super(message);
    }
    public AppRuntimeException(String message, Throwable t){
        super(message, t);
    }
}
