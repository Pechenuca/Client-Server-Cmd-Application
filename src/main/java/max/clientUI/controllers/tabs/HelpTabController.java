package max.clientUI.controllers.tabs;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import max.clientUI.ClientContext;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HelpTabController implements Initializable {

    ObservableList<Label> commandsList = FXCollections.observableArrayList();

    @FXML
    public JFXTextField inputCommandKey;
    @FXML public JFXListView<Label> commandKeysListView;
    @FXML public Label detailsText;
    private ResourceBundle bundle;

    private final ClientContext clientContext;

    public HelpTabController(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        detailsText.setWrapText(true);

        // init components
        List<Label> commands = clientContext.commandManager().getKeysCommands().stream().map(Label::new).collect(Collectors.toList());
        commandsList.addAll(commands);
        commandKeysListView.setItems(commandsList);

        // Filter commands by name functionality
        FilteredList<Label> filteredData = new FilteredList<>(commandsList, s -> true);
        inputCommandKey.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(command -> {
                if (newVal == null || newVal.length() == 0) {
                    return true;
                }
                String writtenText = newVal.toLowerCase();
                return command.getText().toLowerCase().contains(writtenText);
            });
        });
        SortedList<Label> sortedData = new SortedList<>(filteredData);
        commandKeysListView.setItems(sortedData);

        // print details of the selected command
        commandKeysListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selectedItem = commandKeysListView.getSelectionModel().getSelectedItem().getText();
            String description = bundle.getString("commands.description."+selectedItem);
            detailsText.setText(description);
        });
    }
}