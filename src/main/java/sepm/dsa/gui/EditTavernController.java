package sepm.dsa.gui;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.CurrencySet;
import sepm.dsa.model.Location;
import sepm.dsa.model.ProductQuality;
import sepm.dsa.model.Tavern;
import sepm.dsa.service.CurrencySetService;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.service.TavernService;
import sepm.dsa.util.CurrencyFormatUtil;

import java.util.List;

public class EditTavernController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(EditTavernController.class);
	private SpringFxmlLoader loader;

	private Tavern selectedTavern;
	private TavernService tavernService;
	private LocationService locationService;
    private CurrencySetService currencySetService;
	private SaveCancelService saveCancelService;

	private boolean isNewTavern;
    private CurrencySet defaultCurrencySet;

	@FXML
	private TextField nameField;
	@FXML
	private TextArea commentArea;
	@FXML
	private Label useageLabel;
	@FXML
	private ChoiceBox<ProductQuality> qualityCoicheBox;
	@FXML
	private Label priceLabel;
	@FXML
	private TextField bedsField;


	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		log.debug("initialise EditTavernController");
		qualityCoicheBox.setItems(FXCollections.observableArrayList(ProductQuality.values()));
        defaultCurrencySet = currencySetService.getDefaultCurrencySet();
	}

	@FXML
	private void onSavePressed() {
		log.debug("called onSavePressed");

		String name = nameField.getText();
		int beds = 0;
		try {
			beds = Integer.parseInt(bedsField.getText());
		} catch (NumberFormatException ex) {
			throw new DSAValidationException("Anzahl der Betten muss eine ganze Zahl sein!");
		}
		ProductQuality quality = qualityCoicheBox.getValue();

		// beds or quality change --> calulate new
		if (isNewTavern || beds != selectedTavern.getBeds() || quality != selectedTavern.getQuality()) {
			selectedTavern.setName(name);
			selectedTavern.setQuality(quality);
			selectedTavern.setBeds(beds);
			selectedTavern.setComment(commentArea.getText());
			int usage = tavernService.calculateBedsUseage(selectedTavern);
			selectedTavern.setUsage(usage);
			int price = tavernService.calculatePrice(selectedTavern);
			selectedTavern.setPrice(price);
		} else {
			selectedTavern.setName(name);
			selectedTavern.setQuality(quality);
			selectedTavern.setBeds(beds);
			selectedTavern.setComment(commentArea.getText());
		}

		if (isNewTavern) {
			tavernService.add(selectedTavern);
		} else {
			tavernService.update(selectedTavern);
		}
		saveCancelService.save();

		Stage stage = (Stage) nameField.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void onCancelPressed() {
		log.debug("called onCancelPressed");
		saveCancelService.cancel();
		Stage stage = (Stage) nameField.getScene().getWindow();
		stage.close();
	}

	public void setTavernService(TavernService tavernService) {
		log.debug("calling setTavernService(" + tavernService + ")");
		this.tavernService = tavernService;
	}

	public void setLocationService(LocationService locationService) {
		log.debug("calling setLocationService(" + locationService + ")");
		this.locationService = locationService;
	}

	public void setTavern(Tavern tavern) {
		if (tavern == null) {
			isNewTavern = true;
			selectedTavern = new Tavern();
		} else {
			isNewTavern = false;
			this.selectedTavern = tavern;
			fillGuiWithData(selectedTavern);
		}
	}

	/**
	 * @param tavern must be valid and must not be null
	 */
	private void fillGuiWithData(Tavern tavern) {
		nameField.setText(tavern.getName());
		bedsField.setText("" + tavern.getBeds());
		qualityCoicheBox.getSelectionModel().select(tavern.getQuality());
		useageLabel.setText(tavern.getUsage() + "");
        List<CurrencyAmount> currencyAmounts = currencySetService.toCurrencySet(defaultCurrencySet, tavern.getPrice());
		priceLabel.setText(CurrencyFormatUtil.currencySetShortString(currencyAmounts, ", "));
		commentArea.setText(tavern.getComment() == null ? "" : tavern.getComment());
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}

	public void setSaveCancelService(SaveCancelService saveCancelService) {
		this.saveCancelService = saveCancelService;
	}

	public void setPosition(Point2D pos) {
		selectedTavern.setxPos((int) pos.getX());
		selectedTavern.setyPos((int) pos.getY());
	}

	public void setLocation(Location location) {
		selectedTavern.setLocation(location);
	}

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }
}
