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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.*;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.TraderCategoryService;
import sepm.dsa.service.TraderService;

import java.util.ArrayList;
import java.util.Arrays;
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

	ArrayList<String> firstNames = new ArrayList<String>(
			Arrays.asList("Orasilas", "", ""));

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
	private void onSavePressed() {
		log.debug("called onSavePressed");


		//name
		int count = StringUtils.countOccurrencesOf(nameField.getText(), " ");
		if (count != nameField.getText().length()) {
			selectedTrader.setName(nameField.getText());
		} else {
			Dialogs.create()
					.title("Ungültige Eingabe")
					.masthead(null)
					.message("Der Name des Händlers darf nicht nur aus Leerzeichen bestehen!")
					.showWarning();
			return;
		}

		//size
		try {
			selectedTrader.setSize(Integer.parseInt(sizeField.getText()));
		} catch (NumberFormatException e) {
			Dialogs.create()
					.title("Ungültige Eingabe")
					.masthead(null)
					.message("Die Größe des Händler muss eine Zahl sein, die die Anzahl an seiner Waren darstellt!")
					.showWarning();
			return;
		}

		//mut
		try {
			selectedTrader.setMut(Integer.parseInt(muField.getText()));
		} catch (NumberFormatException e) {
			Dialogs.create()
					.title("Ungültige Eingabe")
					.masthead(null)
					.message("Der Mut-Wert des Händler muss eine Zahl sein!")
					.showWarning();
			return;
		}

		//intelligenz
		try {
			selectedTrader.setIntelligence(Integer.parseInt(inField.getText()));
		} catch (NumberFormatException e) {
			Dialogs.create()
					.title("Ungültige Eingabe")
					.masthead(null)
					.message("Der Intelligenz-Wert des Händler muss eine Zahl sein!")
					.showWarning();
			return;
		}

		//charisma
		try {
			selectedTrader.setCharisma(Integer.parseInt(chField.getText()));
		} catch (NumberFormatException e) {
			Dialogs.create()
					.title("Ungültige Eingabe")
					.masthead(null)
					.message("Der Charisma-Wert des Händler muss eine Zahl sein!")
					.showWarning();
			return;
		}

		//convince
		try {
			selectedTrader.setConvince(Integer.parseInt(convinceField.getText()));
		} catch (NumberFormatException e) {
			Dialogs.create()
					.title("Ungültige Eingabe")
					.masthead(null)
					.message("Der Überreden-Wert des Händler muss eine Zahl sein!")
					.showWarning();
			return;
		}

		//location
		Location location = (Location) locationBox.getSelectionModel().getSelectedItem();
		if (location != null) {
			selectedTrader.setLocation(location);
		} else {
			Dialogs.create()
					.title("Ungültige Eingabe")
					.masthead(null)
					.message("Ein Ort muss ausgewählt werden!")
					.showWarning();
			return;
		}

		//category
		TraderCategory category = (TraderCategory) categoryBox.getSelectionModel().getSelectedItem();
		if (category != null) {
			selectedTrader.setCategory(category);
		} else {
			Dialogs.create()
					.title("Ungültige Eingabe")
					.masthead(null)
					.message("Eine Händlerkategorie muss ausgewählt werden!")
					.showWarning();
			return;
		}

		//comment
		selectedTrader.setComment(commentArea.getText());


		if (isNewTrader) {
			traderService.add(selectedTrader);
		} else {
			traderService.update(selectedTrader);
		}

		Stage stage = (Stage) nameField.getScene().getWindow();
		Parent scene = (Parent) loader.load("/gui/traderlist.fxml");
		stage.setScene(new Scene(scene, 600, 400));


	}

	@FXML
	private void onCancelPressed() {
		log.debug("called onCancelPressed");

		Stage stage = (Stage) nameField.getScene().getWindow();
		Parent scene = (Parent) loader.load("/gui/traderlist.fxml");
		stage.setScene(new Scene(scene, 600, 400));
	}

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
			selectedTrader = new Trader();
		} else {
			isNewTrader = false;
		}
		setUp();
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}
}
