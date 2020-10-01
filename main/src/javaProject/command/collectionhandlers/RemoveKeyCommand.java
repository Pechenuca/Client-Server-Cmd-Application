package javaProject.command.collectionhandlers;



import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.database.Credentials;
import javaProject.database.UserModel;
import javaProject.exception.AuthorizationException;

import java.io.IOException;
public class RemoveKeyCommand extends Command {

    public RemoveKeyCommand() {
        commandKey = "remove_key";
        description = "удалить элемент из коллекции по его ключу.\nSyntax: remove_key key";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        StringBuilder sb = new StringBuilder();

        //AuthorizationException happens when the credentials passed are wrong and the user was already logged
        String resultDeletedByKey = "";
        try {
            resultDeletedByKey = context.collectionController().deleteOrganization(Integer.parseInt(args[0]), credentials);
        } catch (AuthorizationException ex) {
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");
        }

        // If it successfully replace it, returns the value of the old mapped object
        if (resultDeletedByKey == null) {
            if (context.collectionManager().removeKey(Integer.valueOf(args[0])) != null)
                sb.append("k:").append(args[0]).append(" Successfully removed!");
        } else
            sb.append("Problems deleting dragon: ").append(resultDeletedByKey);
        return sb.toString();
    }
}