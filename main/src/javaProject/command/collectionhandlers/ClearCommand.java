package javaProject.command.collectionhandlers;
import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.database.Credentials;
import javaProject.database.UserModel;
import javaProject.exception.AuthorizationException;

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
            resDeletingAll = context.collectionController().deleteAllDragons(credentials);
        } catch (AuthorizationException ex) {
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");
        }


        if (resDeletingAll == null) {
            context.collectionManager().clear();
            return "All elems deleted successfully!";
        } else
            return "Problem clearing dragons: " + resDeletingAll;
    }
}