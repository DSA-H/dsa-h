package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.service.CurrencyService;
import sepm.dsa.service.SaveCancelService;

import java.math.BigDecimal;

public class EditCurrencyController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(EditCurrencyController.class);
    private SpringFxmlLoader loader;

    private CurrencyService currencyService;
    private SaveCancelService saveCancelService;

    private Currency selectedCurrency;
    private boolean isNewCurrency;

    @FXML
    private TextField nameField;
    @FXML
    private TextField shortNameField;
    @FXML
    private TextField valueToBaseRateField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    @Override
    public void reload() {
        log.debug("reload EditCurrencyController");
        if (selectedCurrency != null) {
            isNewCurrency = false;
            nameField.setText(selectedCurrency.getName());
            shortNameField.setText(selectedCurrency.getShortName());
            valueToBaseRateField.setText(selectedCurrency.getValueToBaseRate().toString());
        } else {
            isNewCurrency = true;
            selectedCurrency = new Currency();
        }
    }

    public void setCurrency(Currency selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        saveCancelService.cancel();
        Stage stage = (Stage) nameField.getScene().getWindow();

        Parent scene = (Parent) loader.load("/gui/currencyList.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        if (nameField.getText().isEmpty()) {
            throw new DSAValidationException("Bitte Namen eingeben");
        }
        String name = nameField.getText();
        String shortName = shortNameField.getText();
        Integer exchangeToBase = null;
        try {
            exchangeToBase = Integer.parseInt(valueToBaseRateField.getText());

        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Wechselkurs muss eine Zahl sein!");
        }
        if (exchangeToBase <= 0){
            throw new DSAValidationException("Wechselkurs muss Zahl > 0 sein");
        }

        selectedCurrency.setName(name);
        selectedCurrency.setValueToBaseRate(exchangeToBase);
        selectedCurrency.setShortName(shortName);

        if (isNewCurrency) {
            currencyService.add(selectedCurrency);
        } else {
            currencyService.update(selectedCurrency);
        }
        saveCancelService.save();

        // return to currencies-list
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/currencyList.fxml");
        stage.setScene(new Scene(scene, 600, 438));
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
