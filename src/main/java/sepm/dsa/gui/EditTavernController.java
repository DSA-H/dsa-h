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
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TraderCategoryService;
import sepm.dsa.service.TraderService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditTavernController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditTavernController.class);
    private SpringFxmlLoader loader;

    private Trader selectedTrader;
    private TraderService traderService;
    private TraderCategoryService categoryService;
    private LocationService locationService;

    private boolean isNewTrader;

    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox locationBox;
    @FXML
    private TextArea commentArea;
    //....


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditTraderController");


        List<Location> locations = locationService.getAll();
        locationBox.setItems(FXCollections.observableArrayList(locations));

    }

    @FXML
    private void onSavePressed() {
        log.debug("called onSavePressed");




        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/tavernlist.fxml");
        stage.setScene(new Scene(scene, 600, 400));


    }

    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed");

        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/tavernlist.fxml");
        stage.setScene(new Scene(scene, 600, 400));
    }



    public void setLocationService(LocationService locationService) {
        log.debug("calling setLocationService(" + locationService + ")");
        this.locationService = locationService;
    }



    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
}
