package javaProject.command;

import java.io.IOException;

public interface ICommand {
    /**
     * Функция для выполнения команды по работе с коллекцией
     * @param context - the context usable by every main.main.managers.managers.command to communicate with the collection and file manager
     */
    Object execute(ExecutionContext context) throws IOException;
}