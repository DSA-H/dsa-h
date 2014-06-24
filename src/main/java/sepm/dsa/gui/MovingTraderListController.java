package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.TraderService;

import java.util.ArrayList;
import java.util.List;


public class MovingTraderListController extends BaseControllerImpl {

	private static final Logger log = LoggerFactory.getLogger(MovingTraderListController.class);
	private SpringFxmlLoader loader;

	private TraderService traderService;

	private MovingTrader selectedTrader;
    private LocationConnection connection;

	@FXML
	private TableView<MovingTrader> traderTable;
	@FXML
	private TableColumn traderColumn;
	@FXML
	private TableColumn locationColumn;
	@FXML
	private Button detailsButton;
	@FXML
	private Label locationsLabel;
    @FXML
    private Label commentLabel;
    @FXML
    private Accordion accordion;

	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		super.initialize(location, resources);
		log.debug("initialise MovingTraderListController");

        accordion.setExpandedPane(accordion.getPanes().get(0));

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

    @Override
    public void reload() {
        log.debug("reload MovingTraderListController");

        List<Trader> traders = traderService.getAllForLocation(connection.getLocation1());
        traders.addAll(traderService.getAllForLocation(connection.getLocation2()));
        List<MovingTrader> movingTraders = new ArrayList<MovingTrader>();
        for (Trader t : traders) {
            if (t instanceof MovingTrader) {
                movingTraders.add((MovingTrader) t);
            }
        }
	    traderTable.getItems().setAll(movingTraders);

        commentLabel.setText(connection.getComment());

        checkFocus();
    }

    @FXML
	private void onDetailsButtonPressed() {
		log.debug("calling onDetailsPressed");
        selectedTrader = traderTable.getSelectionModel().getSelectedItem();

        if(selectedTrader == null) {
            throw new DSAValidationException("Kein Händler ausgegewählt!");
        }

		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/traderdetails.fxml", stage);
		stage.setTitle("Händler-Details");

		TraderDetailsController controller = loader.getController();
		controller.setTrader(selectedTrader);
        controller.reload();

		stage.setScene(new Scene(scene, 830, 781));
		stage.setResizable(false);
		stage.show();
	}

    @FXML
    private void onClosePressed() {
        Stage stage = (Stage)traderTable.getScene().getWindow();
        stage.close();
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
        this.connection = connection;
        locationsLabel.setText("in den Orten " + connection.getLocation1() + " und " + connection.getLocation2());
	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}
}
