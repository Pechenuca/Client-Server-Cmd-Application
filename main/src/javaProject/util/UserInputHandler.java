package javaProject.util;

import java.io.Serializable;
import java.util.Scanner;

public class UserInputHandler implements IHandlerInput, Serializable {

    private static final long serialVersionUID = -4344186384932255034L;

    private final Scanner commandReader;
    private boolean interactive;
    private String[] inputsAfterInsert;
    private int resultCode;

    /**
     * Конструктор - создает объект класса UserInputHandler для работы со входными данными, создает сканер для считывания данных
     * @see UserInputHandler#UserInputHandler(boolean)
     */
    public UserInputHandler(boolean interactive) {
        this.commandReader = new Scanner(System.in);
        this.interactive = interactive;
        this.inputsAfterInsert = new String[8];
    }

    @Override
    public String read() {
        return commandReader.nextLine();
    }

    @Override
    public String readWithMessage(String s) {
        System.out.print(s);
        return this.read();
    }

    @Override
    public void printLn(String s) {
        System.out.println(s);
    }

    @Override
    public void printLn(int code, String s) {
        this.resultCode = code;
        String codeResult = (this.resultCode == 0) ? "SUCCESSFUL: " : "ERROR: ";
        System.out.println(codeResult + s);
    }

    @Override
    public void printElemOfList(String s) {
        System.out.println(s);
    }

    @Override
    public boolean isInteractive() {
        return interactive;
    }

    @Override
    public String[] getInputsAfterInsert() {
        return inputsAfterInsert;
    }

    @Override
    public int getResultCode() {
        return resultCode;
    }

    @Override
    public Scanner getCommandReader() {
        return commandReader;
    }

    @Override
    public void setInputsAfterInsert(String[] inputsAfterInsert) {
        this.inputsAfterInsert = inputsAfterInsert;
    }
}