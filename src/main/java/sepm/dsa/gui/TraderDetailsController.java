package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Offer;
import sepm.dsa.service.TraderService;

@Service("TraderDetailsController")
public class TraderDetailsController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(TraderDetailsController.class);
	private SpringFxmlLoader loader;

	private TraderService traderService;

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
	private TableColumn standarfPriceColumn;

	@FXML
	private TextField difficultyField;
	@FXML
	private Label resultLabel;


	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		log.debug("initialize TraderDetailsController");
	}

	@FXML
	private void onBackPressed() {
		log.debug("called onBackPressed");
	}

	@FXML
	private void onEditPressed() {
		log.debug("called onEditPressed");
	}

	@FXML
	private void onRolePressed() {
		log.debug("called onRolePressed");
	}
	@FXML
	private void onAddPressed() {
		log.debug("called onAddPressed");
	}
	@FXML
	private void onDeletePressed() {
		log.debug("called onDeletePressed");
	}

	public void setTraderService(TraderService traderService) { this.traderService = traderService; }

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}
}
