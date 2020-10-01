package javaProject.command;

import javaProject.database.Credentials;

import java.io.IOException;

public class ExitCommand extends Command {

    public ExitCommand() {
        commandKey = "exit";
        description = "завершить программу (без сохранения в файл))";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        return null;
    }

}