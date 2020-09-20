package javaProject.command.collectionhandlers;




import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.util.ListEntrySerializable;

import java.io.IOException;
import java.util.List;

public class FilterColByAnnualTurnoverCommand extends Command {
    public FilterColByAnnualTurnoverCommand() {
        commandKey = "filter_starts_with_name";
        description = "вывести элементы, значение поля name которых начинается с заданной подстроки.\nSyntax: filter_starts_with_name name";
    }

    @Override
    public Object execute(ExecutionContext context) throws IOException {
        List<ListEntrySerializable> filteredCol = context.collectionManager().filterStartsWithName(args[0]);
        if (filteredCol.isEmpty())
            return "No elements found.";
        else
            return filteredCol;
    }
}
