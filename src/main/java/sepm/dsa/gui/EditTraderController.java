package sepm.dsa.gui;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.*;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TraderCategoryService;
import sepm.dsa.service.TraderService;

import java.awt.*;
import java.util.*;
import java.util.List;

@Service("EditRegionController")
public class EditTraderController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(EditTraderController.class);
	private SpringFxmlLoader loader;

	private Trader selectedTrader;
	private TraderService traderService;
	private TraderCategoryService categoryService;
	private LocationService locationService;

	private boolean isNewTrader;

	@FXML
	private TextField nameField;
	@FXML
	private TextField sizeField;
	@FXML
	private TextField muField;
	@FXML
	private TextField inField;
	@FXML
	private TextField chField;
	@FXML
	private TextField convinceField;
	@FXML
	private ChoiceBox locationBox;
	@FXML
	private ChoiceBox categoryBox;
	@FXML
	private TextArea commentArea;



	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		log.debug("initialise EditTraderController");

		//init choiceBoxes
		List<TraderCategory> categories = categoryService.getAll();
		List<Location> locations = locationService.getAll();
		categoryBox.setItems(FXCollections.observableArrayList(categories));
		locationBox.setItems(FXCollections.observableArrayList(locations));

	}

	private void setUp() {
		log.debug("calling setUp");

		if (isNewTrader){
			generateRandoms();
		} else {
			nameField.setText(selectedTrader.getName());
			sizeField.setText(""+selectedTrader.getSize());
			categoryBox.getSelectionModel().select(selectedTrader.getCategory());
			locationBox.getSelectionModel().select(selectedTrader.getLocation());
			muField.setText(""+selectedTrader.getMut());
			inField.setText(""+selectedTrader.getIntelligence());
			chField.setText(""+selectedTrader.getCharisma());
			convinceField.setText(""+selectedTrader.getConvince());
			commentArea.setText(selectedTrader.getComment());
		}
	}

	private void generateRandoms() {
		nameField.setText("Random Name");
		muField.setText("14");
		inField.setText("14");
		chField.setText("14");
		convinceField.setText("5");
	}

	@FXML
	private void onSavePressed() {}

	@FXML
	private void onCancelPressed() {}

	public void setTraderService(TraderService traderService) {
		log.debug("calling setTraderService(" + traderService + ")");
		this.traderService = traderService;
	}

	public void setTraderCategoryService(TraderCategoryService traderCategoryService) {
		log.debug("calling setTraderCategoryService(" + traderCategoryService + ")");
		this.categoryService = traderCategoryService;
	}

	public void setLocationService(LocationService locationService) {
		log.debug("calling setLocationService(" + locationService + ")");
		this.locationService = locationService;
	}

	public void setTrader(Trader trader) {
		log.debug("calling setTrader(" + trader + ")");
		selectedTrader = trader;
		if (trader == null) {
			isNewTrader = true;
		} else {
			isNewTrader = false;
		}
		setUp();
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}
}
