package sepm.dsa.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;

public class MainMenuController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu dateiMenu;
    @FXML
    private MenuItem dateiImport;
    @FXML
    private MenuItem dateiExport;
    @FXML
    private MenuItem dateiExit;
    @FXML
    private Menu dateiVerwaltenMenu;
    @FXML
    private MenuItem verwaltenHaendlerKategorie;
    @FXML
    private MenuItem verwaltenGebieteGrenzen;
    @FXML
    private MenuItem verwaltenWaehrungen;
    @FXML
    private MenuItem verwaltenWaren;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {

    }

    @FXML
    private void onGrenzenGebieteClicked() {
        log.debug("onGrenzenGebieteClicked - open Grenzen und Gebiete Window");
        Stage stage = new Stage();
        Parent scene = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        scene = (Parent) loader.load("/gui/regionlist.fxml");

        stage.setTitle("Grenzen und Gebiete");
        stage.setScene(new Scene(scene, 600, 438));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void onExitClicked() {
        Platform.exit();
    }
}
