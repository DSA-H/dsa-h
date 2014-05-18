package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.service.LocationService;

@Service("LocationListController")
public class LocationListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(LocationListController.class);
	private SpringFxmlLoader loader;

	private LocationService locationService;

	@FXML
    private TableView<Location> locationTable;
	@FXML
    private TableColumn nameColumn;
	@FXML
    private TableColumn regionColumn;
	@FXML
    private Button createButton;
	@FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

	@Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize LocationListController");

        // init table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
//                new Callback<TableColumn.CellDataFeatures<Location, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<Location, String> l) {
//                if (l.getValue() != null) {
//                    int locationId = l.getValue().getId();
//                    StringBuilder sb = new StringBuilder();
//                    for (RegionBorder rb : borders) {
//                        // not this region
//                        if (rb.getRegion1().getId() != locationId) {
//                            sb.append(rb.getRegion1().getName());
//                        } else {
//                            sb.append(rb.getRegion2().getName());
//                        }
//                        sb.append(", ");
//                    }
//                    if (sb.length() >= 2) {
//                        sb.delete(sb.length() - 2, sb.length());
//                    }
//                    return new SimpleStringProperty(sb.toString());
//                } else {
//                    return new SimpleStringProperty("");
//                }
//            }
//        });


        ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
        locationTable.setItems(data);
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateButtonPressed - open Location-Details Window");

	Stage stage =  (Stage) locationTable.getScene().getWindow();
	Parent root = (Parent) loader.load("/gui/editlocation.fxml");

	    EditLocationController.setLocation(null);

        stage.setTitle("Ort-Details");
        stage.setScene(new Scene(root, 600, 438));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onEditButtonPressed - open Location-Details Window");

	Stage stage = (Stage) locationTable.getScene().getWindow();
	Parent root = (Parent) loader.load("/gui/editlocation.fxml");

        Location selectedLocation = locationTable.getFocusModel().getFocusedItem();
        EditLocationController.setLocation(selectedLocation);

        stage.setTitle("Location-Details");
        stage.setScene(new Scene(root, 600, 438));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Location");
        Location selectedLocation = locationTable.getFocusModel().getFocusedItem();

        if (selectedLocation != null) {
            log.debug("open Confirm-Delete-Location Dialog");
            Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie den Ort '" + selectedLocation.getName() + "' und alle zugehörigen Händler WTF ja nein ??")
                    .showConfirm(); // TODO was ist hier sinnvoll?
            if(response == Dialog.Actions.YES) {
                locationService.remove(selectedLocation);
                locationTable.getItems().remove(selectedLocation);
            }
        }

        checkFocus();
    }

    @FXML
    private void checkFocus() {
        Location selectedLocation = locationTable.getFocusModel().getFocusedItem();
        if (selectedLocation == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        }
        else{
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }

    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}
}
