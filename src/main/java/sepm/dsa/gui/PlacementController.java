package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.model.Tavern;
import sepm.dsa.model.Trader;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TavernService;
import sepm.dsa.service.TraderService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PlacementController implements Initializable{
	private static final Logger log = LoggerFactory.getLogger(PlacementController.class);
	private SpringFxmlLoader loader;
	private LocationService locationService;
	private TraderService traderService;
	private TavernService tavernService;
	private Location selectedLocation;
	private Point2D pos;
	private Object selectedObj;

	@FXML
	private Label headline;
	@FXML
	private ChoiceBox choiceBox;
	@FXML
	private Button newButton;
	@FXML
	private Button newTavernButton;
	@FXML
	private Button commitButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@FXML
	private void onConfirmPressed() {
		if (choiceBox.getSelectionModel().getSelectedItem() == null) {
			Dialogs.create()
					.title("Fehler")
					.masthead(null)
					.message("Es muss eine Auswahl getroffen werden!")
					.showWarning();
			return;
		}
		if (selectedLocation == null) {
			Location location = (Location) choiceBox.getSelectionModel().getSelectedItem();
			location.setxCoord((int) pos.getX());
			location.setyCoord((int) pos.getY());
			locationService.update(location);
		} else {
			Object obj = choiceBox.getSelectionModel().getSelectedItem();
			if (obj instanceof Trader) {
				Trader trader = (Trader) obj;
				trader.setxPos((int) pos.getX());
				trader.setyPos((int) pos.getY());
				traderService.update(trader);
			} else {
				Tavern tavern = (Tavern) obj;
				tavern.setxPos((int) pos.getX());
				tavern.setyPos((int) pos.getY());
				tavernService.update(tavern);
			}
		}
		Stage stage = (Stage) choiceBox.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void onNewPressed() {
		if (selectedLocation == null) {
			EditLocationController.setLocation(null);

			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/editlocation.fxml");

			EditLocationController controller = loader.getController();
			controller.setPosition(pos);

			stage.setTitle("Ort erstellen");
			stage.setScene(new Scene(scene, 900, 438));
			stage.setResizable(false);
			stage.showAndWait();
		} else {
			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/edittrader.fxml");
			stage.setTitle("Händler erstellen");

			EditTraderController controller = loader.getController();
			controller.setTrader(null);
			controller.setPosition(pos);
			controller.setLocation(selectedLocation);
			stage.setScene(new Scene(scene, 600, 400));
			stage.setResizable(false);
			stage.showAndWait();
		}

		Stage stage = (Stage) choiceBox.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void onNewTavernPressed() {
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/edittavern.fxml");
		stage.setTitle("Wirtshaus erstellen");

		EditTavernController controller = loader.getController();
		controller.setTavern(null);
		controller.setPosition(pos);
		controller.setLocation(selectedLocation);
		stage.setScene(new Scene(scene, 600, 400));
		stage.setResizable(false);
		stage.showAndWait();

		stage = (Stage) choiceBox.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void onBackPressed() {
		Stage stage = (Stage) choiceBox.getScene().getWindow();
		stage.close();
	}

	public void setUp(Location location, Point2D pos, Object selectedObj, boolean noMap) {
		this.selectedLocation = location;
		this.selectedObj = selectedObj;
		this.pos = pos;
		if (noMap) {
			choiceBox.setVisible(false);
			commitButton.setVisible(false);
			headline.setText("Händler/Wirtshaus platzieren");
			newButton.setText("Neuer Händler");
			newTavernButton.setVisible(true);
		} else {
			if (selectedLocation == null) {
				headline.setText("Ort platzieren");
				newButton.setText("Neuer Ort");
				newTavernButton.setVisible(false);
				List<Location> locations = locationService.getAll();
				choiceBox.setItems(FXCollections.observableArrayList(locations));
			} else {
				headline.setText("Händler/Wirtshaus platzieren");
				newButton.setText("Neuer Händler");
				newTavernButton.setVisible(true);
				List<Trader> traders = traderService.getAllForLocation(selectedLocation);
				List<Tavern> taverns = tavernService.getAllByLocation(selectedLocation.getId());
				List<Object> all = new ArrayList<Object>();
				for (Trader t : traders) {
					all.add(t);
				}
				for (Tavern t : taverns) {
					all.add(t);
				}
				choiceBox.setItems(FXCollections.observableArrayList(all));
			}
			if (selectedObj != null) {
				choiceBox.getSelectionModel().select(selectedObj);
			}
		}
	}

	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setTavernService(TavernService tavernService) {
		this.tavernService = tavernService;
	}
}
