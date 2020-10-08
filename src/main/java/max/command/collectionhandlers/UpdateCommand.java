package max.command.collectionhandlers;

import max.command.Command;
import max.command.ExecutionContext;
import max.coreSources.Organization;
import max.database.Credentials;
import max.database.UserModel;
import max.exception.AuthorizationException;
import max.exception.OrgFormatException;

import java.io.IOException;

public class UpdateCommand extends Command {

    protected Organization organization = null;

    public UpdateCommand() {
        commandKey = "update";
        description = "обновить значение элемента коллекции, id которого равен заданному.\nSyntax: update id {element}";
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

        //AuthorizationException happens when the credentials passed are wrong and the user was already logged
        String organizationIDaddedToDB = "";
        try {
            organizationIDaddedToDB = context.collectionController().updateOrganization(Integer.parseInt(args[0]), organization, credentials);
        } catch (AuthorizationException ex) {
            return new Credentials(-1, UserModel.DEFAULT_USERNAME, "");
        }

        // If it successfully replace it, returns the value of the old mapped object
        if (organizationIDaddedToDB == null) {
            if (context.collectionManager().update(Integer.valueOf(args[0]), organization) != null)
                sb.append(organization.toString()).append(" Updated!");
        } else
            sb.append("Problems updating dragon: ").append(organizationIDaddedToDB);

        return sb.toString();
    }

    @Override
    public int requireInput() {
        return TYPE_INPUT_CREDENTIAL;
    }
    @Override
    public Object getInput() {
        return organization;
    }

}