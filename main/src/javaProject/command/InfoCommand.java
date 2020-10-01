package javaProject.command;

import javaProject.database.Credentials;

public class InfoCommand extends Command {

    public InfoCommand() {
        commandKey = "info";
        description = "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) {
        return context.collectionManager().toString();
    }
}