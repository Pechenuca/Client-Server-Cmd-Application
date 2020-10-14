package max.command.collectionhandlers;

import max.command.Command;
import max.command.ExecutionContext;
import max.database.Credentials;
import max.database.UserModel;
import max.exception.AuthorizationException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.MessageFormat;

public class RemoveGreaterKeyCommand extends Command {

    public RemoveGreaterKeyCommand() {
        commandKey = "remove_greater_key";
        description = "удалить из коллекции все элементы, ключ которых превышает заданный\nSyntax: remove_greater_key key";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        StringBuilder sb = new StringBuilder();
        int initialSize = context.collectionManager().getCollection().size();

        String resultDeletedByKey = "";
        int[] deletedIDs = null;
        try {
            deletedIDs = context.DBRequestManager().deleteOrganizationsGreaterThanKey(Integer.parseInt(args[0]), credentials);
        } catch (SQLException | NoSuchAlgorithmException ex) {
            resultDeletedByKey = context.DBRequestManager().getSQLErrorString("remove organizations greater than key", context.resourcesBundle(), ex);
        } catch (AuthorizationException ex) {
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");
        }

        if (deletedIDs != null)
            context.collectionManager().removeOnKey(deletedIDs);
        else
            sb.append(resultDeletedByKey);

        int finalSize = context.collectionManager().getCollection().size();
        if (initialSize == finalSize)
            sb.append(context.resourcesBundle().getString("server.response.command.remove.noremoved"));
        else
            sb.append(MessageFormat.format(context.resourcesBundle().getString("server.response.command.remove.total.removed"), (initialSize - finalSize)));

        return sb.toString();
    }
}
