package max.exception;

public class NotPermissionsException extends RuntimeException {
    public NotPermissionsException(String s) {
        super(s);
    }
    public NotPermissionsException() {
        super();
    }
}
