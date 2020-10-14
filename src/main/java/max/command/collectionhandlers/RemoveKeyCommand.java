package max.command.collectionhandlers;

import max.command.Command;
import max.command.ExecutionContext;
import max.database.Credentials;
import max.database.UserModel;
import max.exception.AuthorizationException;

import java.io.IOException;
import java.text.MessageFormat;

public class RemoveKeyCommand extends Command {

    public RemoveKeyCommand() {
        commandKey = "remove_key";
        description = "удалить элемент из коллекции по его ключу.\nSyntax: remove_key key";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        String res = "";

        //AuthorizationException happens when the credentials passed are wrong and the user was already logged
        String resultDeletedByKey = "";
        try {
            resultDeletedByKey = context.DBRequestManager().deleteOrganization(Integer.parseInt(args[0]), credentials, context.resourcesBundle());
        } catch (AuthorizationException ex) {
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");
        }

        // If it successfully replace it, returns the value of the old mapped object
        if (resultDeletedByKey == null) {
            if (context.collectionManager().removeKey(Integer.valueOf(args[0])) != null)
                res = MessageFormat.format(context.resourcesBundle().getString("server.response.command.removebykey"), args[0]);
        } else
            res = resultDeletedByKey;

        return res;
    }
}
