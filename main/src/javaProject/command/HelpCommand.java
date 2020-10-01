package javaProject.command;

import javaProject.database.Credentials;

import java.io.IOException;
import java.util.Set;
public class HelpCommand extends Command {

    public final String description = "";
    private Set<String> keysCommands;

    /**
     * Конструктор - создает объект класса HelpCommand и keysCommands для вывода доступных команд
     * @param keysCommands - keys for showing commands available
     */
    public HelpCommand(Set<String> keysCommands) {
        commandKey = "help";
        this.keysCommands = keysCommands;
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        StringBuilder s = new StringBuilder();
        s.append("Some Commands for you! \n");
        this.keysCommands.forEach(e -> s.append("- ").append(e).append("\n"));
        s.append("\nWrite man {key} to have some details");
        return s.toString();
    }

    @Override
    public String getDescription() {
        return description;
    }
}