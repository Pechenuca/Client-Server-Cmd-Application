package javaProject;



import javaProject.exception.NoSuchCommandException;
import javaProject.network.ClientChannel;
import javaProject.network.CommandReader;
import javaProject.util.IHandlerInput;
import javaProject.util.UserInputHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.xml.bind.JAXBException;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
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

        IHandlerInput userInputHandler = (IHandlerInput) new UserInputHandler(true);
        CommandManager manager = new CommandManager();
        CommandReader reader = new CommandReader(channel, manager, userInputHandler);

        while (true) {
            try {
                if (channel.isConnected())
                    reader.startInteraction();
                else
                    channel.tryToConnect(address);

                final long start = System.currentTimeMillis();
                while (channel.requestWasSent()) {
                    Object received = channel.receiveData();

                    if (received instanceof String) {
                        if (received.equals("connect")) {
                            channel.setConnected(true);
                            LOG.info("Successfully connected to the server");
                            System.out.println("Successfully connected to the server");
                        }
                    }

                    if (received != null)
                        channel.printObj(received);

                    if (channel.requestWasSent() && System.currentTimeMillis() - start > 1000) {
                        channel.setConnectionToFalse();
                        break;
                    }
                }
            } catch (NoSuchCommandException ex) {
                System.out.println(ex.getMessage());
            } catch (NoSuchElementException ex) {
                reader.finishClient();
            } catch (ClosedChannelException ignored) {
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.err.println("No argument passed");
            } catch (EOFException ex) {
                System.err.println("Reached limit of data to receive");
                LOG.error("Reached Limit", ex);
            } catch (IOException | ClassNotFoundException | JAXBException e) {
                System.err.println("I/O Problems, check logs");
                LOG.error("I/O Problems", e);
            }
        }
    }
}