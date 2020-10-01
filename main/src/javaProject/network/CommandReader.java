package javaProject.network;


import javaProject.command.*;
import javaProject.coreSources.CredentialFactory;
import javaProject.database.Credentials;
import javaProject.database.UserModel;
import javaProject.exception.AuthorizationException;
import javaProject.exception.NoSuchCommandException;
import javaProject.util.IHandlerInput;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javaProject.CommandManager;
import javaProject.coreSources.Factory;
import javaProject.coreSources.Organization;

import java.io.IOException;

public class CommandReader {

    private static final Logger LOG = LogManager.getLogger(CommandReader.class);

    private final ClientChannel channel;
    private final IHandlerInput userInputHandler;
    private final CommandManager commandManager;
    private final Factory factory;

    public CommandReader(ClientChannel socket, CommandManager commandManager, IHandlerInput userInput) {
        this.channel = socket;
        this.userInputHandler = userInput;
        this.commandManager = commandManager;
        this.factory = new Factory();
    }

    /**
     * Функция для чтения команд от пользователя
     */
    public void startInteraction(Credentials credentials) throws IOException, ArrayIndexOutOfBoundsException, NoSuchCommandException {
        String commandStr;
        commandStr = userInputHandler.readWithMessage("Write Command: ");
        Command command = commandManager.getCommand(commandStr);

        if (command instanceof HelpCommand || command instanceof DescriptorCommand)
            userInputHandler.printLn((String) command.execute(null, credentials));
        else if (command instanceof ExitCommand)
            finishClient();
        else {
            if (credentials.username.equals(UserModel.DEFAULT_USERNAME)
                    && !(command instanceof LoginCommand)
                    && !(command instanceof RegisterCommand))
                throw new AuthorizationException("The default user can not execute special commands, please login");

            checkForInputs(command);
            channel.sendCommand(new CommandPacket(command, credentials));
        }
    }

    /**
     * Функция для проверки, нужны ли еще входные данные для отправки команды
     * @param command - команда
     */
    public void checkForInputs(Command command) {
        if (command.requireInput() == ICommand.TYPE_INPUT_ORGANIZATION) {
            Organization organization = factory.generateOrganizationByInput(userInputHandler);
            command.addInput(organization);
        } else if (command.requireInput() == ICommand.TYPE_INPUT_CREDENTIAL) {
            Credentials credentials = CredentialFactory.getInstance().generateCredentialByInput(userInputHandler);
            command.addInput(credentials);
        }
    }

    /**
     * Функция для отключения клиента
     */
    public void finishClient() {
        System.out.println("Good bye!");
        LOG.info("Finishing client");
        channel.disconnect();
        System.exit(0);
    }
}