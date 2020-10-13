package max.command;


import max.database.Credentials;

import java.io.IOException;
import java.util.Map;

public class DescriptorCommand extends Command {

    private Map<String, Command> commandsDictionary;
    /**
     * Конструктор - создает объект класса ManDescriptorCommand и экземпляр класса commandsDictionary для вывода информации о команде по ее ключу
     * @param commandsDictionary - commands instances for getting its descriptions
     */
    public DescriptorCommand(Map<String, Command> commandsDictionary) {
        this.commandsDictionary = commandsDictionary;
        commandKey = "man";
        description = "Describe a command by its key";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        StringBuilder s = new StringBuilder();
        if (this.commandsDictionary.containsKey(args[0]))
            s.append("Command: ").append(args[0]).append("\n").append(this.commandsDictionary.get(args[0]).getDescription());
        else
            s.append("Command: '").append(args[0]).append("' doesn't exist");
        return s.toString();
    }
}