package max.command;


import max.database.Credentials;

import java.io.IOException;

public interface ICommand {

    int TYPE_NO_INPUT = 0;
    int TYPE_INPUT_ORGANIZATION = 1;
    int TYPE_INPUT_CREDENTIAL = 2;

    /**
     * Функция для выполнения команды по работе с коллекцией
     * @param context - the context usable by every max.command to communicate with the collection and file manager
     * @param credentials - credentials of the User that sent the request
     */
    Object execute(ExecutionContext context, Credentials credentials) throws IOException;
}