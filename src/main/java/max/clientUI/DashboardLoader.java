package max.clientUI;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import max.clientUI.controllers.MainController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardLoader implements Initializable {

    @FXML
    private StackPane rootLayoutPane;
    private ResourceBundle bundle;
    private final MainController mainController;

    public DashboardLoader(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        mainController.setBundle(bundle);
        final Runnable fetchData = mainController::refreshLocalCollection;
        loadSplashScreen();

        new Thread(fetchData).start();
    }

    public void loadSplashScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/splash_screen.fxml"));
            loader.setResources(bundle);
            StackPane splashScreenPane = loader.load();
            rootLayoutPane.getChildren().setAll(splashScreenPane);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), splashScreenPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);
            fadeIn.play();

            fadeIn.setOnFinished(e -> loadMainScreen());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMainScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/dashboard.fxml"));
            loader.setController(mainController);
            loader.setResources(bundle);
            Parent dashboardPane = loader.load();
            rootLayoutPane.getChildren().setAll(dashboardPane);
        } catch (IOException ex) {
            //LOG.error("Error loading the main dashboard", ex);
        }
    }
}