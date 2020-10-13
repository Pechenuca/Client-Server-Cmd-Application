package max;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import max.clientUI.ClientContext;
import max.clientUI.LocalCollectionManager;
import max.clientUI.controllers.LoginRegisterController;
import max.database.Credentials;
import max.database.CurrentUser;
import max.managers.CommandManager;
import max.network.ClientChannel;
import max.network.ClientResponseHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientUIMain extends Application {

    private static final Logger LOG = LogManager.getLogger(ClientUIMain.class);
    private static ClientContext clientContext;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login_register.fxml"));
        loader.setController(new LoginRegisterController(clientContext));
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.LangBundle", new Locale("en"));
        loader.setResources(bundle);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle(bundle.getString("login.window.title"));
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        initConfig(args);
        launch(args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.log(Level.INFO, "Closing Organizations World on {}", LocalDateTime.now())));
    }


    public static void initConfig(String[] args) {
        InetSocketAddress address = null;
        try {
            final int port = Integer.parseInt(args[0]);
            if (args.length > 1) {
                final String host = args[1];
                address = new InetSocketAddress(host, port);
            }
            address = new InetSocketAddress(port);
        } catch (ArrayIndexOutOfBoundsException ex) {
            LOG.error("Port isn't provided");
            System.exit(-1);
        } catch (IllegalArgumentException ex) {
            LOG.error("The provided port is out of the available range: " + args[0], ex);
            System.exit(-1);
        }

        try {
            final ClientChannel channel = new ClientChannel(address);
            CurrentUser currentUser = new CurrentUser(new Credentials(-1, "default", ""));
            LOG.info("Logged as the 'default' user, please use login command");

            CommandManager manager = new CommandManager();
            ClientResponseHandler responseHandler = new ClientResponseHandler(channel, currentUser);
            LocalCollectionManager collectionManager = new LocalCollectionManager();

            clientContext = new ClientContext() {
                @Override
                public CommandManager commandManager() {
                    return manager;
                }
                @Override
                public ClientChannel clientChannel() {
                    return channel;
                }
                @Override
                public ClientResponseHandler responseHandler() {
                    return responseHandler;
                }
                @Override
                public LocalCollectionManager localCollection() {
                    return collectionManager;
                }
            };

        } catch (IOException ex) {
            LOG.error("Unable to connect to the server", ex);
            System.exit(-1);
        }
    }


}