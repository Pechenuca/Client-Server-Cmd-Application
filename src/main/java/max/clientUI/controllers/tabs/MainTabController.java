package max.clientUI.controllers.tabs;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.NumberValidator;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import max.clientUI.AlertMaker;
import max.clientUI.controllers.MainController;
import max.command.Command;
import max.command.ICommand;
import max.coreSources.Address;
import max.coreSources.Coordinates;
import max.coreSources.OrganizationType;
import max.util.FxUtils;
import max.util.OrganizationEntrySerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainTabController implements Initializable {

    private static final Logger LOG = LogManager.getLogger(MainTabController.class);

    private final ObservableList<OrganizationEntrySerializable> organizationsList = FXCollections.observableArrayList();

    @FXML
    public JFXTextField inputOrganizationsSearch;
    @FXML public JFXTextField inputKeyOrganization;
    @FXML public TableView<OrganizationEntrySerializable> organizationsTableView;
    @FXML public TableColumn<OrganizationEntrySerializable, Integer> idCol;
    @FXML public TableColumn<OrganizationEntrySerializable, Integer> keyCol;
    @FXML public TableColumn<OrganizationEntrySerializable, String> nameCol;
    @FXML public TableColumn<OrganizationEntrySerializable, Coordinates> coordinatesCol;
    @FXML public TableColumn<OrganizationEntrySerializable, String> fullNameCol;
    @FXML public TableColumn<OrganizationEntrySerializable, ZonedDateTime> dateCol;
    @FXML public TableColumn<OrganizationEntrySerializable, Long> annualTurnoverCol;

    @FXML public TableColumn<OrganizationEntrySerializable, OrganizationType> typeCol;
    @FXML public TableColumn<OrganizationEntrySerializable, Address> officialAddressCol;
    private ResourceBundle bundle;

    private final MainController mainController;

    public MainTabController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
        initCol();
        refreshData();
        loadFilteringOption();

        inputKeyOrganization.getValidators().addAll(new NumberValidator(), new RequiredFieldValidator());
    }

    public void refreshData() {
        organizationsList.clear();
        organizationsList.addAll(mainController.getContext().localCollection().getLocalList());
        organizationsTableView.setItems(organizationsList);
        loadFilteringOption();
    }

    @FXML
    public void handleRefresh(ActionEvent actionEvent) {
        refreshData();
    }

    private void initCol() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        keyCol.setCellValueFactory(new PropertyValueFactory<>("key"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        coordinatesCol.setCellValueFactory(new PropertyValueFactory<>("coordinates"));
        //dateCol.setCellValueFactory(col -> new SimpleStringProperty(col.getValue().getCreationDate().format(dateFormatter)));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        dateCol.setCellFactory(column -> handleFormatCellDateCreation());
        annualTurnoverCol.setCellValueFactory(new PropertyValueFactory<>("annualTurnover"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        officialAddressCol.setCellValueFactory(new PropertyValueFactory<>("officialAddress"));
    }

    private TableCell<OrganizationEntrySerializable, ZonedDateTime> handleFormatCellDateCreation() {
        return new TableCell<OrganizationEntrySerializable, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if(empty)
                    setText(null);
                else
                    this.setText(FxUtils.formatZonedDateTimeValue(item, bundle.getLocale()));
            }
        };
    }

    public void loadFilteringOption() {
        // Filter commands by name functionality
        FilteredList<OrganizationEntrySerializable> filteredData = new FilteredList<>(organizationsList, s -> true);
        inputOrganizationsSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(organizationEntry -> {
                if (newVal == null || newVal.length() == 0)
                    return true;

                String writtenText = newVal.toLowerCase();
                if (String.valueOf(organizationEntry.getKey()).contains(writtenText))
                    return true;
                else if (String.valueOf(organizationEntry.getId()).contains(writtenText))
                    return true;
                else if (organizationEntry.getName().toLowerCase().contains(writtenText))
                    return true;
                else if (organizationEntry.getColor().toString().toLowerCase().contains(writtenText))
                    return true;
                else
                    return false;
            });
        });
        SortedList<OrganizationEntrySerializable> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(organizationsTableView.comparatorProperty());
        organizationsTableView.setItems(sortedData);
    }

    @FXML
    public void handleOrganizationEdit(ActionEvent actionEvent) {
        // Get selected row
       OrganizationEntrySerializable selectedForEdit = organizationsTableView.getSelectionModel().getSelectedItem();
        mainController.loadEditOrganizationDialog(selectedForEdit, true, false);
    }

    @FXML
    public void handleOrganizationRemove(ActionEvent actionEvent) {
        // Get selected row
        OrganizationEntrySerializable selectedForRemove = organizationsTableView.getSelectionModel().getSelectedItem();
        mainController.loadRemoveOrganizationDialog(selectedForRemove);
    }

    @FXML
    public void handleCommandByKeyButtonAction(ActionEvent actionEvent) {
        String commandCalled = ((Control)actionEvent.getSource()).getId();
        Command command = mainController.getContext().commandManager().getCommand(commandCalled);

        String arg = inputKeyOrganization.getText();
        if (!inputKeyOrganization.validate()) {
            AlertMaker.showSimpleAlert(bundle.getString("tab.main.alert.validation.error.title"), bundle.getString("tab.main.alert.validation.error.content"));
            return;
        }

        if (command.requireInput() == ICommand.TYPE_INPUT_ORGANIZATION) {
            if (commandCalled.equals("update")) {
                OrganizationEntrySerializable selectedToEdit = mainController.getContext().localCollection().getByKey(Integer.parseInt(arg));
                mainController.loadEditOrganizationDialog(selectedToEdit, true, false);
            } else {
                mainController.loadEditOrganizationDialog(new OrganizationEntrySerializable(Integer.parseInt(arg), null), false, true);
            }
            return;
        }

        mainController.sendRequest(commandCalled, new String[]{arg});
        refreshData();
    }

    @FXML
    public void handleRemoveOrganizationButtonAction(ActionEvent actionEvent) {
        String arg = inputKeyOrganization.getText();
        if (!inputKeyOrganization.validate()) {
            AlertMaker.showSimpleAlert(bundle.getString("tab.main.alert.validation.error.title"), bundle.getString("tab.main.alert.validation.error.content"));
            return;
        }

        OrganizationEntrySerializable selectedForRemove = mainController.getContext().localCollection().getByKey(Integer.parseInt(arg));
        mainController.loadRemoveOrganizationDialog(selectedForRemove);
    }

    @FXML
    public void handleCommandWithFileButtonAction(ActionEvent actionEvent) {
        String commandCalled = ((Control)actionEvent.getSource()).getId();
        String title = MessageFormat.format(bundle.getString("dashboard.alert.commandwithfile.content"), commandCalled);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        File selectedFile = fileChooser.showOpenDialog(organizationsTableView.getScene().getWindow());
        if (selectedFile != null) {
            mainController.sendRequest(commandCalled, new String[]{selectedFile.getAbsolutePath()});
        } else {
            AlertMaker.showSimpleAlert(bundle.getString("dashboard.alert.error.remove.cancelled.title"), bundle.getString("dashboard.alert.error.remove.cancelled.content"));
        }
    }

    @FXML
    public void handleInfoButtonAction(ActionEvent actionEvent) {
        mainController.sendRequest("info", null);
    }

    @FXML
    public void handleClearButtonAction(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("dashboard.alert.command.clear.title"));
        alert.setContentText(bundle.getString("dashboard.alert.command.clear.content"));
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK)
            mainController.sendRequest("clear", null);
        else
            AlertMaker.showSimpleAlert(bundle.getString("dashboard.alert.error.remove.cancelled.title"), bundle.getString("dashboard.alert.error.remove.cancelled.content"));
    }

    @FXML
    public void handleExitButtonAction(ActionEvent actionEvent) {
        System.exit(0);
    }
}