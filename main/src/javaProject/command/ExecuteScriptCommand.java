package javaProject.command;

import javaProject.coreSources.Factory;
import javaProject.database.Credentials;
import javaProject.database.UserModel;
import javaProject.exception.OrgFormatException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExecuteScriptCommand extends Command {

    private Factory factory;
    private List<Command> commands;

    public ExecuteScriptCommand(List<Command> commands) {
        this.commands = commands;
        factory = new Factory();
        commandKey = "execute_script";
        description = "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\nSyntax: execute_script file_name";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        if (args.length < 1)
            throw new ArrayIndexOutOfBoundsException();

        if (context.collectionController().credentialsNotExist(credentials))
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");

        ArrayList<Object> result = new ArrayList<>();

        String pathToFile = Paths.get(args[0]).toAbsolutePath().toString();
        String commandsStr = context.fileManager().getStrFromFile(pathToFile);

        String[] commands = commandsStr.trim().split("\n");
        for (int i = 0; i < commands.length; i++) {
            try {
                result.add("\nCOMMAND #" + i);
                boolean organizationInputSuccess = false;
                String[] ss = commands[i].trim().split(" ");
                Command command = getCommand(ss[0]);
                if (command == null) {
                    result.add("Not found command");
                    break;
                }
                command.setArgs(getCommandArgs(ss));
                if (command.requireInput() == ICommand.TYPE_INPUT_ORGANIZATION) {
                    String[] inputsAfterInsert = Arrays.copyOfRange(commands, i + 1, commands.length);
                    command.addInput(factory.generateFromScript(inputsAfterInsert));
                    if (command.getInput() == null)
                        result.add("An input was not in the correct format or The number of inputs is different from the needed");
                    else
                        organizationInputSuccess= true;
                }
                result.add(command.execute(context, credentials));
                if (organizationInputSuccess)
                    i+=8;
            } catch (OrgFormatException ex) {
                result.add(ex.getMessage());
            } catch (NumberFormatException ex) {
                result.add("Incorrect format of the entered value");
            } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
                result.add("There is a problem in the amount of args passed");
            }
        }
        return result;
    }

    private Command getCommand(String s) {
        return commands.stream().filter(e -> e.getCommandKey().equals(s)).findFirst().orElse(null);
    }

    public String[] getCommandArgs(String[] fullStr) {
        String[] inputArgs = new String[2];
        inputArgs = Arrays.copyOfRange(fullStr, 1, fullStr.length);
        return inputArgs;
    }
}