package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.service.CurrencyService;

import java.math.BigDecimal;

public class EditCurrencyController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditCurrencyController.class);
    private SpringFxmlLoader loader;
    private SessionFactory sessionFactory;

    private CurrencyService currencyService;
    private static Currency selectedCurrency;
    private boolean isNewCurrency;

    @FXML
    private TextField nameField;
    @FXML
    private TextField valueToBaseRateField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize EditCurrencyController");

        if (selectedCurrency != null) {
            isNewCurrency = false;
            nameField.setText(selectedCurrency.getName());
            valueToBaseRateField.setText(selectedCurrency.getValueToBaseRate().toString());
        } else {
            isNewCurrency = true;
            selectedCurrency = new Currency();
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
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
        BigDecimal exchangeToBase;
        try {
            exchangeToBase = new BigDecimal(valueToBaseRateField.getText());

        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Wechselkurs muss eine Zahl sein!");
        }

        selectedCurrency.setName(name);
        selectedCurrency.setValueToBaseRate(exchangeToBase);

        if (isNewCurrency) {
            currencyService.add(selectedCurrency);
        } else {
            currencyService.update(selectedCurrency);
        }

        // return to currencies-list
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/currencyList.fxml");
        stage.setScene(new Scene(scene, 600, 438));
    }

    public static void setCurrency(Currency currency) {
        selectedCurrency = currency;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
