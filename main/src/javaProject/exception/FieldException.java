package javaProject.exception;

public class FieldException extends RuntimeException{
    public FieldException(){
        super("Недопустимое значение поля!");
    }
}