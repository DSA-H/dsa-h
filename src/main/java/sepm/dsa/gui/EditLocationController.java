package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.io.File;
import java.net.URL;
import java.util.*;

@Service("EditLocationController")
public class EditLocationController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(EditLocationController.class);
    private SpringFxmlLoader loader;

    private Location selectedLocation;
    private Set<LocationConnection> connections = new HashSet<>();

    private LocationService locationService;
    private LocationConnectionService locationConnectionService;
    private RegionService regionService;
	private MapService mapService;
    private SaveCancelService saveCancelService;

	private int xCoord = 0;
	private int yCoord = 0;
	private File newMap;

    @FXML
    private TextField nameField;
    //@FXML
    //private ChoiceBox<Weather> weatherChoiceBox;
    @FXML
    private Button mapCoordSelection;
    @FXML
    private ChoiceBox<TownSize> sizeChoiceBox;
    @FXML
    private ComboBox<Region> regionChoiceBox;
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

    @FXML
    private TableColumn<LocationConnection, Integer> travelTimeColumn;

    @FXML
    private Button editConnectionsBtn;

    public void setConnections(Set<LocationConnection> connections) {
        this.connections = connections;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        //weatherChoiceBox.getItems().setAll(Weather.values());
        sizeChoiceBox.getItems().setAll(TownSize.values());

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
    }

    @Override
    public void reload() {
        log.debug("reload EditLocationController");

        if(isNew() && selectedLocation.getId() != null) {
            selectedLocation = new Location();
            connections.clear();
        }

        // init region choice box
	    Region selectedRegion = regionChoiceBox.getSelectionModel().getSelectedItem();
        regionChoiceBox.getItems().setAll(regionService.getAll());
        if(!regionChoiceBox.getItems().contains(selectedRegion)) {
            regionChoiceBox.getSelectionModel().select(selectedLocation.getRegion());
        }else if(selectedRegion != null) {
            regionChoiceBox.getSelectionModel().select(selectedRegion);
        }

	    locationConnectionsTable.getItems().setAll(this.connections);
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

    private void applyLocationChanges() {
        // save region
        String name = nameField.getText();
        //Weather weather = Weather.parse(weatherChoiceBox.getSelectionModel().getSelectedIndex());
        TownSize townSize = TownSize.parse(sizeChoiceBox.getSelectionModel().getSelectedIndex());
        String comment = commentArea.getText();
        Region seletcedRegionForLocation = (Region) regionChoiceBox.getSelectionModel().getSelectedItem();
        if (seletcedRegionForLocation == null) {
            throw new DSAValidationException("Wählen sie ein Gebiet aus");
        }
        selectedLocation.setName(name);
        selectedLocation.setComment(comment);
        selectedLocation.setSize(townSize);
        selectedLocation.setRegion(seletcedRegionForLocation);
        selectedLocation.setxCoord(xCoord);
        selectedLocation.setyCoord(yCoord);
        try {
            selectedLocation.setHeight(Integer.parseInt(height.getText()));
        } catch (NumberFormatException e) {
            throw new DSAValidationException("Höhe muss eine Zahl sein.");
        }

    }

    private boolean isNew() {
        if(selectedLocation.getId() != null) {
            if(locationService.get(selectedLocation.getId()) == null) {
                return true;
            }else {
                return false;
            }
        }else {
            return true;
        }

    }

    @FXML
    private void onSavePressed() {
        log.debug("calling SaveButtonPressed");

	    if (newMap != null) {
		    mapService.setLocationMap(selectedLocation, newMap);
	    }
        applyLocationChanges();

        if (isNew()) {
            log.info("addConnection location");
            locationService.add(selectedLocation);
            selectedLocation.setWeather(Weather.getNewWeather(selectedLocation.getRegion().getTemperature(), selectedLocation.getRegion().getRainfallChance()));
        } else {
            log.info("update location");
            locationService.update(selectedLocation);
        }

        Set<LocationConnection> localConnectionList = connections;
        for (LocationConnection connection : locationConnectionService.getAllByLocation(selectedLocation.getId())) {
            boolean contain = false;
            for (LocationConnection localConnection : localConnectionList) {
                if (localConnection.equalsById(connection)) {
                    locationConnectionService.update(connection);
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                locationConnectionService.remove(connection);
            }
            localConnectionList.remove(connection);
        }
        for (LocationConnection connection : localConnectionList) {
            locationConnectionService.add(connection);
        }


        saveCancelService.save();
//        locationService.update(selectedLocation);
        saveCancelService.refresh(selectedLocation);

        // return to locationlist
        Stage stage = (Stage) cancelButton.getScene().getWindow();
	    stage.close();
    }

    @FXML
    public void chooseBackground() {
        log.info("Select Backgroundimage Location");
	    newMap = mapService.chooseMap();
    }


    public void setLocation(Location location) {
        log.debug("calling setLocation(" + location + ")");
        selectedLocation = location;
        if (selectedLocation == null) {
            selectedLocation = new Location();
            //weatherChoiceBox.getSelectionModel().select(Temperature.MEDIUM.getValue());
            sizeChoiceBox.getSelectionModel().select(RainfallChance.MEDIUM.getValue());
        } else {
            xCoord = selectedLocation.getxCoord();
            yCoord = selectedLocation.getyCoord();
            nameField.setText(selectedLocation.getName() == null ? "" : selectedLocation.getName());
            //weatherChoiceBox.getSelectionModel().select(selectedLocation.getWeather());
            sizeChoiceBox.getSelectionModel().select(selectedLocation.getSize());
            commentArea.setText(selectedLocation.getComment() == null ? "" : selectedLocation.getComment());
            height.setText(selectedLocation.getHeight() == null ? "" : "" + selectedLocation.getHeight());
            regionChoiceBox.getSelectionModel().select(selectedLocation.getRegion());
            connections = new HashSet<>(selectedLocation.getAllConnections());
        }

    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    @FXML
    public void onEditConnectionsClicked() {
        log.debug("calling onEditConnectionsClicked");
        applyLocationChanges();

        Stage myStage = (Stage)locationConnectionsTable.getScene().getWindow();
        myStage.close();

        Stage stage = new Stage();
        Parent root = (Parent) loader.load("/gui/editlocationconnections.fxml", stage);
        EditLocationConnectionsController ctrl = (EditLocationConnectionsController)loader.getController();
        ctrl.setSelectedLocation(selectedLocation);
        ctrl.reload();

        stage.setTitle("Reiseverbindungen für Ort '" + selectedLocation.getName() + "' bearbeiten");
        stage.setScene(new Scene(root, 900, 500));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

	public void setPosition(Point2D pos) {
		this.yCoord = (int) pos.getY();
		this.xCoord = (int) pos.getX();
	}

	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setLocationConnectionService(LocationConnectionService locationConnectionService) {
        this.locationConnectionService = locationConnectionService;
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
//                locationConnectionService.addConnection(c);
//                locationConnectionsTable.getItems().addConnection(c);
//            } catch (DSARuntimeException ex) {
//                errorMsgs.addConnection(ex.getMessage());
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
//        locationConnectionService.removeConnection(selected);
//        locationConnectionsTable.getItems().removeConnection(selected);
//    }

//    public void setLocationConnectionService(LocationConnectionService locationConnectionService) {
//        this.locationConnectionService = locationConnectionService;
//    }


//    private void removeConnection(LocationConnection connection) {
//        selectedLocation.removeConnection(connection);
//    }

}