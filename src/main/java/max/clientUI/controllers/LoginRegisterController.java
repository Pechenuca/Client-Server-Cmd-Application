package max.clientUI.controllers;


import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import max.clientUI.AlertMaker;
import max.clientUI.ClientContext;
import max.clientUI.DashboardLoader;
import max.command.Command;
import max.database.Credentials;
import max.network.CommandPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginRegisterController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(LoginRegisterController.class);

    @FXML public AnchorPane rootAnchorPane;
    @FXML public JFXTextField username;
    @FXML public JFXPasswordField password;
    private ResourceBundle bundle;

    private final ClientContext clientContext;

    public LoginRegisterController(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        loadComponents();
    }

    public void loadComponents() {
        try {
            LoginComponentController controller = new LoginComponentController(this);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login_form_menu.fxml"));
            loader.setController(controller);
            loader.setResources(bundle);
            Parent menuRoot = loader.load();
            rootAnchorPane.getChildren().setAll(menuRoot);
            username = controller.getUsername();
            password = controller.getPassword();
        } catch (IOException e) {
            LOG.error("reloading pane while changing language", e);
        }
    }

    public void makeUserRequest(String buttonClicked) {
        String usernameText = username.getText();
        String passwordText = password.getText();
        Command command = clientContext.commandManager().getCommand(buttonClicked);
        command.addInput(new Credentials(-1, usernameText, passwordText));

        clientContext.clientChannel().sendCommand(new CommandPacket(command, clientContext.responseHandler().getCurrentUser().getCredentials(), bundle.getLocale()));

        Object response = clientContext.responseHandler().checkForResponse();

        if (response instanceof Credentials) {
            closeStage();
            clientContext.responseHandler().setCurrentUser((Credentials) response);
            LOG.info("User successfully logged in {}", clientContext.responseHandler().getCurrentUser().getCredentials().username);
            clientContext.responseHandler().setReceivedObjectToNull();
            loadMain();
        } else {
            setWrongCredentialsStyle();
            AlertMaker.showErrorMessage(bundle.getString("login.alert.error"), (String)response);
        }
    }

    public void setWrongCredentialsStyle() {
        username.getStyleClass().add("wrong-credentials");
        password.getStyleClass().add("wrong-credentials");
    }

    private void closeStage() {
        ((Stage) username.getScene().getWindow()).close();
    }

    void loadMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main_layout.fxml"));
            loader.setController(new DashboardLoader(new MainController(clientContext)));
            loader.setResources(bundle);
            Parent parent = loader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(bundle.getString("dashboard.window.title"));
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            LOG.error("Error loading the main dashboard", ex);
        }
    }

    public void switchLanguage(String languageCode)  {
        Locale locale = Locale.forLanguageTag(languageCode);
        bundle = ResourceBundle.getBundle("bundles.LangBundle", locale);
        loadComponents();
    }

}