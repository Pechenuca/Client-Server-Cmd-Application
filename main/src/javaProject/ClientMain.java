package javaProject;

import javaProject.database.Credentials;
import javaProject.database.CurrentUser;
import javaProject.exception.AuthorizationException;
import javaProject.exception.NoSuchCommandException;
import javaProject.network.ClientChannel;
import javaProject.network.ClientResponseHandler;
import javaProject.network.CommandReader;
import javaProject.util.IHandlerInput;
import javaProject.util.UserInputHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.NoSuchElementException;

public class ClientMain {

    private static final Logger LOG = LogManager.getLogger(ClientMain.class);

    public static void main(String[] args) {
        InetSocketAddress address = null;
        ClientChannel channel = null;
        try {
            final int port = Integer.parseInt(args[0]);
            if (args.length > 1) {
                final String host = args[1];
                address = new InetSocketAddress(host, port);
            }
            address = new InetSocketAddress(port);
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.err.println("Port isn't provided");
            LOG.error("Port isn't provided");
            System.exit(-1);
        } catch (IllegalArgumentException ex) {
            System.err.println("The provided port is out of the available range: " + args[0]);
            LOG.error("The provided port is out of the available range: " + args[0], ex);
            System.exit(-1);
        }

        try {
            channel = new ClientChannel();
        } catch (IOException ex) {
            System.err.println("Unable to connect to the server, check logs for detailed information");
            LOG.error("Unable to connect to the server", ex);
            System.exit(-1);
        }

        CurrentUser currentUser = new CurrentUser(new Credentials(-1, "default", ""));
        System.out.println("Logged as the 'default' user, please use login command");

        IHandlerInput userInputHandler = new UserInputHandler(true);
        CommandManager manager = new CommandManager();
        CommandReader reader = new CommandReader(channel, manager, userInputHandler);
        ClientResponseHandler responseHandler = new ClientResponseHandler(channel, currentUser);

        while(true) {
            try {
                if (channel.isConnected())
                    reader.startInteraction(currentUser.getCredentials());
                else
                    channel.tryToConnect(address);

                responseHandler.checkForResponse();

                final long start = System.currentTimeMillis();
                while (channel.requestWasSent()) {
                    if (channel.requestWasSent() && System.currentTimeMillis() - start > 1000) {
                        System.out.println("Seems the server went down!");
                        channel.setConnectionToFalse();
                        break;
                    }
                }

            } catch (NoSuchCommandException | AuthorizationException ex) {
                System.out.println(ex.getMessage());
            } catch (NoSuchElementException ex) {
                reader.finishClient();
                responseHandler.finishReceiver();
            } catch (ClosedChannelException ignored) {
            }catch (ArrayIndexOutOfBoundsException ex) {
                System.err.println("No argument passed");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("I/O Problems, check logs");
                LOG.error("I/O Problems", e);
            }
        }
    }
}