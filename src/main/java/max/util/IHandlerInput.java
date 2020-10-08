package max.util;

import java.util.NoSuchElementException;
import java.util.Scanner;

public interface IHandlerInput {
    /**
     * Функция получения данных с консоли
     * @return возвращает строку с данными из консоли
     */
    String read() throws NoSuchElementException;
    /**
     * Функция получения данных с консоли
     * @param s - строка, показывающая, ввод каких данных программа ожидает от пользователя
     * @return возвращает строку с данными из консоли
     */
    String readWithMessage(String s);
    /**
     * Функция вывода данных в консоль
     * @param s - строка, выводимая в консоль
     */
    void printLn(String s);
    /**
     * Функция вывода данных в консоль
     * @param code - переменная, показывающая, успешно ли завершилась предыдущая команда
     * @param s - строка, выводимая в консоль
     */
    void printLn(int code, String s);
    /**
     * Функция вывода элементов списка в консоль
     * @param s - элемент списка и строка, выводящаяся в консоль
     */
    void printElemOfList(String s);

    /**
     * check if the inputs are handled by the client or the program
     * @return interactive
     */
    boolean isInteractive();

    String[] getInputsAfterInsert();
    void setInputsAfterInsert(String[] numLineFile);
    int getResultCode();
    Scanner getCommandReader();
}