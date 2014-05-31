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
import sepm.dsa.service.TavernService;

import java.util.List;

public class EditTavernController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditTavernController.class);
    private SpringFxmlLoader loader;

    private Tavern selectedTavern;
    private TavernService tavernService;
    private LocationService locationService;

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
        log.debug("calling SaveButtonPressed");

        String name = nameField.getText();
        selectedTavern.setName(name);
	    selectedTavern.setUsage(Integer.parseInt(usageField.getText()));
        //selectedTaver.setBeds(Integer.parseInt(bedField.getText()));
        //selectedTavern.setComment(commentArea.getText());


        if (isNewTavern) {
            tavernService.add(selectedTavern);
        } else {
            tavernService.update(selectedTavern);
        }


        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed");

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
        this.selectedTavern = tavern;
	    if (selectedTavern == null) {
		    selectedTavern = new Tavern();
	    } else {
		    nameField.setText(selectedTavern.getName());
		    //bedsField.setText(selectedTavern.getBeds());
		    usageField.setText("" + selectedTavern.getUsage());
	    }
    }

	public void setPosition(Point2D pos) {
		selectedTavern.setxPos((int) pos.getX());
		selectedTavern.setyPos((int) pos.getY());
	}

	public void setLocation(Location location) {
		selectedTavern.setLocation(location);
	}

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
}
