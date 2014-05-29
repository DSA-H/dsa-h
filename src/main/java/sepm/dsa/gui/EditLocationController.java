package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.LocationConnectionService;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.MapService;
import sepm.dsa.service.RegionService;

import java.io.File;
import java.util.*;

@Service("EditLocationController")
public class EditLocationController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditLocationController.class);
    private SpringFxmlLoader loader;

    private static Location selectedLocation;

    private LocationService locationService;
    private RegionService regionService;
	private MapService mapService;
    // true if the location is not editing
    private boolean isNewLocation;

	private int xCoord = 0;
	private int yCoord = 0;

    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox weatherChoiceBox;
    @FXML
    private Button mapCoordSelection;
    @FXML
    private ChoiceBox sizeChoiceBox;
    @FXML
    private ChoiceBox regionChoiceBox;
    @FXML
    private TextField height;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button cancelButton;
    @FXML
    private Button removeBorderButton;
    //map file name
    private String backgroundMapName = "";

    @FXML
    private TableView<LocationConnection> locationConnectionsTable;

    @FXML
    private TableColumn<LocationConnection, String> connectionToColumn;
//    @FXML
//    private TableColumn<LocationConnection, Location> location2Column;
    @FXML
    private TableColumn<LocationConnection, Integer> travelTimeColumn;

    @FXML
    private Button editConnectionsBtn;
//    @FXML
//    private Button suggestConnectionsBtn;
//    @FXML
//    private Button addConnectionBtn;
//    @FXML
//    private Button removeConnectionBtn;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditRegionController");

        // init ChoiceBoxes
        List<String> sizeList = new ArrayList<>();
        for (TownSize t : TownSize.values()) {
            sizeList.add(t.getName());
        }
        List<String> weatherList = new ArrayList<>();
        for (Weather w : Weather.values()) {
            weatherList.add(w.getName());
        }
        weatherChoiceBox.setItems(FXCollections.observableArrayList(weatherList));
        sizeChoiceBox.setItems(FXCollections.observableArrayList(sizeList));

        // set values if editing
        if (selectedLocation != null) {
            isNewLocation = false;
            nameField.setText(selectedLocation.getName());
            weatherChoiceBox.getSelectionModel().select(selectedLocation.getWeather().getValue());
            sizeChoiceBox.getSelectionModel().select(selectedLocation.getSize().getValue());
            commentArea.setText(selectedLocation.getComment());
	        xCoord = selectedLocation.getxCoord();
	        yCoord = selectedLocation.getyCoord();
            height.setText(selectedLocation.getHeight().toString());
            regionChoiceBox.getSelectionModel().select(selectedLocation.getRegion());
        } else {
            isNewLocation = true;
            selectedLocation = new Location();
            weatherChoiceBox.getSelectionModel().select(Temperature.MEDIUM.getValue());
            sizeChoiceBox.getSelectionModel().select(RainfallChance.MEDIUM.getValue());
        }

        // init region choice box
        List<Region> otherRegions = regionService.getAll();
//        otherRegions.remove(selectedLocation.getRegion());
        regionChoiceBox.setItems(FXCollections.observableArrayList(otherRegions));

        travelTimeColumn.setCellValueFactory(new PropertyValueFactory<>("travelTime"));
//        location1Column.setCellValueFactory(new PropertyValueFactory<>("location1"));
//        location2Column.setCellValueFactory(new PropertyValueFactory<>("location2"));
//        location1Column.setCellFactory(new Callback<TableColumn<LocationConnection, Location>, TableCell<LocationConnection, Location>>() {
//            @Override
//            public TableCell<LocationConnection, Location> call(TableColumn<LocationConnection, Location> locationConnectionLocationTableColumn) {
//                return new TableCell<LocationConnection, Location>() {
//
//                    @Override
//                    protected void updateItem(Location item, boolean empty) {
//                        super.updateItem(item, empty);
//
//                        if (!empty) {
//                            if (item != null) {
//                                setText(item.getName());
//                                if (selectedLocation.equals(item)) {
//                                    //
//                                }
//                            } else {
//                                setText("<null>");
//                            }
//                        } else {
//                            setText(null);
//                        }
//                    }
//
//                };
//            }
//        });
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

    }

    public void setLocationService(LocationService locationService) {
        log.debug("calling setLocationService(" + locationService + ")");
        this.locationService = locationService;
    }

    public void setRegionService(RegionService regionService) {
        log.debug("calling setRegionService(" + regionService + ")");
        this.regionService = regionService;
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) nameField.getScene().getWindow();
	    stage.close();
    }

    private void saveLocation() {
        // save region
        String name = nameField.getText();
        Weather weather = Weather.parse(weatherChoiceBox.getSelectionModel().getSelectedIndex());
        TownSize townSize = TownSize.parse(sizeChoiceBox.getSelectionModel().getSelectedIndex());
        String comment = commentArea.getText();
        Region seletcedRegionForLocation = (Region) regionChoiceBox.getSelectionModel().getSelectedItem();
        if (seletcedRegionForLocation == null) {
            throw new DSAValidationException("Wählen sie ein Gebiet aus");
        }
        selectedLocation.setPlanFileName(backgroundMapName);
        selectedLocation.setName(name);
        selectedLocation.setComment(comment);
        selectedLocation.setWeather(weather);
        selectedLocation.setSize(townSize);
        selectedLocation.setRegion(seletcedRegionForLocation);
        selectedLocation.setxCoord(xCoord);
        selectedLocation.setyCoord(yCoord);
        try {
            selectedLocation.setHeight(Integer.parseInt(height.getText()));
        } catch (NumberFormatException e) {
            throw new DSAValidationException("Höhe muss eine Zahl sein.");
        }

        log.info("connections now in selected Location");
        for (LocationConnection con : selectedLocation.getAllConnections()) {
            log.info("location: " + con);
        }

        if (isNewLocation) {
            log.info("add location");
            locationService.add(selectedLocation);
        } else {
            log.info("update location");
            locationService.update(selectedLocation);
        }

        selectedLocation = locationService.get(selectedLocation.getId());
    }

    @FXML
    private void onSavePressed() {
        log.debug("calling SaveButtonPressed");

        saveLocation();


//        locationService.update(selectedLocation);


        // return to locationlist
        Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
    }

    @FXML
    public void chooseBackground() {
        log.info("Select Backgroundimage Location");
	    File newMap = mapService.chooseMap();
	    if (newMap == null) { return; }
	    mapService.setLocationMap(selectedLocation, newMap);
    }


    public static void setLocation(Location location) {
        log.debug("calling setLocation(" + location + ")");
        selectedLocation = location;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    @FXML
    public void onEditConnectionsClicked() {
        log.debug("calling onEditConnectionsClicked");

        saveLocation();

        EditLocationConnectionsController.setSelectedLocation(selectedLocation);

        Stage stage = (Stage) locationConnectionsTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/editlocationconnections.fxml");

        stage.setTitle("Reiseverbindungen für Ort '" + selectedLocation.getName() + "' bearbeiten");
        stage.setScene(new Scene(root, 900, 500));
        stage.show();
    }

	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}

//    @FXML
//    public void onSuggestConnectionsBtnClicked() {
//        try {
//            selectedLocation.setxCoord(Integer.parseInt(xCoord.getText()));
//        } catch (NumberFormatException e) {
//            throw new DSAValidationException("xCoord muss eine Zahl sein.");
//        }
//        try {
//            selectedLocation.setyCoord(Integer.parseInt(yCoord.getText()));
//        } catch (NumberFormatException e) {
//            throw new DSAValidationException("yCoord muss eine Zahl sein.");
//        }
//        List<LocationConnection> suggestedConnections = locationService.suggestLocationConnectionsAround(selectedLocation, 100.0);
//
////        if (selectedLocation.getId() != null) {
////            Location reloaded = locationService.get(selectedLocation.getId());
////            if (reloaded != null) {
////                suggestedConnections.addAll(reloaded.getAllConnections());
////            }
////        }
////        ObservableList<LocationConnection> connections = FXCollections.observableArrayList(suggestedConnections);
////        locationConnectionsTable.getItems().clear();
////        locationConnectionsTable.setItems(connections);
//        ArrayList<String> errorMsgs = new ArrayList<>();
//        for (LocationConnection c : suggestedConnections) {
//            try {
//                locationConnectionService.add(c);
//                locationConnectionsTable.getItems().add(c);
//            } catch (DSARuntimeException ex) {
//                errorMsgs.add(ex.getMessage());
//            }
//        }
//        if (errorMsgs.size() > 0) {
//            Dialogs.create()
//                    .title(errorMsgs.size() + " Verbindungen konnten nicht hinzugefügt werden.")
//                    .masthead(null)
//                    .message(errorMsgs.toString())
//                    .showWarning();
//        }
//
//    }

//    @FXML
//    public void onAddConnectionBtnClicked() {
//
//    }
//
//    @FXML
//    public void onRemoveConnectionBtnClicked() {
//        LocationConnection selected = locationConnectionsTable.getSelectionModel().getSelectedItem();
//        locationConnectionService.remove(selected);
//        locationConnectionsTable.getItems().remove(selected);
//    }

//    public void setLocationConnectionService(LocationConnectionService locationConnectionService) {
//        this.locationConnectionService = locationConnectionService;
//    }


//    private void removeConnection(LocationConnection connection) {
//        selectedLocation.removeConnection(connection);
//    }

}