package javaProject.command.collectionhandlers;


import javaProject.command.Command;
import javaProject.command.ExecutionContext;

import java.io.IOException;

public class RemoveGreaterKeyCommand extends Command {
    public RemoveGreaterKeyCommand() {
        commandKey = "remove_greater_key";
        description = "удалить из коллекции все элементы, ключ которых превышает заданный\nSyntax: remove_greater_key key";
    }

    @Override
    public Object execute(ExecutionContext context) throws IOException {
        context.result().setLength(0);
        int initialSize = context.collectionManager().getCollection().size();
        context.collectionManager().removeGreaterKey(Integer.valueOf(args[0]));
        int finalSize = context.collectionManager().getCollection().size();

        if (initialSize == finalSize)
            context.result().append("No Dragons removed");
        else
            context.result().append("A total of ").append(initialSize - finalSize).append(" were removed");
        return context.result().toString();
    }
}
