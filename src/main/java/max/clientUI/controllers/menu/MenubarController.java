package max.clientUI.controllers.menu;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import max.clientUI.controllers.LoginRegisterController;
import max.clientUI.controllers.MainController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenubarController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(MenubarController.class);

    @FXML
    private Label currentUserLabel;
    @FXML private ToggleGroup languageOptions;
    private ResourceBundle bundle;

    private final MainController mainController;

    public MenubarController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;

        languageOptions.selectedToggleProperty().addListener(switchLanguageListener());

        currentUserLabel.setText(bundle.getString("menubar.current.user.title") + ": " + mainController.getContext().responseHandler().getCurrentUser().getCredentials().username);
    }

    private ChangeListener<Toggle> switchLanguageListener() {
        return (ov, old_toggle, new_toggle) -> {
            if (languageOptions.getSelectedToggle() != null) {
                String selectedID = ((RadioMenuItem) languageOptions.getSelectedToggle()).getId();
                mainController.switchLanguage(selectedID);
            }
        };
    }

    @FXML
    public void handleMenuLogOut(ActionEvent actionEvent) {
        try {
            mainController.closeWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login_register.fxml"));
            loader.setController(new LoginRegisterController(mainController.getContext()));
            loader.setResources(bundle);
            Parent root = loader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(bundle.getString("login.window.title"));
            stage.setScene(new Scene(root));
            stage.show();
            LOG.info("Logged out!");
        } catch (IOException ex) {
            LOG.error("Error logging out!", ex);
        }
    }

    @FXML
    public void handleMenuAddOrganization(ActionEvent actionEvent) {
        mainController.loadEditOrganizationDialog(null, false, false);
    }

    @FXML
    public void handleAboutMenu(ActionEvent actionEvent) {

    }
}