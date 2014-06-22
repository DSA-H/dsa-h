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
import javafx.stage.Modality;
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
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
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
    }

    @Override
    public void reload() {
        log.debug("initialize");

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
                allConnections.add(conWrapper.getLocationConnection());
            }
		    locationConnectionsTable.getItems().setAll(allConnections);
        }

        checkFocus();
        // TODO suggest connections click
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSelectedLocation(Location selectedLocation) {
        if (selectedLocation == null) {
            selectedLocation = new Location();
        }
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
		suggestLocationConnectionsTable.getItems().setAll(newConns);

    }

    @FXML
    public void onEditConnectionClicked() {
        log.debug("calling onEditConnectionClicked()");
        Stage myStage = (Stage)locationConnectionsTable.getScene().getWindow();
        myStage.close();

        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();

        this.setLoadSelectedLocation_Connections_OnInitialize(false);

        selectedLocation.clearConnections();
        for (LocationConnectionWrapper w : locationConnectionsToStore) {
            selectedLocation.addConnection(w.getLocationConnection());
        }

        Stage stage = new Stage();
        Parent root = (Parent) loader.load("/gui/editlocationconnection.fxml", stage);
        EditLocationConnectionController ctrl = loader.getController();
        ctrl.setLocationConnection(selected);
        ctrl.setSelectedLocation(selectedLocation);
        ctrl.reload();

        stage.setTitle("Reiseverbindung bearbeiten");
        stage.setScene(new Scene(root, 500, 380));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    @FXML
    public void onRemoveConnectionClicked() {
        log.debug("calling onRemoveConnectionClicked()");
        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();
        if (locationConnectionsToStore.remove(new LocationConnectionWrapper(selected))) {
            locationConnectionsTable.getItems().remove(selected);
            suggestLocationConnectionsTable.getItems().add(selected);
        }
    }

    @FXML
    public void onApplyAllClicked() {
        log.debug("calling onApplyAllClicked()");

        List<LocationConnection> applied = new ArrayList<>(suggestLocationConnectionsTable.getItems().size());
        for (LocationConnection c : suggestLocationConnectionsTable.getItems()) {
            applied.add(c);
        }
        for (LocationConnection c : applied) {
            if (locationConnectionsToStore.add(new LocationConnectionWrapper(c))) {
                locationConnectionsTable.getItems().add(c);
                suggestLocationConnectionsTable.getItems().remove(c);
            }
        }
    }

    @FXML
    public void onFinishedClicked() {
        log.debug("calling onFinishedClicked()");
        Stage myStage = (Stage)locationConnectionsTable.getScene().getWindow();
        myStage.close();

        Set<LocationConnection> selectedLocationConnections = new HashSet<>(locationConnectionsToStore.size());
        for (LocationConnectionWrapper conWrapper : locationConnectionsToStore) {
            selectedLocationConnections.add(conWrapper.getLocationConnection());
        }

        Stage stage = new Stage();
        Parent root = (Parent) loader.load("/gui/editlocation.fxml", stage);
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
        if (locationConnectionsToStore.add(new LocationConnectionWrapper(selected))) {
            suggestLocationConnectionsTable.getItems().remove(selected);
            locationConnectionsTable.getItems().add(selected);
        }
    }

    @FXML
    private void checkFocus() {
        log.debug("calling checkFocus()");
        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();
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

    private void reloadLocation() {
        selectedLocation = locationService.get(selectedLocation.getId());
    }

    public void setLoadSelectedLocation_Connections_OnInitialize(boolean loadSelectedLocation_Connections_OnInitialize) {
        this.loadSelectedLocation_Connections_OnReload = loadSelectedLocation_Connections_OnInitialize;
    }
}
