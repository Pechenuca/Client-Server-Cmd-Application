package max;

import max.command.ExecutionContext;
import max.database.CollectionModel;
import max.database.DatabaseConfigurer;
import max.database.UserModel;
import max.managers.CollectionManager;
import max.managers.FileManager;
import max.network.ServerRequestHandler;
import max.network.ServerSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class ServerMain {

    private static final Logger LOG = LogManager.getLogger(ServerMain.class);

    public static void main(String[] args) {
        /*
         * port and server config
         * */
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

        /*
         * max.database config
         * */
        final DatabaseConfigurer dbConfigurer = new DatabaseConfigurer();
        if (dbConfigurer.needReadProperties())
            dbConfigurer.readCustomProperties();
        else
            dbConfigurer.loadProperties();
        dbConfigurer.setConnection();

        /*
         * receiver and data handler config
         * */
        try {
            socket = new ServerSocket(address);

            final CollectionModel collectionModel = new CollectionModel(dbConfigurer.getDbConnection());
            final UserModel userModel = new UserModel(dbConfigurer.getDbConnection());
            final CollectionController controller = new CollectionController(collectionModel, userModel);

            final FileManager fileManager = new FileManager();
            final CollectionManager collectionManager = new CollectionManager(controller.fetchCollectionFromDB());

            final ExecutionContext executionContext = new ExecutionContext() {
                @Override
                public CollectionManager collectionManager() {
                    return collectionManager;
                }
                @Override
                public CollectionController collectionController() {
                    return controller;
                }
                @Override
                public FileManager fileManager() {
                    return fileManager;
                }

            };
            final ServerRequestHandler requestManager = new ServerRequestHandler(socket, executionContext);

            if (socket.getSocket().isBound()) {
                LOG.info("Socket Successfully opened on " + address);
                System.out.println("Socket Successfully opened on " + address);
            }
            else {
                LOG.error("Strange behaviour trying to bind the server");
                System.err.println("Strange behaviour trying to bind the server");
                System.exit(-1);
            }

            //create shutdown hook with anonymous implementation
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                requestManager.disconnect();
                dbConfigurer.disconnect();
            }));

            requestManager.receiveFromWherever();

            while (socket.getSocket().isBound()) {
            }

        } catch (IOException | SQLException ex) {
            System.err.println("Problems: " + ex.getMessage() + "\nCheck logs for details");
            LOG.error("Severe Issue",ex);
        } catch (NoSuchElementException ex) {
            System.err.println("You wrote something strange");
            LOG.error("You wrote something strange",ex);
        } catch (JAXBException ex) {
            System.err.println("Error initialing the Parser");
            LOG.error("Error initialing the Parser", ex);
        }
    }
}