package javaProject;

import javaProject.command.ExecutionContext;
import javaProject.network.ServerRequestHandler;
import javaProject.network.ServerSocket;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;

import java.io.IOException;
import java.net.InetSocketAddress;

import java.net.SocketException;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

public class ServerMain {
    private static final Logger LOG = LogManager.getLogger(ServerMain.class);

    public static void main(String[] args) {
        InetSocketAddress address = null;
        ServerSocket socket = null;
        try {
            final int port = Integer.parseInt(args[0]);
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
            socket = new ServerSocket(address);

            final FileManager fileManager = new FileManager(Paths.get(args[1]).toAbsolutePath().toString());
            final CollectionManager collectionManager = new CollectionManager(fileManager.getCollectionFromFile());
            StringBuilder stringBuilder = new StringBuilder();
            final ExecutionContext executionContext = new ExecutionContext() {
                @Override
                public CollectionManager collectionManager() {
                    return collectionManager;
                }

                @Override
                public FileManager fileManager() {
                    return fileManager;
                }

                @Override
                public StringBuilder result() {
                    return stringBuilder;
                }
            };
            final ServerRequestHandler requestManager = new ServerRequestHandler(socket, executionContext);

            if (socket.getSocket().isBound()) {
                LOG.info("Socket Successfully opened on " + address);
                System.out.println("Socket Successfully opened on " + address);
            } else {
                LOG.error("Strange behaviour trying to bind the server");
                System.err.println("Strange behaviour trying to bind the server");
                System.exit(-1);
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    requestManager.disconnect();
                    executionContext.fileManager().SaveCollectionInXML(
                            executionContext.collectionManager().getCollection());
                    System.out.println("All elements of collection saved into the file!");
                } catch (JAXBException | IOException e) {
                    System.err.println("problem saving the collection in file, check logs");
                    LOG.error("problem saving the collection in file", e);
                }
            }));

            while (socket.getSocket().isBound()) {
                requestManager.receiveFromWherever();
            }

        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (ArrayIndexOutOfBoundsException | InvalidPathException | SecurityException ex) {
            System.err.println("Invalid file's path or/and security problem trying to access it");
            LOG.error("Invalid file's path or/and security problem trying to access it", ex);
        } catch (JAXBException ex) {
            System.err.println("Problem processing the data from/into the file: " + ex.getMessage());
            LOG.error("Problem processing the data from/into the file", ex);
        } catch (IOException ex) {
            System.err.println("I/O problems: " + ex.getMessage());
            LOG.error("I/O problems", ex);
        } catch (NoSuchElementException ex) {
            System.err.println("You wrote something strange");
            LOG.error("You wrote something strange", ex);
        }
    }
}
