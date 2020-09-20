package javaProject.exception;

public class ConnectException extends RuntimeException{
    public ConnectException() {
        super("Не удалось подключиться к серверу. Повторите попытку позже.");
    }
}