package javaProject.command.collectionhandlers;

import com.sun.prism.impl.shape.OpenPiscesRasterizer;
import javaProject.command.Command;
import javaProject.command.ExecutionContext;
import javaProject.coreSources.Organization;
import javaProject.database.Credentials;
import javaProject.database.UserModel;
import javaProject.exception.AuthorizationException;
import javaProject.exception.OrgFormatException;

import java.io.IOException;

public class UpdateCommand extends Command {

    protected Organization organization = null;

    public UpdateCommand() {
        commandKey = "update";
        description = "обновить значение элемента коллекции, id которого равен заданному.\nSyntax: update id {element}";
    }

    @Override
    public void addInput(Object dragon) {
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
            organizationIDaddedToDB = context.collectionController().updateDragon(Integer.parseInt(args[0]), organization, credentials);
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