package javaProject.exception;

public class ValidationException extends RuntimeException {
    public ValidationException() {
        super("Команда не прошла валидацию");
    }
}
