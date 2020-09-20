package javaProject.command.collectionhandlers;


import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.coreSources.Organization;
import javaProject.exception.OrgFormatException;

import java.io.IOException;

public class UpdateCommand extends Command {

    protected boolean requireInputs = true;
    protected Organization organization = null;

    public UpdateCommand() {
        commandKey = "update";
        description = "обновить значение элемента коллекции, id которого равен заданному.\nSyntax: update id {element}";
    }

    @Override
    public void addOrgInput(Organization organization) {
        this.organization = organization;
    }

    @Override
    public Object execute(ExecutionContext context) throws IOException {
        context.result().setLength(0);

        if (organization == null)
            throw new OrgFormatException();

        // If it successfully replace it, returns the value of the old mapped object
        if (context.collectionManager().update(Integer.valueOf(args[0]), organization) != null)
            context.result().append(organization.toString()).append(" Updated!");
        else
            context.result().append("The ID '").append(Integer.valueOf(args[0])).append("' doesn't exist");

        return context.result().toString();
    }

    @Override
    public boolean requireOrgInput() {
        return requireInputs;
    }
    @Override
    public Organization getOrganization() {
        return organization;
    }


}