package max.network;

import max.command.Command;
import max.database.Credentials;

import java.io.Serializable;
import java.util.Locale;

public class CommandPacket implements Serializable {

    private static final long serialVersionUID = -860971330126223957L;

    private final Command command;
    private final Credentials credentials;
    private final Locale locale;

    public CommandPacket(Command command, Credentials credentials, Locale locale) {
        this.command = command;
        this.credentials = credentials;
        this.locale = locale;
    }

    public Credentials getCredentials() {
        return credentials;
    }
    public Command getCommand() {
        return command;
    }
    public Locale getLocale() {
        return locale;
    }
}