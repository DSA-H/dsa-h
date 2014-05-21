package sepm.dsa.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.service.LocationConnectionService;

import java.net.URL;
import java.text.ParseException;
import java.util.ResourceBundle;


@Service("EditLocationConnectionController")
public class EditLocationConnectionController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditLocationConnectionController.class);
    private SpringFxmlLoader loader;

    private LocationConnectionService locationConnectionService;

    private static LocationConnection locationConnection;

    @FXML
    private Label lbl_Location1;

    @FXML
    private Label lbl_Location2;

    @FXML
    private TextField tf_TravelTime;

    @FXML
    private TextArea ta_Comment;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.debug("initialise");
        lbl_Location1.setText(locationConnection.getLocation1().getName());
        lbl_Location1.setText(locationConnection.getLocation2().getName());
        tf_TravelTime.setText(locationConnection.getTravelTime() + "");
        String comment = locationConnection.getComment();
        ta_Comment.setText(comment == null ? "" : comment);
    }

    public void setLocationConnectionService(LocationConnectionService locationConnectionService) {
        log.debug("setLocationConnectionService(" + locationConnectionService + ")");
        this.locationConnectionService = locationConnectionService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        log.debug("setLoader(" + loader + ")");
        this.loader = loader;
    }

    @FXML
    public void onSaveClicked() {
        log.debug("onSaveClicked");
        Integer travelTime = null;
        try {
            travelTime = Integer.parseInt(tf_TravelTime.getText());
        } catch (NumberFormatException ex) {}

        locationConnection.setTravelTime(travelTime);
        locationConnection.setComment(ta_Comment.getText());
        locationConnectionService.update(locationConnection);
        goBack();
    }

    @FXML
    public void onAbortClicked() {
        log.debug("onAbortClicked");
        goBack();

    }

    private void goBack() {

        Stage stage = (Stage) lbl_Location1.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/editlocationconnections.fxml");

        stage.setScene(new Scene(root, 900, 500));
        stage.show();
    }

    public static void setLocationConnection(LocationConnection locationConnection) {
        EditLocationConnectionController.locationConnection = locationConnection;
    }
}
