package max.exception;

public class NoSuchCommandException extends RuntimeException {
    public NoSuchCommandException(String s) {
        super(s);
    }
}
