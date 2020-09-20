package javaProject.command;

import java.io.IOException;

public class ExitCommand extends Command {

    public ExitCommand() {
        commandKey = "exit";
        description = "завершить программу (без сохранения в файл))";
    }

    @Override
    public Object execute(ExecutionContext context) throws IOException {
        return null;
    }

}