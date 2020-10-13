package max.clientUI.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginComponentController implements Initializable {

    @FXML public JFXTextField username;
    @FXML public JFXPasswordField password;
    @FXML public ToggleGroup languageOptions;
    private ResourceBundle bundle;

    private final LoginRegisterController loginRegisterController;

    public LoginComponentController(LoginRegisterController loginRegisterController) {
        this.loginRegisterController = loginRegisterController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        languageOptions.selectedToggleProperty().addListener(switchLanguageListener());
    }

    @FXML
    public void handleLoginRegisterButtonAction(ActionEvent actionEvent) {
        String buttonClicked = ((Control)actionEvent.getSource()).getId();
        loginRegisterController.makeUserRequest(buttonClicked);
    }

    private ChangeListener<Toggle> switchLanguageListener() {
        return (ov, old_toggle, new_toggle) -> {
            if (languageOptions.getSelectedToggle() != null) {
                String selectedID = ((RadioMenuItem) languageOptions.getSelectedToggle()).getId();
                loginRegisterController.switchLanguage(selectedID);
            }
        };
    }

    public JFXTextField getUsername() {
        return username;
    }

    public JFXPasswordField getPassword() {
        return password;
    }
}