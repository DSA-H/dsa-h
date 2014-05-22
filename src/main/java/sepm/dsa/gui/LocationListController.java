package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TraderService;

@Service("LocationListController")
public class LocationListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(LocationListController.class);
    private SpringFxmlLoader loader;

    private LocationService locationService;

    private TraderService traderService;

    @FXML
    private TableView<Location> locationTable;
    @FXML
    private TableColumn nameColumn;
    @FXML
    private TableColumn regionColumn;
    @FXML
    private Button createButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize LocationListController");

        // init table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
        locationTable.setItems(data);
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateButtonPressed - open Location-Details Window");

        EditLocationController.setLocation(null);

        Stage stage = (Stage) locationTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/editlocation.fxml");

        stage.setTitle("Ort erstellen");
        stage.setScene(new Scene(root, 900, 438));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onEditButtonPressed - open Location-Details Window");

        //TODO schön machen
        Location selectedLocation = locationTable.getFocusModel().getFocusedItem();
        EditLocationController.setLocation(selectedLocation);

        Stage stage = (Stage) locationTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/editlocation.fxml");

        stage.setTitle("Ort bearbeiten");
        stage.setScene(new Scene(root, 900, 438));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Location");
        Location selectedLocation = locationTable.getFocusModel().getFocusedItem();

        if (selectedLocation != null) {
            log.debug("open Confirm-Delete-Location Dialog");
            int traderSize = traderService.getAllForLocation(selectedLocation).size();
            int tavernsSize = 0;    // TODO get taverns connected to location
            String connectedEntries = "";
            connectedEntries += "\n" + traderSize + " Händler";
            connectedEntries += "\n" + tavernsSize + " Wirtshäuser";
            // TODO ? make a 'DeleteService' to providing information of all entities, which connected entities exist

            Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie den Ort '" + selectedLocation.getName() + "' wirklich löschen? Folgende verbundenden Einträge würden ebenfalls gelöscht werden:" + connectedEntries)
                    .showConfirm(); // TODO was ist hier sinnvoll?
            if (response == Dialog.Actions.YES) {
                locationService.remove(selectedLocation);
                locationTable.getItems().remove(selectedLocation);
            }
        }

        checkFocus();
    }

    @FXML
    private void checkFocus() {
        Location selectedLocation = locationTable.getFocusModel().getFocusedItem();
        if (selectedLocation == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }

    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }
}
