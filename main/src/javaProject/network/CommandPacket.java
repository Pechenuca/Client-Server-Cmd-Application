package javaProject.network;

import javaProject.command.Command;
import javaProject.database.Credentials;

import java.io.Serializable;

public class CommandPacket implements Serializable {

    private static final long serialVersionUID = -860971330126223957L;

    private final Command command;
    private final Credentials credentials;

    public CommandPacket(Command command, Credentials credentials) {
        this.command = command;
        this.credentials = credentials;
    }
    public Credentials getCredentials() {
        return credentials;
    }
    public Command getCommand() {
        return command;
    }
}