package max;

import max.command.ExecutionContext;
import max.command.ExecutionContextImpl;
import max.database.CollectionModel;
import max.database.DBRequestManager;
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
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

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
            LOG.error("Port isn't provided");
            System.exit(-1);
        } catch (IllegalArgumentException ex) {
            LOG.error("The provided port is out of the available range: " + args[0], ex);
            System.exit(-1);
        }

        /*
         * database config
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
            final DBRequestManager controller = new DBRequestManager(collectionModel, userModel);

            final FileManager fileManager = new FileManager();
            final CollectionManager collectionManager = new CollectionManager(controller.fetchCollectionFromDB());
            final ResourceBundle bundle = ResourceBundle.getBundle("bundles.LangBundle", new Locale("en"));
            final ExecutionContext executionContext = new ExecutionContextImpl(collectionManager, controller, fileManager, bundle);

            final ServerRequestHandler requestManager = new ServerRequestHandler(socket, executionContext);

            if (socket.getSocket().isBound()) {
                LOG.info("Socket Successfully opened on " + address);
            }
            else {
                LOG.error("Strange behaviour trying to bind the server");
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
            LOG.error("Several Issues: " + ex.getMessage() + "\n",ex);
        } catch (NoSuchElementException ex) {
            LOG.error("You wrote something strange",ex);
        } catch (JAXBException ex) {
            LOG.error("Error initialing the Parser", ex);
        }
    }
}