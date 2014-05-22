package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Trader;

import java.util.ArrayList;
import java.util.List;

@Service("TraderDetailsController")
public class TraderDetailsController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(TraderDetailsController.class);
	private SpringFxmlLoader loader;

	private Trader trader;
	private Offer selectedOffer;

	@FXML
	private TableView offerTable;
	@FXML
	private TableColumn amountColumn;
	@FXML
	private TableColumn productColumn;
	@FXML
	private TableColumn localPriceColumn;
	@FXML
	private TableColumn standardPriceColumn;

	@FXML
	private Label nameLabel;
	@FXML
	private Label categoryLabel;

	@FXML
	private TextField difficultyField;
	@FXML
	private Label resultLabel;
	@FXML
	private TextArea commentArea;


	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		log.debug("initialize TraderDetailsController");

		amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
		productColumn.setCellValueFactory(new PropertyValueFactory<>("product"));
		localPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
		standardPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));

	}

	@FXML
	private void onBackPressed() {
		log.debug("called onBackPressed");

		Stage stage = (Stage) offerTable.getScene().getWindow();
		Parent scene = (Parent) loader.load("/gui/traderlist.fxml");
		stage.setScene(new Scene(scene, 600, 400));
	}

	@FXML
	private void onEditPressed() {
		log.debug("called onEditPressed");
		Stage stage = (Stage) offerTable.getScene().getWindow();
		Parent scene = (Parent) loader.load("/gui/edittrader.fxml");
		EditTraderController controller = loader.getController();
		controller.setTrader(trader);
		stage.setScene(new Scene(scene, 600, 400));
	}

	@FXML
	private void onRolePressed() {
		log.debug("called onRolePressed");
		//TODO not part of version 1
	}
	@FXML
	private void onAddPressed() {
		log.debug("called onAddPressed");
		//TODO not part of version 1
	}

	@FXML
	private void onDeletePressed() {
		log.debug("called onDeletePressed");
		//TODO not part of version 1
	}

	@FXML
	private void onTradePressed() {
		log.debug("called onTradePressed");
		//TODO not part of version 1
	}

	public void setTrader(Trader trader) {
		this.trader = trader;

		nameLabel.setText(trader.getName());
		categoryLabel.setText(trader.getCategory().getName());
		commentArea.setText(trader.getComment());

		List<Offer> offers = new ArrayList<>(trader.getOffers());
		offerTable.setItems(FXCollections.observableArrayList(offers));
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}
}
