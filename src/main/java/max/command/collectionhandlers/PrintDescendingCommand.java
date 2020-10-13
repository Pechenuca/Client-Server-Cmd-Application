package max.command.collectionhandlers;

import max.command.Command;
import max.command.ExecutionContext;
import max.coreSources.Organization;
import max.database.Credentials;
import max.database.UserModel;
import max.util.OrganizationEntrySerializable;


import java.io.IOException;
import java.util.List;

public class PrintDescendingCommand extends Command {

    public PrintDescendingCommand() {
        commandKey = "print_descending";
        description = "вывести элементы коллекции в порядке убывания.\nSyntax: print_descending -{k/i/n/d} где: -k=key / -i=id / -n=name / -d=creation_date";
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {

        if (context.DBRequestManager().credentialsNotExist(credentials))
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");

        String res = "";
        List<OrganizationEntrySerializable> sortedOrganizations = null;

        switch (args[0]) {
            case "":
            case "-k":
                //System.out.println("Sorting by key...");
                sortedOrganizations = context.collectionManager().sortByKey();
                break;
            case "-i":
                //System.out.println("Sorting by ID...");
                sortedOrganizations = context.collectionManager().sortById();
                break;
            case "-n":
                //System.out.println("Sorting by Name...");
                sortedOrganizations = context.collectionManager().sortByName();
                break;
            case "-d":
                //System.out.println("Sorting by Creation Date...");
                sortedOrganizations = context.collectionManager().sortByCreationDate();
                break;
            default:
                res = context.resourcesBundle().getString("server.response.command.printdescending.error.options");
        }
        if (sortedOrganizations != null)
            return sortedOrganizations;
        return res;
    }

    @Override
    public String getDescription() {
        return description;
    }
}