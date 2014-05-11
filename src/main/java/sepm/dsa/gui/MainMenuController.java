package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainMenuController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu dateiMenu;
    @FXML
    private MenuItem dateiImportExport;
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
    public void initialize (java.net.URL location, java.util.ResourceBundle resources) {

    }

    @FXML
    private void onGrenzenGebieteClicked() {
        Stage details = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("regionlist.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        details.setTitle("Grenzen und Gebiete");
        details.setScene(new Scene(root, 600, 438));
        details.show();

    }
}
