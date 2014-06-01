package sepm.dsa.gui;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
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
    private TextArea commentArea;
    @FXML
    private TextField usageField;
    @FXML
    private TextField bedsField;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditTavernController");
    }

    @FXML
    private void onSavePressed() {
        log.debug("called onSavePressed");

        // save region
        String name = nameField.getText();
        selectedTavern.setName(name);
        selectedTavern.setUsage(Integer.parseInt(usageField.getText()));
        selectedTavern.setBeds(Integer.parseInt(bedsField.getText()));
        selectedTavern.setComment(commentArea.getText());

        if (isNewTavern) {
            tavernService.add(selectedTavern);
        } else {
            tavernService.update(selectedTavern);
        }
        saveCancelService.save();

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();


    }

    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed");
        saveCancelService.cancel();
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
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
            fillGuiWithData(selectedTavern);
        }
    }

    /**
     * @param tavern must be valid and must not be null
     */
    private void fillGuiWithData(Tavern tavern) {
        nameField.setText(tavern.getName());
        usageField.setText("" + tavern.getUsage());
        bedsField.setText("" + tavern.getBeds());
        commentArea.setText(tavern.getComment() == null ? "" : tavern.getComment());
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setPosition(Point2D pos) {
        selectedTavern.setxPos((int) pos.getX());
        selectedTavern.setyPos((int) pos.getY());
    }

    public void setLocation(Location location) {
        selectedTavern.setLocation(location);
    }
}
