package javaProject.command.collectionhandlers;



import javaProject.command.Command;
import javaProject.command.ExecutionContext;

import java.io.IOException;

public class RemoveKeyCommand extends Command {

    public RemoveKeyCommand() {
        commandKey = "remove_key";
        description = "удалить элемент из коллекции по его ключу.\nSyntax: remove_key key";
    }

    @Override
    public Object execute(ExecutionContext context) throws IOException {
        context.result().setLength(0);
        if (context.collectionManager().removeKey(Integer.valueOf(args[0])) != null)
            context.result().append("k:").append(args[0]).append(" Successfully removed!");
        else
            context.result().append("The key '").append(args[0]).append("' doesn't exist");
        return context.result().toString();
    }
}

