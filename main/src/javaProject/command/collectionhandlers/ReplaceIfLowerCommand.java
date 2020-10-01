package javaProject.command.collectionhandlers;


import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.coreSources.Organization;
import javaProject.database.Credentials;
import javaProject.database.UserModel;
import javaProject.exception.AuthorizationException;
import javaProject.exception.OrgFormatException;

import java.io.IOException;


public class ReplaceIfLowerCommand extends Command {

    protected Organization organization = null;

    public ReplaceIfLowerCommand() {
        commandKey = "replace_if_lower";
        description = "заменить значение по ключу, если новое значение меньше старого.\nSyntax: replace_if_lower key {element}";
    }

    @Override
    public void addInput(Object organization) {
        this.organization = (Organization) organization;
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (organization == null)
            throw new OrgFormatException();

        int organizationID = context.collectionManager().isLowerAndGetID(Integer.valueOf(args[0]), organization);
        if (organizationID > 0) {
            //AuthorizationException happens when the credentials passed are wrong and the user was already logged
            String resultOrganizationUpdated = "";
            try {
                resultOrganizationUpdated = context.collectionController().updateDragon(organizationID, organization, credentials);
            } catch (AuthorizationException ex) {
                return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");
            }


            if (resultOrganizationUpdated == null) {
                context.collectionManager().replaceIfLower(Integer.valueOf(args[0]), organization);
                sb.append(organization.toString()).append(" replaced the young poor organization!");
            } else
                sb.append("Problems updating organization: ").append(resultOrganizationUpdated);
        } else
            sb.append("The given Organization is not old enough! or the key is wrong!");

        return sb.toString();
    }

    @Override
    public int requireInput() {
        return TYPE_INPUT_ORGANIZATION;
    }
    @Override
    public Object getInput() {
        return organization;
    }
}