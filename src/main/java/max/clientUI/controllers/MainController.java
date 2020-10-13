package max.clientUI.controllers;

import com.jfoenix.controls.JFXTabPane;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import max.clientUI.AlertMaker;
import max.clientUI.ClientContext;
import max.clientUI.LocalCollectionManager;
import max.clientUI.controllers.forms.AddOrganizationController;
import max.clientUI.controllers.menu.MenubarController;
import max.clientUI.controllers.tabs.HelpTabController;
import max.clientUI.controllers.tabs.MainTabController;
import max.clientUI.controllers.tabs.MapTabController;
import max.command.Command;
import max.database.Credentials;
import max.database.UserModel;
import max.network.CommandPacket;
import max.util.OrganizationEntrySerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;

public class MainController implements Initializable {

    public class CollectionRefresher extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                    refreshInterface();
                } catch (InterruptedException e) {
                    LOG.error("I/O Problems", e);
                }
            }
        }
    }

    private static final Logger LOG = LogManager.getLogger(MainController.class);

    @FXML private StackPane rootLayoutPane;
    @FXML private StackPane menubarPane;
    @FXML private JFXTabPane mainTabPane;
    @FXML private Tab mainTab, mapTab, helpTab;
    private ResourceBundle bundle;

    public final CollectionRefresher refresherThread;
    private final ClientContext clientContext;
    private MenubarController menubarController;
    private MainTabController mainTabController;
    private MapTabController mapTabController;

    public MainController(ClientContext clientContext) {
        this.clientContext = clientContext;
        this.refresherThread = new CollectionRefresher();
        refresherThread.setName("CollectionRefresherThread");
        refresherThread.setPriority(Thread.MIN_PRIORITY);
        refresherThread.setDaemon(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
        loadComponents();
        refresherThread.start();
    }

    public void loadComponents() {
        try {
            menubarController = new MenubarController(this);
            mainTabController = new MainTabController(this);
            mapTabController = new MapTabController(this);

            mainTab.setText(bundle.getString("dashboard.tab.main.title"));
            mapTab.setText(bundle.getString("dashboard.tab.map.title"));
            helpTab.setText(bundle.getString("dashboard.tab.help.title"));

            // Menu loader
            FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/views/menu/menubar.fxml"));
            menuLoader.setController(menubarController);
            menuLoader.setResources(ResourceBundle.getBundle("bundles.LangBundle", bundle.getLocale()));
            Parent menuRoot = menuLoader.load();
            menubarPane.getChildren().addAll(menuRoot);

            // main tab loader
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/views/tabs/main_tab.fxml"));
            mainLoader.setController(mainTabController);
            mainLoader.setResources(ResourceBundle.getBundle("bundles.LangBundle", bundle.getLocale()));
            Parent mainRoot = mainLoader.load();
            mainTab.setContent(mainRoot);

            // map tab loader
            FXMLLoader mapLoader = new FXMLLoader(getClass().getResource("/views/tabs/map_tab.fxml"));
            mapLoader.setController(mapTabController);
            mapLoader.setResources(ResourceBundle.getBundle("bundles.LangBundle", bundle.getLocale()));
            Parent mapRoot = mapLoader.load();
            mapTab.setContent(mapRoot);

            // help tab loader
            FXMLLoader helpLoader = new FXMLLoader(getClass().getResource("/views/tabs/help_tab.fxml"));
            helpLoader.setController(new HelpTabController(clientContext));
            helpLoader.setResources(ResourceBundle.getBundle("bundles.LangBundle", bundle.getLocale()));
            Parent helpRoot = helpLoader.load();
            helpTab.setContent(helpRoot);

            mainTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
                if(newTab == mainTab)
                    mainTabController.refreshData();
                else if (newTab == mapTab)
                    mapTabController.refreshData();
            });
        }
        catch(IOException ex) {
            LOG.error("unable to load tabs", ex);
            System.exit(0);
        }
    }

    public synchronized void refreshInterface() {
        if (refreshLocalCollection()) {
            Platform.runLater(() -> {
                mainTabController.refreshData();
                mapTabController.refreshData();
            });
        }
    }


    public boolean refreshLocalCollection() {
        boolean changeMade = false;
        Command command = clientContext.commandManager().getCommand("show");
        clientContext.clientChannel().sendCommand(new CommandPacket(command, clientContext.responseHandler().getCurrentUser().getCredentials(), bundle.getLocale()));

        Object response = clientContext.responseHandler().checkForResponse();

        if (response instanceof List) {
            List<OrganizationEntrySerializable> receivedList = (List<OrganizationEntrySerializable>) response;
            if (!clientContext.localCollection().equals(new LocalCollectionManager(receivedList))) {
                clientContext.localCollection().getLocalList().clear();
                clientContext.localCollection().getLocalList().addAll(receivedList);
                changeMade = true;
            }

            LOG.info("Successfully fetched collection: {} elements", clientContext.localCollection().getLocalList().size());
            clientContext.responseHandler().setReceivedObjectToNull();
        }

        return changeMade;
    }


    /**
     *
     * If the button is pressed from the main window command section, gets a OrganizationEntrySerializable with the
     * key set but the organization null and editMode in false, so the checks are to fill the received key in the form.
     * When is trying to insert receives the OrganizationEntrySerializable totally null and ediMode is false
     *
     * @param selectedForEdit organization to edit
     * @param editMode trying to update the organization
     * @param passingKey if comes from the input where the key is passed
     */
    public void loadEditOrganizationDialog(OrganizationEntrySerializable selectedForEdit, boolean editMode, boolean passingKey) {
        if (selectedForEdit == null && editMode) {
            AlertMaker.showErrorMessage(bundle.getString("dashboard.alert.error.noorganization.selected.title"),
                    bundle.getString("dashboard.alert.error.noorganization.selected.content"));
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/forms/add_organization.fxml"));
            AddOrganizationController controller = new AddOrganizationController(clientContext, editMode);
            loader.setResources(bundle);
            loader.setController(controller);
            Parent parent = loader.load();
            if (selectedForEdit != null)
                if (editMode || passingKey)
                    controller.inflateUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle((editMode) ? bundle.getString("dashboard.alert.edit.title") : bundle.getString("dashboard.alert.insert.title"));
            stage.setScene(new Scene(parent));
            stage.show();

            stage.setOnHiding((e) -> refreshInterface());

        } catch (IOException ex) {
            LOG.error("error trying to edit/insert a organization, ", ex);
        }
    }


    public void loadRemoveOrganizationDialog(OrganizationEntrySerializable selectedForRemove) {
        if (selectedForRemove == null) {
            AlertMaker.showErrorMessage(bundle.getString("dashboard.alert.error.noorganization.selected.title"), bundle.getString("dashboard.alert.error.noorganization.selected.content"));
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(bundle.getString("dashboard.alert.remove.title"));
        String content = MessageFormat.format(bundle.getString("dashboard.alert.remove.content"), selectedForRemove.getId());
        alert.setContentText(content);
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK) {
            String[] args = new String[]{String.valueOf(selectedForRemove.getKey())};
            sendRequest("remove_key", args);
        } else {
            AlertMaker.showSimpleAlert(bundle.getString("dashboard.alert.error.remove.cancelled.title"), bundle.getString("dashboard.alert.error.remove.cancelled.content"));
        }
    }

    public void sendRequest(String commandKey, String[] args) {
        Command command = clientContext.commandManager().getCommand(commandKey);
        command.setArgs(args);
        clientContext.clientChannel().sendCommand(new CommandPacket(command, clientContext.responseHandler().getCurrentUser().getCredentials(), bundle.getLocale()));
        Object response = clientContext.responseHandler().checkForResponse();
        handleResponse(commandKey, response);
    }

    public void handleResponse(String commandKey, Object response) {
        if (response instanceof String) {
            if (commandKey.equals("execute_script")) {
                AlertMaker.showResponseScriptAlert(bundle.getString("tab.main.script.alert.title"), (String)response);
            } else {
                AlertMaker.showSimpleAlert(bundle.getString("dashboard.alert.request.result"), (String) response);
            }
            refreshInterface();
            clientContext.responseHandler().setReceivedObjectToNull();
            LOG.info("Result of the command {}: {}", commandKey, (String) response);
        }
    }


    public ClientContext getContext() {
        return clientContext;
    }

    public void closeWindow() {
        clientContext.responseHandler().setCurrentUser(new Credentials(-1, UserModel.DEFAULT_USERNAME, ""));
        Stage stage = (Stage) mainTab.getTabPane().getScene().getWindow();
        stage.close();
    }

    public void switchLanguage(String languageCode) {
        Locale locale = Locale.forLanguageTag(languageCode);
        bundle = ResourceBundle.getBundle("bundles.LangBundle", locale);
        loadComponents();
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}