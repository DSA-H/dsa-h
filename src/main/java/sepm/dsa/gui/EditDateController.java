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
import sepm.dsa.service.TimeService;

import java.net.URL;
import java.util.ResourceBundle;

public class EditDateController extends BaseControllerImpl {
    private static final Logger log = LoggerFactory.getLogger(EditDateController.class);
    private SpringFxmlLoader loader;

    private TimeService timeService;

    @FXML
    private TextField day;
    @FXML
    private ComboBox<String> month;
    @FXML
    private TextField year;
    @FXML
    private Label actDate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        month.getItems().setAll(DSADate.getMonthNames());
        DSADate date = timeService.getCurrentDate();

        actDate.setText(date.toString());
        day.setText(date.getDay()+"");
        month.getSelectionModel().select(date.getMonth()-1);
        year.setText(date.getYear()+"");
    }

    @Override
    public void reload() {
        log.debug("reload EditDateController");
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

        DSADate newDate = null;
        try {
            int d = Integer.parseInt(day.getText());
            int m = month.getSelectionModel().getSelectedIndex() + 1;
            int y = Integer.parseInt(year.getText());
            newDate = new DSADate(y, m, d);
        }catch(NumberFormatException ex) {
            throw new DSAValidationException("Tag und Jahr müssen jeweils ganze Zahlen sein!");
        }

        Action response = Dialogs.create()
                .title("Datum wirklich ändern?")
                .masthead(null)
                .message("Wollen Sie das Datum wirklich auf " + newDate + " ändern? Das Ändern des Datums führt bei " +
                        "bestehenden Händlern und abgeschlossenen Käufen eventuell zu nicht sinnvollen relativen Zeitangaben und sollte daher nur einmal zu" +
                        " Beginn einer Spielwelt gemacht werden. \nBeachten Sie, dass es zum Vorstellen des Datums, wegen im Spiel " +
                        "vergangener Zeit, einen eigenen Button im Hauptmenü gibt!")
                .showConfirm();
        if (response == Dialog.Actions.YES) {
            timeService.setCurrentDate(newDate);
            Stage stage = (Stage) day.getScene().getWindow();
            stage.close();
        }
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
}
