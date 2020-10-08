package max.coreSources;


import max.database.Credentials;
import max.util.IHandlerInput;

public class CredentialFactory {
    private static CredentialFactory instance = null;
    private IHandlerInput inputHandler;

    public static CredentialFactory getInstance() {
        if (instance == null)
            instance = new CredentialFactory();
        return instance;
    }

    /**
     *
     * Read what the user writes on, validates it and create the instance if
     * everything is successful
     *
     * @param inputHandler manages all related with the IO
     * @return instance of Credentials with the input entered
     */
    public Credentials generateCredentialByInput(IHandlerInput inputHandler) {
        this.inputHandler = inputHandler;
        String username = "";
        do {
            username = inputHandler.readWithMessage("username: ");
            username = username.isEmpty() ? null : username;
        } while (username == null);

        String pass = "";
        do {
            pass = inputHandler.readWithMessage("password: ");
            pass = pass.isEmpty() ? null : pass;
        } while (pass == null);

        return new Credentials(-1, username, pass);
    }
}

