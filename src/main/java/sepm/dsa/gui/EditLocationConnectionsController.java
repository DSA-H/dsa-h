package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import sepm.dsa.model.LocationConnectionWrapper;
import sepm.dsa.service.LocationConnectionService;
import sepm.dsa.service.LocationService;

import java.net.URL;
import java.util.*;

@Service("EditLocationConnectionsController")
public class EditLocationConnectionsController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(EditLocationConnectionsController.class);

    private SpringFxmlLoader loader;

    private Location selectedLocation;
    private static List<LocationConnection> locationConnections;

    private boolean loadSelectedLocation_Connections_OnReload = true;

    private LocationConnectionService locationConnectionService;
    private LocationService locationService;

    private Set<LocationConnectionWrapper> locationConnectionsToStore = new HashSet<>();

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

    @FXML
    private Button removeButton;

    @FXML
    private Button editButton;

    @Override
    public void reload() {
        log.debug("initialize");
        log.info("--- going to edit location connections for location '" + selectedLocation + "'");
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
        if (loadSelectedLocation_Connections_OnReload) {
            Set<LocationConnection> allConnections = selectedLocation.getAllConnections();
            locationConnectionsToStore.clear();
            for (LocationConnection con : allConnections) {
                locationConnectionsToStore.add(new LocationConnectionWrapper(con));
            }
	    locationConnectionsTable.getItems().setAll(allConnections);
        } else {
            Set<LocationConnection> allConnections = new HashSet<>(locationConnectionsToStore.size());
            for (LocationConnectionWrapper conWrapper : locationConnectionsToStore) {
//                locationConnectionsToStore.add(new LocationConnectionWrapper(con));
                allConnections.add(conWrapper.getLocationConnection());
            }
		locationConnectionsTable.getItems().setAll(allConnections);
        }

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

        locationConnectionsTable.getFocusModel().focusedItemProperty().addListener(new ChangeListener<LocationConnection>() {
            @Override
            public void changed(ObservableValue<? extends LocationConnection> observable, LocationConnection oldValue, LocationConnection newValue) {
                checkFocus();
            }
        });
        checkFocus();
        // TODO suggest connections click
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSelectedLocation(Location selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public void setLocationConnectionService(LocationConnectionService locationConnectionService) {
        this.locationConnectionService = locationConnectionService;
    }

    @FXML
    public void onSuggestConnectionsClicked() {
        log.debug("calling onSuggestConnectionsClicked()");
        List<LocationConnection> suggestedConnections = locationConnectionService.suggestLocationConnectionsAround(selectedLocation, 100.0);
        addToSuggestion(suggestedConnections);
    }

    @FXML
    public void onFilterConnectionsClicked() {
        log.debug("calling onFilterConnectionsClicked()");
        List<LocationConnection> suggestedConnections = locationConnectionService.suggestLocationConnectionsByFilter(selectedLocation, locationNameFilter.getText());
        addToSuggestion(suggestedConnections);
    }

    private void addToSuggestion(Collection<LocationConnection> connections) {
        List<LocationConnection> newConns = new ArrayList<>(connections.size());
        for (LocationConnection c : connections) {
            if (!locationConnectionsToStore.contains(new LocationConnectionWrapper(c))) {
                newConns.add(c);
            }
        }
	suggestLocationConnectionsTable.setItems(newConns);

    }

    @FXML
    public void onEditConnectionClicked() {
        log.debug("calling onEditConnectionClicked()");
        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();

        this.setLoadSelectedLocation_Connections_OnInitialize(false);

        Stage stage = (Stage) locationConnectionsTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/editlocationconnection.fxml");
        EditLocationConnectionController ctrl = loader.getController();
        ctrl.setLocationConnection(selected);
        ctrl.reload();

        stage.setTitle("Reiseverbindung bearbeiten");
        stage.setScene(new Scene(root, 500, 380));
        stage.show();
    }

    @FXML
    public void onRemoveConnectionClicked() {
        log.debug("calling onRemoveConnectionClicked()");
        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();
//        locationConnectionService.remove(selected);
        if (locationConnectionsToStore.remove(new LocationConnectionWrapper(selected))) {
            locationConnectionsTable.getItems().remove(selected);
            suggestLocationConnectionsTable.getItems().add(selected);
//            selectedLocation.removeConnection(selected);
        }
    }

    @FXML
    public void onApplyAllClicked() {
        log.debug("calling onApplyAllClicked()");

        List<LocationConnection> applied = new ArrayList<>(suggestLocationConnectionsTable.getItems().size());
        for (LocationConnection c : suggestLocationConnectionsTable.getItems()) {
//            locationConnectionService.add(c);
            applied.add(c);
        }
        for (LocationConnection c : applied) {
            if (locationConnectionsToStore.add(new LocationConnectionWrapper(c))) {
                locationConnectionsTable.getItems().add(c);
                suggestLocationConnectionsTable.getItems().remove(c);
//                selectedLocation.addConnection(c);
            }
        }
    }

    @FXML
    public void onFinishedClicked() {
        log.debug("calling onFinishedClicked()");

//        selectedLocation.clearConnections();
//        selectedLocation.addAllConnections(locationConnectionsToStore);
        Set<LocationConnection> selectedLocationConnections = new HashSet<>(locationConnectionsToStore.size());
        for (LocationConnectionWrapper conWrapper : locationConnectionsToStore) {
            selectedLocationConnections.add(conWrapper.getLocationConnection());
        }

        Stage stage = (Stage) locationConnectionsTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/editlocation.fxml");
        EditLocationController ctrl = loader.getController();
        ctrl.setLocation(selectedLocation);
        ctrl.setConnections(selectedLocationConnections);
        ctrl.reload();

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
//        locationConnectionService.add(selected);
        if (locationConnectionsToStore.add(new LocationConnectionWrapper(selected))) {
            suggestLocationConnectionsTable.getItems().remove(selected);
            locationConnectionsTable.getItems().add(selected);
//            selectedLocation.addConnection(selected);
        }
//        reloadLocation();
    }

    @FXML
    private void checkFocus() {
        log.debug("calling checkFocus()");
        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
        if (selected == null) {
            removeButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            removeButton.setDisable(false);
            editButton.setDisable(false);
        }
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

//    private void refreshLocationConnectionsToStoreList() {
//        locationConnectionsTable.setItems(FXCollections.observableList(new ArrayList<>(locationConnectionsToStore)));
//    }


    public void setLoadSelectedLocation_Connections_OnInitialize(boolean loadSelectedLocation_Connections_OnInitialize) {
        this.loadSelectedLocation_Connections_OnReload = loadSelectedLocation_Connections_OnInitialize;
    }
}
