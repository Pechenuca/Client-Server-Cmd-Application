package javaProject.exception;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String s) {
        super(s);
    }
    public AuthorizationException() {
        super();
    }
}