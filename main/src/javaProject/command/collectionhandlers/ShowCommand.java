package javaProject.command.collectionhandlers;


import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.database.Credentials;
import javaProject.database.UserModel;

public class ShowCommand extends Command {

    public ShowCommand() {
        commandKey = "show";
        description = "вывести в стандартный поток вывода все элементы коллекции в строковом представлении";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) {

        if (context.collectionController().credentialsNotExist(credentials))
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");

        return context.collectionManager().getSerializableList();
    }
}