package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.DSADate;
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.service.TimeService;

import java.net.URL;
import java.util.ResourceBundle;

public class ForwardDateController extends BaseControllerImpl {
    private static final Logger log = LoggerFactory.getLogger(ForwardDateController.class);
    private SpringFxmlLoader loader;

    private TimeService timeService;
    private SaveCancelService saveCancelService;

    @FXML
    private TextField day;
    @FXML
    private Label actDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
	    super.initialize(location, resources);

	    day.setText("3");  // default value
    }

    @Override
    public void reload() {
        DSADate date = timeService.getCurrentDate();

        actDate.setText(date.toString());
    }

    @FXML
    public void cancelClicked() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) day.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void saveClicked() {
        log.debug("SaveButtonPressed");

        int d = 0;
        try {
            d = Integer.parseInt(day.getText());
            timeService.forwardTime(d);

            saveCancelService.save();

            Stage stage = (Stage) day.getScene().getWindow();
            stage.close();
        }catch(NumberFormatException ex) {
            throw new DSAValidationException("Tag muss eine ganze positive Zahl sein!");
        }
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
