package javaProject.command.collectionhandlers;


import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.coreSources.Organization;
import javaProject.exception.OrgFormatException;


public class ReplaceIfLowerCommand extends Command {

    protected boolean requireInputs = true;
    protected Organization organization = null;

    public ReplaceIfLowerCommand() {
        commandKey = "replace_if_lower";
        description = "заменить значение по ключу, если новое значение меньше старого.\nSyntax: replace_if_lower key {element}";
    }

    @Override
    public void addOrgInput(Organization organization) {
        this.organization = organization;
    }

    @Override
    public Object execute(ExecutionContext context) {
        context.result().setLength(0);
        if (organization == null)
            throw new OrgFormatException();

        if (context.collectionManager().replaceIfLower(Integer.valueOf(args[0]), organization) != null)
            context.result().append(organization.toString()).append(" replaced the young poor dragon!");
        else
            context.result().append("The key '").append(Integer.valueOf(args[0])).append("' doesn't exist or is not old enough!");
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