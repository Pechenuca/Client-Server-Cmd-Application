package max.command.collectionhandlers;

import max.command.Command;
import max.command.ExecutionContext;
import max.database.Credentials;
import max.database.UserModel;
import max.exception.AuthorizationException;

import java.io.IOException;

public class ClearCommand extends Command {

    public ClearCommand() {
        commandKey = "clear";
        description = "очистить коллекцию";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {

        //AuthorizationException happens when the credentials passed are wrong and the user was already logged
        String resDeletingAll = "";
        try {
            resDeletingAll = context.DBRequestManager().deleteAllOrganization(credentials, context.resourcesBundle());
        } catch (AuthorizationException ex) {
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");
        }

        if (resDeletingAll == null) {
            context.collectionManager().clear();
            return context.resourcesBundle().getString("server.response.command.clear");
        } else
            return resDeletingAll;
    }
}