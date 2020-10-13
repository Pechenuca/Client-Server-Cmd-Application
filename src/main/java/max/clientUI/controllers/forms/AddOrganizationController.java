package max.clientUI.controllers.forms;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import max.clientUI.AlertMaker;
import max.clientUI.ClientContext;
import max.command.Command;
import max.coreSources.Address;
import max.coreSources.Coordinates;
import max.coreSources.Organization;
import max.coreSources.OrganizationType;
import max.network.CommandPacket;
import max.util.OrganizationEntrySerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.net.URL;
import java.util.ResourceBundle;

public class AddOrganizationController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(AddOrganizationController.class);

    public interface Func<T, V> {
        boolean compare(T t, V v);
    }

    @FXML
    public StackPane rootPane;
    @FXML public Label idLabel;
    @FXML public JFXTextField keyTextField;
    @FXML public JFXTextField nameTextField;
    @FXML public JFXTextField coordinateX;
    @FXML public JFXTextField coordinateY;
    @FXML public JFXTextField annualTurnoverTextField;
    @FXML public JFXComboBox<Label> typeBox;
    @FXML public JFXComboBox<Label> officialAddressBox;
    @FXML public JFXTextField fullNameTextField;
    private ResourceBundle bundle;

    private final ClientContext clientContext;
    private boolean editMode = false;
    private int editingID = -1;

    public AddOrganizationController(ClientContext clientContext, boolean editMode) {
        this.clientContext = clientContext;
        this.editMode = editMode;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;

        for (Address address: Address.values())
            officialAddressBox.getItems().add(new Label(address.name()));

        for (OrganizationType type: OrganizationType.values())
            typeBox.getItems().add(new Label(type.name()));


        initValidators();
    }


    @FXML
    private void cancelOperation(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }


    public boolean validationGetsError() {
        return !(keyTextField.validate() && nameTextField.validate()
                && coordinateX.validate() && annualTurnoverTextField.validate()
                && coordinateY.validate() && officialAddressBox.validate()
                && typeBox.validate() && typeBox.validate() && fullNameTextField.validate());
    }


    @FXML
    public void addOrganization(ActionEvent actionEvent) {
        if (validationGetsError()) {
            return;
        }

        try {

            Organization organization = new Organization(
                    editingID,
                    clientContext.responseHandler().getCurrentUser().getCredentials().id,
                    nameTextField.getText(),
                    new Coordinates(Long.parseLong(coordinateX.getText()), Float.parseFloat(coordinateY.getText())),
                    Long.parseLong(annualTurnoverTextField.getText()),
                    fullNameTextField.getText(),
                    //Color.valueOf(colorBox.getSelectionModel().getSelectedItem().getText()),
                    OrganizationType.valueOf(typeBox.getSelectionModel().getSelectedItem().getText()),
                    Address.valueOf(officialAddressBox.getSelectionModel().getSelectedItem().getText()));

            if (editMode) {
                sendRequest(organization, organization.getId().toString(), "update");
                editMode = false;
            } else
                sendRequest(organization, keyTextField.getText(), "insert");
        } catch (NumberFormatException ex) {
            AlertMaker.showErrorMessage(bundle.getString("tab.main.alert.validation.error.title"), bundle.getString("tab.main.alert.validation.error.content"));
        }
    }


    public void sendRequest(Organization input, String arg, String commandKey) {
        Command command = clientContext.commandManager().getCommand(commandKey);
        command.setArgs(new String[]{arg});
        command.addInput(input);

        clientContext.clientChannel().sendCommand(new CommandPacket(command, clientContext.responseHandler().getCurrentUser().getCredentials(), bundle.getLocale()));

        Object response = clientContext.responseHandler().checkForResponse();

        if (response instanceof String) {
            AlertMaker.showSimpleAlert(bundle.getString("dashboard.alert.request.result"), (String)response);
            clientContext.responseHandler().setReceivedObjectToNull();
            cleanEntries();
            cancelOperation(new ActionEvent());
            LOG.info("Result of the insert/update process: {}", (String) response);
        }
    }


    public void cleanEntries() {
        idLabel.setText("ID = ?");
        keyTextField.setText("");
        nameTextField.setText("");
        coordinateX.setText("");
        coordinateY.setText("");
        annualTurnoverTextField.setText("");
        typeBox.getSelectionModel().clearSelection();
        officialAddressBox.getSelectionModel().clearSelection();
        fullNameTextField.setText("");
    }


    public void inflateUI(OrganizationEntrySerializable organization) {
        if (organization.getOrganization() == null) {
            keyTextField.setText(String.valueOf(organization.getKey()));
            keyTextField.setEditable(false);
            return;
        }

        idLabel.setText("ID= " + organization.getOrganization().getId().toString());
        editingID = organization.getOrganization().getId();
        keyTextField.setText(String.valueOf(organization.getKey()));
        nameTextField.setText(organization.getOrganization().getName());
        coordinateX.setText(organization.getOrganization().getCoordinates().getX().toString());
        coordinateY.setText(organization.getOrganization().getCoordinates().getY().toString());
        annualTurnoverTextField.setText(organization.getOrganization().getAnnualTurnover().toString());

        autoSelectComboBoxValue(typeBox, organization.getOrganization().getType(), (type, typeBoxVal) -> type.equals(Enum.valueOf(OrganizationType.class, typeBoxVal)));
        autoSelectComboBoxValue(officialAddressBox, organization.getOrganization().getOfficialAddress(), (officialAddress, addressBoxVal) -> officialAddress.equals(Enum.valueOf(Address.class, addressBoxVal)));
        keyTextField.setEditable(false);
    }

    public <T> void autoSelectComboBoxValue(JFXComboBox<Label> comboBox, T value, Func<T, String> f) {
        for (Label t : comboBox.getItems())
            if (f.compare(value, t.getText()))
                comboBox.setValue(t);
    }


    public void initValidators() {
        NumberValidator numValidator = new NumberValidator(bundle.getString("form.add.validation.format.msg"));
        RequiredFieldValidator requiredValidator = new RequiredFieldValidator(bundle.getString("form.add.validation.required.msg"));
        RegexValidator annualTurnoverValidator = new RegexValidator(bundle.getString("form.add.validation.annualTurnover.msg"));
        annualTurnoverValidator.setRegexPattern("^[1-9]\\d*$");
        RegexValidator coordXValidator = new RegexValidator(bundle.getString("form.add.validation.coord.x.msg"));
        coordXValidator.setRegexPattern("^-(32[0-7]|3[0-1]\\d|[1-2]\\d\\d|\\d{1,2})|\\d+$");

        keyTextField.getValidators().addAll(numValidator, requiredValidator);
        nameTextField.getValidators().addAll(requiredValidator);
        coordinateX.getValidators().addAll(numValidator, requiredValidator, coordXValidator);
        coordinateY.getValidators().addAll(numValidator, requiredValidator);
        annualTurnoverTextField.getValidators().addAll(numValidator, requiredValidator, annualTurnoverValidator);
        typeBox.getValidators().addAll(requiredValidator);
        officialAddressBox.getValidators().addAll(requiredValidator);
        fullNameTextField.getValidators().addAll(numValidator);
    }

}