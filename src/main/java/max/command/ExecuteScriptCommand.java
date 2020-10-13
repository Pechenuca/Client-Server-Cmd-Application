package max.command;

import max.coreSources.Factory;
import max.database.Credentials;
import max.database.UserModel;
import max.exception.OrgFormatException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExecuteScriptCommand extends Command {

    private final Factory factory;
    private final List<Command> commands;

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

        if (context.DBRequestManager().credentialsNotExist(credentials))
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");

        ArrayList<String> result = new ArrayList<>();
        String commandsStr = context.fileManager().getStrFromFile(args[0]);

        String[] commands = commandsStr.trim().split("\n");
        for (int i = 0; i < commands.length; i++) {
            try {
                String commandTitle = MessageFormat.format(context.resourcesBundle().getString("server.response.command.execscript.title"), i);
                result.add("\n" + commandTitle);
                boolean dragonInputSuccess = false;
                String[] ss = commands[i].trim().split(" ");
                Command command = getCommand(ss[0]);
                if (command == null) {
                    result.add(context.resourcesBundle().getString("server.response.command.execscript.error.comm.notfound"));
                    continue;
                }
                command.setArgs(getCommandArgs(ss));
                if (command.requireInput() == ICommand.TYPE_INPUT_ORGANIZATION) {
                    ArrayList<String> inputsAfterInsert = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(commands, i + 1, commands.length)));
                    inputsAfterInsert.add(0, String.valueOf(credentials.id));
                    command.addInput(factory.generateFromScript(inputsAfterInsert));
                    if (command.getInput() == null)
                        result.add(context.resourcesBundle().getString("server.response.command.execscript.error.dragoninput"));
                    else
                        dragonInputSuccess = true;
                }
                result.add((String) command.execute(context, credentials));
                if (dragonInputSuccess)
                    i+=8;
            } catch (OrgFormatException ex) {
                result.add(ex.getMessage());
            } catch (NumberFormatException ex) {
                result.add(context.resourcesBundle().getString("server.response.error.format.arguments"));
            } catch (ArrayIndexOutOfBoundsException | NullPointerException ex) {
                result.add(context.resourcesBundle().getString("server.response.error.amount.arguments"));
            }
        }
        return String.join("\n ", result);
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