package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.service.LocationConnectionService;
import sepm.dsa.service.LocationService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

@Service("EditLocationConnectionsController")
public class EditLocationConnectionsController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditLocationConnectionsController.class);

    private SpringFxmlLoader loader;

    private static Location selectedLocation;
    private static List<LocationConnection> locationConnections;

    private LocationConnectionService locationConnectionService;
    private LocationService locationService;

    @FXML
    private TableView<LocationConnection> locationConnectionsTable;

    @FXML
    private TableColumn<LocationConnection, String> connectionToColumn;

    @FXML
    private TableColumn<LocationConnection, Integer> travelTimeColumn;

    @FXML
    private TableView<LocationConnection> suggestLocationConnectionsTable;

    @FXML
    private TableColumn<LocationConnection, String> scConnectedToColumn;

    @FXML
    private TableColumn<LocationConnection, Integer> scTravelTimeColumn;

    @FXML
    private TextField locationNameFilter;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.debug("initialize");
        log.info("--- going to edit location connections for location '" + selectedLocation + "'");

        reloadLocation();

        travelTimeColumn.setCellValueFactory(new PropertyValueFactory<>("travelTime"));
        connectionToColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LocationConnection, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LocationConnection, String> r) {
                if (r.getValue() != null) {
                    String connectedToString = r.getValue().connectedTo(selectedLocation).getName();
                    return new SimpleStringProperty(connectedToString);
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        Set<LocationConnection> allConnections = selectedLocation.getAllConnections();
        ObservableList<LocationConnection> connections = FXCollections.observableArrayList(allConnections);
        locationConnectionsTable.setItems(connections);

        scTravelTimeColumn.setCellValueFactory(new PropertyValueFactory<>("travelTime"));
        scConnectedToColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<LocationConnection, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<LocationConnection, String> r) {
                if (r.getValue() != null) {
                    String connectedToString = r.getValue().connectedTo(selectedLocation).getName();
                    return new SimpleStringProperty(connectedToString);
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });

        // TODO suggest connections click
    }


    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public static void setSelectedLocation(Location selectedLocation) {
        EditLocationConnectionsController.selectedLocation = selectedLocation;
    }

    public void setLocationConnectionService(LocationConnectionService locationConnectionService) {
        this.locationConnectionService = locationConnectionService;
    }

    @FXML
    public void onSuggestConnectionsClicked() {
        log.debug("calling onSuggestConnectionsClicked()");
        List<LocationConnection> suggestedConnections = locationConnectionService.suggestLocationConnectionsAround(selectedLocation, 100.0);
        ObservableList<LocationConnection> connections2 = FXCollections.observableArrayList(suggestedConnections);
        suggestLocationConnectionsTable.setItems(connections2);

    }

    @FXML
    public void onFilterConnectionsClicked() {
        log.debug("calling onFilterConnectionsClicked()");
        List<LocationConnection> suggestedConnections = locationConnectionService.suggestLocationConnectionsByFilter(selectedLocation, locationNameFilter.getText());
        ObservableList<LocationConnection> connections = FXCollections.observableArrayList(suggestedConnections);
        suggestLocationConnectionsTable.setItems(connections);
    }


    @FXML
    public void onEditConnectionClicked() {
        log.debug("calling onEditConnectionClicked()");
        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();
//        selected = locationConnectionService.get(selected.getLocation1(), selected.getLocation2());
        EditLocationConnectionController.setLocationConnection(selected);

        Stage stage = (Stage) locationConnectionsTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/editlocationconnection.fxml");

        stage.setTitle("Reiseverbindung bearbeiten");
        stage.setScene(new Scene(root, 500, 380));
        stage.show();
    }

    @FXML
    public void onRemoveConnectionClicked() {
        log.debug("calling onRemoveConnectionClicked()");
        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();
        locationConnectionService.remove(selected);
        locationConnectionsTable.getItems().remove(selected);
        suggestLocationConnectionsTable.getItems().add(selected);
    }

    @FXML
    public void onApplyAllClicked() {
        log.debug("calling onApplyAllClicked()");

        List<LocationConnection> applied = new ArrayList<>(suggestLocationConnectionsTable.getItems().size());
        for (LocationConnection c : suggestLocationConnectionsTable.getItems()) {
            locationConnectionService.add(c);
            applied.add(c);
        }
        for (LocationConnection c : applied) {
            locationConnectionsTable.getItems().add(c);
            suggestLocationConnectionsTable.getItems().remove(c);
        }
    }

    @FXML
    public void onFinishedClicked() {
        log.debug("calling onFinishedClicked()");

        EditLocationController.setLocation(selectedLocation);

        Stage stage = (Stage) locationConnectionsTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/editlocation.fxml");

        stage.setTitle("Ort erstellen");
        stage.setScene(new Scene(root, 900, 438));
        stage.show();
    }

    private long lastClickedSuggestTable = 0;

    @FXML
    public void onSuggestTableClicked() {
        log.debug("calling onSuggestTableClicked()");

        long newClick = new java.util.Date().getTime();

        if (newClick - lastClickedSuggestTable > 300) {
            lastClickedSuggestTable = newClick;
            return;
        }
        // double click detected
        log.debug("freeBoxesTableClicked - Double click detected! (" + (newClick - lastClickedSuggestTable) + " ms)");
        lastClickedSuggestTable = newClick;

        LocationConnection selected = suggestLocationConnectionsTable.getSelectionModel().getSelectedItem();
        locationConnectionService.add(selected);
        suggestLocationConnectionsTable.getItems().remove(selected);
        locationConnectionsTable.getItems().add(selected);
//        reloadLocation();
    }

    public void setLocationService(LocationService locationService) {
        log.debug("calling setLocationService(" +locationService+ ")");
        this.locationService = locationService;
    }

    public static void setLocationConnections(List<LocationConnection> locationConnections) {
        log.debug("calling setLocationConnections()");
        EditLocationConnectionsController.locationConnections = locationConnections;
    }

    private void reloadLocation() {
        selectedLocation = locationService.get(selectedLocation.getId());
    }

}
