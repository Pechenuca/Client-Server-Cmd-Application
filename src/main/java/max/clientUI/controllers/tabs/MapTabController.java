package max.clientUI.controllers.tabs;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import max.clientUI.AlertMaker;
import max.clientUI.canvas.AbsResizableCanvas;
import max.clientUI.canvas.ResizableMapCanvas;
import max.clientUI.canvas.ResizableOrganizationPictureCanvas;
import max.clientUI.controllers.MainController;
import max.coreSources.OrganizationType;
import max.util.FxUtils;
import max.util.OrganizationEntrySerializable;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Field;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MapTabController implements Initializable {

    private static final Logger LOG = (Logger) LogManager.getLogger(MainTabController.class);

    @FXML
    public Pane wrapperMapPane, organizationPicturePane;
    @FXML public JFXButton editOrganizationButton, removeOrganizationButton;
    @FXML public GridPane organizationDetailsGrid;
    private AbsResizableCanvas organizationsMapCanvas;
    private AbsResizableCanvas organizationPictureCanvas;
    private ResourceBundle bundle;

    private final MainController mainController;
    private final ArrayList<OrganizationEntrySerializable> organizationsList = new ArrayList<>();
    private OrganizationEntrySerializable selectedOrganization = null;

    public MapTabController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;

        // Map canvas init
        organizationsMapCanvas = new ResizableMapCanvas(organizationsList, wrapperMapPane);
        wrapperMapPane.getChildren().add(organizationsMapCanvas);
        organizationsMapCanvas.widthProperty().bind(wrapperMapPane.widthProperty());
        organizationsMapCanvas.heightProperty().bind(wrapperMapPane.heightProperty());
        organizationsMapCanvas.setOnMouseClicked(event -> {
            selectedOrganization = (OrganizationEntrySerializable)organizationsMapCanvas.findObj(event.getSceneX(), event.getSceneY());
            handleDetailOrganization();
        });

        // organization canvas picture init
        organizationPictureCanvas = new ResizableOrganizationPictureCanvas();
        organizationPicturePane.getChildren().add(organizationPictureCanvas);
        organizationPictureCanvas.widthProperty().bind(organizationPicturePane.widthProperty());
        organizationPictureCanvas.heightProperty().bind(organizationPicturePane.heightProperty());

        refreshData();
    }

    public void refreshData() {
        if (needAddOrganization() && organizationsList.size() > 0)
            checkForNewOrganization();
        else {
            organizationsList.clear();
            organizationsList.addAll(mainController.getContext().localCollection().getLocalList());
            organizationsMapCanvas.setObj(mainController.getContext().localCollection().getLocalList());
            organizationsMapCanvas.draw();
        }
        if (selectedOrganization != null)
            updateOrganizationDetails();
    }

    public boolean needAddOrganization() {
        return organizationsList.size() < mainController.getContext().localCollection().getLocalList().size();
    }

    public void checkForNewOrganization() {
        List<OrganizationEntrySerializable> diff = new ArrayList<>();
        outer: for (OrganizationEntrySerializable fetched : mainController.getContext().localCollection().getLocalList()) {
            for (OrganizationEntrySerializable elemMap : organizationsList)
                if (fetched.equals(elemMap))
                    continue outer;
            diff.add(fetched);
        }

        for (OrganizationEntrySerializable newElem : diff) {
            System.out.println(newElem.toString());
            organizationsList.add(newElem);
            ((List<OrganizationEntrySerializable>)  organizationsMapCanvas.getObj()).add(newElem);
            organizationsMapCanvas.animateEntry(newElem);
        }
    }

    public void updateOrganizationDetails() {
        int oldSelectedOrganizationID = selectedOrganization.getOrganization().getId();
        selectedOrganization = mainController.getContext().localCollection().getByID(oldSelectedOrganizationID);
        if (selectedOrganization != null)
            handleDetailOrganization();
        else {
            organizationDetailsGrid.getChildren().clear();
            organizationPictureCanvas.setObj(null);
            organizationPictureCanvas.draw();
        }
    }

    public void handleDetailOrganization() {
        if (selectedOrganization == null)
            return;

        changeStatusActionButtons(selectedOrganization.getOrganization().getUserID());

        try {
            loadingOrganizationPicture();
            loadingOrganizationFields();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            AlertMaker.showErrorMessage(bundle.getString("tab.map.error.fetching"), ex.getMessage());
        }
    }

    private void changeStatusActionButtons(int userID) {
        int currentUserID = mainController.getContext().responseHandler().getCurrentUser().getCredentials().id;
        if (currentUserID != 1 && currentUserID != userID) {
            editOrganizationButton.setDisable(true);
            removeOrganizationButton.setDisable(true);
        } else {
            editOrganizationButton.setDisable(false);
            removeOrganizationButton.setDisable(false);
        }
    }

    private void loadingOrganizationPicture() {
        organizationPictureCanvas.setObj(selectedOrganization.getOrganization());
        organizationPictureCanvas.draw();

        //TODO: OPTIMIZE BACKGROUND PAINTING
       OrganizationType type = selectedOrganization.getType();
        if (type.equals(OrganizationType.GOVERNMENT)) {
            organizationPicturePane.setStyle("-fx-background-color: " + FxUtils.toHexString(Color.PERU));
        }
        else if (type.equals(OrganizationType.TRUST)) {
            organizationPicturePane.setStyle("-fx-background-color: " + FxUtils.toHexString(Color.AQUA));
        }
        else if (type.equals(OrganizationType.COMMERCIAL)){
            organizationPicturePane.setStyle("-fx-background-color: " + FxUtils.toHexString(Color.LIGHTGRAY));
        }
        else if (type.equals(OrganizationType.PRIVATE_LIMITED_COMPANY)) {
            organizationPicturePane.setStyle("-fx-background-color: " + FxUtils.toHexString(Color.MAROON));
        }
        else if (type.equals(OrganizationType.PUBLIC)) {
            organizationPicturePane.setStyle("-fx-background-color: " + FxUtils.toHexString(Color.MAROON));
        }
        organizationPictureCanvas.animateEntry(selectedOrganization);
    }

    private void loadingOrganizationFields() throws IllegalAccessException {
        organizationDetailsGrid.getChildren().clear();
        int x = 0, y = 0;
        for (Field organizationField: selectedOrganization.getOrganization().getClass().getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(organizationField.getModifiers()))
                continue;

            organizationField.setAccessible(true);
            String title = bundle.getString("tab.main.table.col." + organizationField.getName());
            String content = organizationField.get(selectedOrganization.getOrganization()).toString();
            if (organizationField.getName().equalsIgnoreCase("creationDate"))
                content = FxUtils.formatZonedDateTimeValue((ZonedDateTime) organizationField.get(selectedOrganization.getOrganization()), bundle.getLocale());
            final Label attrTemp = new Label(title + ": " + content);
            attrTemp.getStyleClass().add("detail-organization");
            if (x == 2) {
                y++;
                x = 0;
            }
            organizationDetailsGrid.add(attrTemp, x, y);  //x is column index and 0 is row index
            x++;
        }
    }

    @FXML
    public void handleEditOrganizationButtonAction(ActionEvent actionEvent) {
        mainController.loadEditOrganizationDialog(selectedOrganization, true, false);
    }

    @FXML
    public void handleRemoveOrganizationButtonAction(ActionEvent actionEvent) {
        mainController.loadRemoveOrganizationDialog(selectedOrganization);
    }

    private static class Logger {
    }
}