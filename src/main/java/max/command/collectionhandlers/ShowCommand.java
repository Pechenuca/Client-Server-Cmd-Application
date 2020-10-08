package max.command.collectionhandlers;

import max.command.Command;
import max.command.ExecutionContext;
import max.database.Credentials;
import max.database.UserModel;

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