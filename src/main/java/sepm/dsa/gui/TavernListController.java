package sepm.dsa.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.model.Tavern;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.service.TavernService;

import java.util.List;

public class TavernListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TavernListController.class);
    private SpringFxmlLoader loader;

    private TavernService tavernService;
    private LocationService locationService;
    private SaveCancelService saveCancelService;

    private Tavern selectedTavern;

    @FXML
    private ListView tavernList;
    @FXML
    private ChoiceBox locationBox;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize TavernListController");

        List<Location> locations = locationService.getAll();
        locationBox.setItems(FXCollections.observableArrayList(locations));
        locationBox.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Location>() {
                    @Override
                    public void changed(ObservableValue<? extends Location> selected, Location oldLoc, Location newLoc) {
                        //List<Tavern> taverns = tavernService.getAllByLocation((Location) locationBox.getSelectionModel().getSelectedItem());
                        //tavernList.setItems(FXCollections.observableArrayList(taverns));
                        checkFocus();
                        List<Tavern> taverns = tavernService.getAllByLocation(selected.getValue().getId());
                        tavernList.setItems(FXCollections.observableArrayList(taverns));
                    }
                }
        );
        tavernList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tavern>() {
                    @Override
                    public void changed(ObservableValue<? extends Tavern> selected, Tavern oldTavern, Tavern newTavern) {
                        selectedTavern = newTavern;
                        if (selectedTavern == null) {
                            editButton.setDisable(true);
                            deleteButton.setDisable(true);
                        } else {
                            editButton.setDisable(false);
                            deleteButton.setDisable(false);
                        }
                    }
                }
        );
    }

    @FXML
    private void onCreatePressed() {
        log.debug("called onCreateButtonPressed");

        Stage stage = (Stage) locationBox.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/edittavern.fxml");
        EditTavernController controller = loader.getController();
        controller.setTavern(null);
        stage.setScene(new Scene(scene, 600, 400));
    }

    @FXML
    private void onDeletePressed() {
        log.debug("called onDeleteButtonPressed");
        checkFocus();

        if (selectedTavern != null) {
            log.debug("open Confirm-Delete-Tavern Dialog");
            Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie das Wirtshaus '" + selectedTavern.getName() + "' wirklich löschen")
                    .actions(Dialog.Actions.NO, Dialog.Actions.YES)
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                tavernService.remove(selectedTavern);
	            saveCancelService.save();
                tavernList.getItems().remove(selectedTavern);
            }
        }

        checkFocus();
    }

    @FXML
    private void onEditPressed() {
//        log.debug("called onEditPressed");
//
//        Stage stage = (Stage) locationBox.getScene().getWindow();
//        Parent scene = (Parent) loader.load("/gui/edittavern.fxml");
//        TraderDetailsController controller = loader.getController();
//        checkFocus();
//        controller.setTrader(selectedTavern);
//        stage.setScene(new Scene(scene, 800, 400));
        log.debug("called onEditPressed");

        Stage stage = (Stage) locationBox.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/edittavern.fxml");
        EditTavernController controller = loader.getController();
//        selectedTavern = (Tavern) tavernList.getFocusModel().getFocusedItem();
        checkFocus();
        selectedTavern = (Tavern) tavernList.getFocusModel().getFocusedItem();
        controller.setTavern(selectedTavern);
        stage.setScene(new Scene(scene, 600, 400));
    }

    @FXML
    private void onBackPressed() {
        log.debug("called onBackPressed");

        Stage stage = (Stage) locationBox.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void checkFocus() {
        selectedTavern = (Tavern) tavernList.getFocusModel().getFocusedItem();
        if (selectedTavern == null) {
            editButton.setDisable(true);
            deleteButton.setDisable(true);
        } else {
            editButton.setDisable(false);
            deleteButton.setDisable(false);

        }

    }

    public void setTavernService(TavernService tavernService) {
        this.tavernService = tavernService;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
