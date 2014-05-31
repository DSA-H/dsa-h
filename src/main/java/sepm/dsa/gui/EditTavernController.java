package sepm.dsa.gui;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.model.Tavern;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.service.TavernService;

import java.util.List;

public class EditTavernController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditTavernController.class);
    private SpringFxmlLoader loader;

    private Tavern selectedTavern;
    private TavernService tavernService;
    private LocationService locationService;
    private SaveCancelService saveCancelService;

    private boolean isNewTavern;

    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<Location> locationBox;
    @FXML
    private TextArea commentArea;
    @FXML
    private TextField usageField;
    @FXML
    private TextField bedsField;
    @FXML
    private TextField xCoordField;
    @FXML
    private TextField yCoordField;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditTavernController");


        List<Location> locations = locationService.getAll();
        locationBox.setItems(FXCollections.observableArrayList(locations));

    }

    @FXML
    private void onSavePressed() {
        log.debug("called onSavePressed");

        // save region
        String name = nameField.getText();
        selectedTavern.setName(name);
        selectedTavern.setLocation(locationBox.getSelectionModel().getSelectedItem());
        selectedTavern.setUsage(Integer.parseInt(usageField.getText()));
//        selectedTaver.setBeds(Integer.parseInt(bedField.getText()));
        selectedTavern.setxPos(Integer.parseInt(xCoordField.getText()));
        selectedTavern.setyPos(Integer.parseInt(yCoordField.getText()));
//        selectedTavern.setComment(commentArea.getText());

        Tavern persistedTavern = null;
        if (isNewTavern) {
            persistedTavern = tavernService.add(selectedTavern);
        } else {
            persistedTavern = tavernService.update(selectedTavern);
        }
        saveCancelService.save();
        log.info("persistedTavern: " + persistedTavern == null ? "null" : persistedTavern.toString());

        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/tavernlist.fxml");
        stage.setScene(new Scene(scene, 600, 400));


    }

    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed");
        saveCancelService.cancel();
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/tavernlist.fxml");
        stage.setScene(new Scene(scene, 600, 400));
    }

    public void setTavernService(TavernService tavernService) {
        log.debug("calling setTavernService(" + tavernService + ")");
        this.tavernService = tavernService;
    }

    public void setLocationService(LocationService locationService) {
        log.debug("calling setLocationService(" + locationService + ")");
        this.locationService = locationService;
    }

    public void setTavern(Tavern tavern) {
	    if (tavern == null) {
            isNewTavern = true;
            selectedTavern = new Tavern();
        } else {
            isNewTavern = false;
            this.selectedTavern = tavern;
        }
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
