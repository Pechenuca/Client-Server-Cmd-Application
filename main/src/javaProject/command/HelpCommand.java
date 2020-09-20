package javaProject.command;

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
    public Object execute(ExecutionContext context) {
        StringBuilder s = new StringBuilder();
        s.append("Some Commands for you! \n").append(this.keysCommands.toString()).append("\nWrite man {key} to have some details");
        return s.toString();
    }

    @Override
    public String getDescription() {
        return description;
    }
}