package javaProject.command.collectionhandlers;


import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.database.Credentials;
import javaProject.database.UserModel;
import javaProject.exception.AuthorizationException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

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
            deletedIDs = context.collectionController().deleteOrganizationsGreaterThanKey(Integer.parseInt(args[0]), credentials);
        } catch (SQLException | NoSuchAlgorithmException ex) {
            resultDeletedByKey = ex.getMessage();
        } catch (AuthorizationException ex) {
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");
        }

        if (deletedIDs != null)
            context.collectionManager().removeOnKey(deletedIDs);
        else
            sb.append("Problems deleting organizations: ").append(resultDeletedByKey);

        int finalSize = context.collectionManager().getCollection().size();

        if (initialSize == finalSize)
            sb.append("No Organizations removed");
        else
            sb.append("A total of ").append(initialSize - finalSize).append(" organizations were removed");
        return sb.toString();
    }
}