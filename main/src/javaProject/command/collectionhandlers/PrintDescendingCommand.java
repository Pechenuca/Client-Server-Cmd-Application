package javaProject.command.collectionhandlers;


import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.util.ListEntrySerializable;

import java.io.IOException;
import java.util.List;

public class PrintDescendingCommand extends Command {

    public PrintDescendingCommand() {
        commandKey = "print_descending";
        description = "вывести элементы коллекции в порядке убывания.\nSyntax: print_descending -{k/i/n/d} где: -k=key / -i=id / -n=name / -d=creation_date";
    }

    @Override
    public Object execute(ExecutionContext context) throws IOException {
        context.result().setLength(0);
        List<ListEntrySerializable> sortedOrganizations = null;

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
                context.result().append("This option is not available. Correct= -{k/i/n/d}");
        }
        if (sortedOrganizations != null)
            return sortedOrganizations;
        return context.result().toString();
    }

    @Override
    public String getDescription() {
        return description;
    }
}