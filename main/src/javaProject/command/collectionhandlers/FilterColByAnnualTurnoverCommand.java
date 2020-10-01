package javaProject.command.collectionhandlers;




import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.database.Credentials;
import javaProject.database.UserModel;
import javaProject.util.ListEntrySerializable;

import java.io.IOException;
import java.util.List;

public class FilterColByAnnualTurnoverCommand extends Command {
    public FilterColByAnnualTurnoverCommand() {
        commandKey = "filter_contains_name";
        description = "вывести элементы, значение поля name которых содержит заданную.\nSyntax: filter_contains_name name";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {

        if (context.collectionController().credentialsNotExist(credentials))
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");

        List<ListEntrySerializable> filteredCol = context.collectionManager().filterContainsName(args[0]);
        if (filteredCol.isEmpty())
            return "No elements found.";
        else
            return filteredCol;
    }
}
