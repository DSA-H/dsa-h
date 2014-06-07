package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.*;
import sepm.dsa.service.TraderService;

import java.util.ArrayList;
import java.util.List;


public class MovingTraderListController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(MovingTraderListController.class);
	private SpringFxmlLoader loader;

	private TraderService traderService;

	private MovingTrader selectedTrader;

	@FXML
	private TableView traderTable;
	@FXML
	private TableColumn traderColumn;
	@FXML
	private TableColumn locationColumn;
	@FXML
	private Button detailsButton;
	@FXML
	private Label locationsLabel;


	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		log.debug("initialise MovingTraderListController");

		traderColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<MovingTrader, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<MovingTrader, String> mt) {
				if (mt.getValue() != null) {
					return new SimpleStringProperty(mt.getValue().toString());
				} else {
					return new SimpleStringProperty("");
				}
			}
		});
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
	}

	@FXML
	private void onDetailsButtonPressed() {
		log.debug("calling onDetailsPressed");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/traderdetails.fxml");
		stage.setTitle("Händler-Details");

		TraderDetailsController controller = loader.getController();
		controller.setTrader(selectedTrader);
		stage.setScene(new Scene(scene, 830, 781));
		stage.setResizable(false);
		stage.showAndWait();
	}

	@FXML
	private void checkFocus() {
		selectedTrader = (MovingTrader) traderTable.getSelectionModel().getSelectedItem();
		if (selectedTrader == null) {
			selectedTrader = (MovingTrader) traderTable.getFocusModel().getFocusedItem();
		}
		if (selectedTrader == null) {
			detailsButton.setDisable(true);
		} else {
			detailsButton.setDisable(false);
		}
	}

	public void setLocationConnection(LocationConnection connection) {
		locationsLabel.setText("in den Orten " + connection.getLocation1() + " und " + connection.getLocation2());

		List<Trader> traders = traderService.getAllForLocation(connection.getLocation1());
		traders.addAll(traderService.getAllForLocation(connection.getLocation2()));
		List<MovingTrader> movingTraders = new ArrayList<MovingTrader>();
		for (Trader t : traders) {
			if (t instanceof MovingTrader) {
				movingTraders.add((MovingTrader) t);
			}
		}
		traderTable.setItems(FXCollections.observableArrayList(movingTraders));

		checkFocus();
	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}
}
