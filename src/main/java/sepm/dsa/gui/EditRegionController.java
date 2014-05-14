package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Temperature;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;
import sepm.dsa.model.Region;

import java.util.ArrayList;
import java.util.List;

@Service("EditRegionController")
public class EditRegionController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditRegionController.class);

    private static Region selectedRegion;

    private RegionService regionService;
    private RegionBorderService regionBorderService;
    // true if the region is not editing
    private boolean isNewRegion;

    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox temperatureChoiceBox;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ChoiceBox rainfallChoiceBox;
    @FXML
    private ChoiceBox borderChoiceBox;
    @FXML
    private TextField borderCost;
    @FXML
    private TextArea commentArea;
    @FXML
    private TableView borderTable;
    @FXML
    private TableColumn borderColumn;
    @FXML
    private TableColumn borderCostColumn;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditRegionController");
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("borderChoiceBox"));
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("borderCost"));


        List<String> temperatureList = new ArrayList<>();
        for(Temperature t : Temperature.values()) {
            temperatureList.add(t.getName());
        }
        List<String> rainList = new ArrayList<>();
        for(RainfallChance t : RainfallChance.values()) {
            rainList.add(t.getName());
        }

        temperatureChoiceBox.setItems(FXCollections.observableArrayList(temperatureList));
        rainfallChoiceBox.setItems(FXCollections.observableArrayList(rainList));

        // set values if editing
        if (selectedRegion != null) {
            isNewRegion = false;
            nameField.setText(selectedRegion.getName());
            colorPicker.setValue(new Color(
                            (double) Integer.valueOf(selectedRegion.getColor().substring(0, 2), 16) / 255,
                            (double) Integer.valueOf(selectedRegion.getColor().substring(2, 4), 16) / 255,
                            (double) Integer.valueOf(selectedRegion.getColor().substring(4, 6), 16) / 255,
                            1.0)
            );
            temperatureChoiceBox.getSelectionModel().select(selectedRegion.getTemperature().getValue());
            rainfallChoiceBox.getSelectionModel().select(selectedRegion.getRainfallChance().getValue());
            commentArea.setText(selectedRegion.getComment());
        }else {
            isNewRegion = true;
            temperatureChoiceBox.getSelectionModel().select(Temperature.MEDIUM.getValue());
            rainfallChoiceBox.getSelectionModel().select(RainfallChance.MEDIUM.getValue());
        }

    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setRegionBorderService(RegionBorderService regionBorderService) {
        this.regionBorderService = regionBorderService;
    }

    @FXML
    private void onBorderCostColumnChanged() {
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        scene = (Parent) loader.load("/gui/regionlist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        String name = nameField.getText();
        Temperature temperature = Temperature.parse(temperatureChoiceBox.getSelectionModel().getSelectedIndex());
        RainfallChance rainfallChance = RainfallChance.parse(rainfallChoiceBox.getSelectionModel().getSelectedIndex());
        String comment = commentArea.getText();

        Color selectedColor = colorPicker.getValue();
        String colorString =
                Integer.toHexString((int) (selectedColor.getRed()*255)) + "" +
                Integer.toHexString((int) (selectedColor.getGreen()*255)) + "" +
                Integer.toHexString((int) (selectedColor.getBlue()*255));

        if (isNewRegion) {
            selectedRegion = new Region();
        }

        selectedRegion.setColor(colorString);
        selectedRegion.setName(name);
        selectedRegion.setComment(comment);
        selectedRegion.setTemperature(temperature);
        selectedRegion.setRainfallChance(rainfallChance);

        if(isNewRegion) {
            regionService.add(selectedRegion);
        }else {
            regionService.update(selectedRegion);
        }

        // return to regionlist
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Parent scene = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        scene = (Parent) loader.load("/gui/regionlist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onAddBorderPressed() {
    }

    @FXML
    private void onRemoveBorderPressed() {
    }

    public static void setRegion(Region region) {
        selectedRegion = region;
    }
}
