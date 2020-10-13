package max.command;

import max.database.Credentials;

import java.io.IOException;

public class RegisterCommand extends Command {

    protected Credentials credentials = null;

    public RegisterCommand() {
        commandKey = "register";
        description = "Register into the system to manage your own dragons.\nSyntax: register {credentials}";
    }

    @Override
    public void addInput(Object credentials) {
        this.credentials = (Credentials) credentials;
    }

    @Override
    public Object execute(ExecutionContext context, Credentials credentials) throws IOException {
        return context.DBRequestManager().register(this.credentials, context.resourcesBundle());
    }

    @Override
    public int requireInput() {
        return TYPE_INPUT_CREDENTIAL;
    }

    @Override
    public Object getInput() {
        return credentials;
    }
}
