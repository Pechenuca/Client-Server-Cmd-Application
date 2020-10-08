package max.exception;

import java.io.IOException;

public class EmptyFileException extends IOException {
    /**
     * Конструктор - создает объект класса EmptyFileException
     * @param s - сообщение об исключении
     */
    public EmptyFileException(String s) {
        super(s);
    }
}