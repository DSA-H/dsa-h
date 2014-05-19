package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.RegionService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service("EditLocationController")
public class EditLocationController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditLocationController.class);
    private SpringFxmlLoader loader;

    private static Location selectedLocation;

    private LocationService locationService;
    private RegionService regionService;
    // true if the location is not editing
    private boolean isNewLocation;

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
    private TextField xCoord;
    @FXML
    private TextField yCoord;
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
            xCoord.setText(selectedLocation.getxCoord().toString());
            yCoord.setText(selectedLocation.getyCoord().toString());
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
        Parent scene = (Parent) loader.load("/gui/locationlist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("calling SaveButtonPressed");

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
        try {
            selectedLocation.setxCoord(Integer.parseInt(xCoord.getText()));
        } catch (NumberFormatException e) {
            throw new DSAValidationException("xCoord muss eine Zahl sein.");
        }
        try {
            selectedLocation.setyCoord(Integer.parseInt(yCoord.getText()));
        } catch (NumberFormatException e) {
            throw new DSAValidationException("yCoord muss eine Zahl sein.");
        }
        try {
            selectedLocation.setHeight(Integer.parseInt(height.getText()));
        } catch (NumberFormatException e) {
            throw new DSAValidationException("Höhe muss eine Zahl sein.");
        }

        if (isNewLocation) {
            locationService.add(selectedLocation);
        } else {
            locationService.update(selectedLocation);
        }

        // return to locationlist
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/locationlist.fxml");


        //TODO ist das so gut immer eine NEUE scene zu öffnen?
        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    public void chooseBackground() {
        log.info("Select Backgroundimage Location");
//choose File to export
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ort Karte wählen");
        List<String> extensions = new ArrayList<String>();
        extensions.add("*.jpg");
        extensions.add("*.png");
        extensions.add("*.gif");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", extensions),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );

        //look for location map
        File newlocationMap = fileChooser.showOpenDialog(new Stage());

        if (newlocationMap == null) {
            return;
        }
        this.backgroundMapName = newlocationMap.getAbsolutePath();
    }


    public static void setLocation(Location location) {
        log.debug("calling setLocation(" + location + ")");
        selectedLocation = location;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
}