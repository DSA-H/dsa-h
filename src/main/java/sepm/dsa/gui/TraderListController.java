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
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TraderService;

import java.util.ArrayList;
import java.util.List;

@Service("TraderListController")
public class TraderListController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(TraderListController.class);
	private SpringFxmlLoader loader;

	private TraderService traderService;
	private LocationService locationService;

	private Trader selectedTrader;

	@FXML
	private ListView traderList;
	@FXML
	private ChoiceBox locationBox;
	@FXML
	private Button detailsButton;
	@FXML
	private Button deleteButton;

	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		log.debug("initialize TraderListController");

		List<Location> locations = locationService.getAll();
		locationBox.setItems(FXCollections.observableArrayList(locations));
		locationBox.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Location>() {
					@Override
					public void changed(ObservableValue<? extends Location> selected, Location oldLoc, Location newLoc) {
						List<Trader> traders = traderService.getAllForLocation((Location) locationBox.getSelectionModel().getSelectedItem());
						traderList.setItems(FXCollections.observableArrayList(traders));
						checkFocus();
					}
				}
		);
		traderList.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Trader>() {
					@Override
					public void changed(ObservableValue<? extends Trader> selected, Trader oldTra, Trader newTra) {
						selectedTrader = newTra;
						if (selectedTrader == null) {
							detailsButton.setDisable(true);
							deleteButton.setDisable(true);
						} else {
							detailsButton.setDisable(false);
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
		Parent scene = (Parent) loader.load("/gui/edittrader.fxml");
		EditTraderController controller = loader.getController();
		controller.setTrader(null);
		stage.setScene(new Scene(scene, 600, 400));
	}

	@FXML
	private void onDeletePressed() {
		log.debug("called onDeleteButtonPressed");
		checkFocus();
		if (selectedTrader != null) {
			log.debug("open Confirm-Delete-Trader Dialog");
			Action response = Dialogs.create()
					.title("Löschen?")
					.masthead(null)
					.message("Wollen Sie den Händer '" + selectedTrader.getName() + "' wirklich löschen")
					.showConfirm();
			if (response == Dialog.Actions.YES) {
				traderService.remove(selectedTrader);
				traderList.getItems().remove(selectedTrader);
			}
		}

		checkFocus();
	}

	@FXML
	private void onDetailsPressed() {
		log.debug("called onDetailsPressed");

		Stage stage = (Stage) locationBox.getScene().getWindow();
		Parent scene = (Parent) loader.load("/gui/traderdetails.fxml");
		TraderDetailsController controller = loader.getController();
		checkFocus();
		controller.setTrader(selectedTrader);
		stage.setScene(new Scene(scene, 800, 400));
	}

	@FXML
	private void onBackPressed() {
		log.debug("called onBackPressed");

		Stage stage = (Stage) locationBox.getScene().getWindow();
		stage.close();
	}



	@FXML
	private void checkFocus() {
		Trader selectedTrader = (Trader) traderList.getFocusModel().getFocusedItem();
		if (selectedTrader == null) {
			detailsButton.setDisable(true);
			deleteButton.setDisable(true);
		} else {
			detailsButton.setDisable(false);
			deleteButton.setDisable(false);
		}

	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}
}
