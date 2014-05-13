package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;
import sepm.dsa.model.Region;

@Service("EditRegionController")
public class EditRegionController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditRegionController.class);

    private static Region selectedRegion;

    private RegionService regionService;
    private RegionBorderService regionBorderService;

    @FXML
    private TextField name;
    @FXML
    private ChoiceBox temperature;
    @FXML
    private ColorPicker color;
    @FXML
    private ChoiceBox rainfall;
    @FXML
    private ChoiceBox border;
    @FXML
    private TextField borderCost;
    @FXML
    private TextArea comment;
    @FXML
    private TableView borderTable;
    @FXML
    private TableColumn borderColumn;
    @FXML
    private TableColumn borderCostColumn;
    @FXML
    private Button cancel;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditRegionController");
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("border"));
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("borderCost"));

        // DUMMY!
        // TODO change to enum
        ObservableList<String> temperatureList = FXCollections.observableArrayList("ARCTIC", "LOW", "MEDIUM", "HIGH", "VOLCANO");
        ObservableList<String> rainList = FXCollections.observableArrayList("DESERT", "LOW", "MEDIUM", "HIGH", "MONSUN");

        temperature.setItems(temperatureList);
        rainfall.setItems(rainList);

        if (selectedRegion != null) {
            name.setText(selectedRegion.getName());
            color.setValue(new Color(
                    (double) Integer.valueOf(selectedRegion.getColor().substring( 0, 2 ), 16 ) / 255,
                    (double) Integer.valueOf(selectedRegion.getColor().substring( 2, 4 ), 16 ) / 255,
                    (double) Integer.valueOf(selectedRegion.getColor().substring( 4, 6 ), 16 ) / 255,
                    1.0)
            );



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
        Stage stage = (Stage) name.getScene().getWindow();
        Parent scene = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        scene = (Parent) loader.load("/gui/regionlist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        String newName = name.getText();

        Color selectedColor = color.getValue();
        String colorString =
                Integer.toHexString((int) (selectedColor.getRed()*255)) + "" +
                Integer.toHexString((int) (selectedColor.getGreen()*255)) + "" +
                Integer.toHexString((int) (selectedColor.getBlue()*255));

        boolean allowed = true;
        int counter = 0;
        for(int i=0; i<newName.length(); i++) {
            if( newName.charAt(i) == ' ' ) {
                counter++;
            }
        }
        if (newName.length() == counter) {
            allowed = false;
        }

        if (allowed) {
            if (selectedRegion == null) {
                Region newRegion = new Region();
                newRegion.setColor(colorString);
                newRegion.setName(newName);
                regionService.add(newRegion);
            }
            else {
                selectedRegion.setColor(colorString);
                selectedRegion.setName(newName);
                regionService.update(selectedRegion);
            }

            // return to regionlist
            Stage stage = (Stage) cancel.getScene().getWindow();
            Parent scene = null;
            SpringFxmlLoader loader = new SpringFxmlLoader();

            scene = (Parent) loader.load("/gui/regionlist.fxml");

            stage.setScene(new Scene(scene, 600, 438));
        }
        else {
            /*
            Stage warningStage = new Stage();
            Parent scene = null;
            SpringFxmlLoader loader = new SpringFxmlLoader();
            scene = (Parent) loader.load("/gui/warning.fxml");
            warningStage.setScene(new Scene(scene, 300, 200));
            */
        }
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
