package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.model.CurrencySet;
import sepm.dsa.service.CurrencyService;
import sepm.dsa.service.CurrencySetService;
import sepm.dsa.service.SaveCancelService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EditCurrencySetController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(EditCurrencySetController.class);
    private SpringFxmlLoader loader;

    private CurrencyService currencyService;
    private CurrencySetService currencySetService;
    private SaveCancelService saveCancelService;

    private CurrencySet selectedCurrencySet;

    @FXML
    private TextField tf_Name;
    @FXML
    private ChoiceBox<Currency> choice_Currency1;
    @FXML
    private ChoiceBox<Currency> choice_Currency2;
    @FXML
    private ChoiceBox<Currency> choice_Currency3;
    @FXML
    private ChoiceBox<Currency> choice_Currency4;
    @FXML
    private ChoiceBox<Currency> choice_Currency5;
    private ChoiceBox<Currency>[] choiceBoxes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        choiceBoxes = new ChoiceBox[] { choice_Currency1, choice_Currency2, choice_Currency3, choice_Currency4, choice_Currency5 };
    }

    @Override
    public void reload() {
        log.debug("reload EditCurrencySetController");
        Currency[] tempCurrencies = new Currency[] {
                choice_Currency1.getSelectionModel().getSelectedItem(),
                choice_Currency2.getSelectionModel().getSelectedItem(),
                choice_Currency3.getSelectionModel().getSelectedItem(),
                choice_Currency4.getSelectionModel().getSelectedItem(),
                choice_Currency5.getSelectionModel().getSelectedItem()
        };

        List<Currency> availableCurrencies = currencyService.getAll();
        availableCurrencies.add(0, null);

        for (ChoiceBox<Currency> choiceBox : choiceBoxes) {
            choiceBox.getItems().setAll(availableCurrencies);
        }

        if (isNew()) {
            for (int i=0; i<choiceBoxes.length; i++) {
                choiceBoxes[i].getSelectionModel().select(tempCurrencies[i]);
            }
        } else {
            tf_Name.setText(selectedCurrencySet.getName());
            List<Currency> currencies = currencyService.getAllByCurrencySet(selectedCurrencySet);
            int i = 0;
            for (Currency c : currencies) {
                choiceBoxes[i].getSelectionModel().select(c);
                i++;
            }
            for (; i<choiceBoxes.length; i++) {
                choiceBoxes[i].getSelectionModel().select(null);
            }
        }
    }

    private boolean isNew() {
        return selectedCurrencySet.getId() == null;
    }

    public void setCurrencySet(CurrencySet selectedCurrencySet) {
        if (selectedCurrencySet == null) {
            this.selectedCurrencySet = new CurrencySet();
        } else {
            this.selectedCurrencySet = selectedCurrencySet;
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        saveCancelService.cancel();
        Stage stage = (Stage) tf_Name.getScene().getWindow();

        Parent scene = (Parent) loader.load("/gui/currencysetlist.fxml", stage);
        CurrencySetListController ctrl = loader.getController();
        ctrl.reload();

        stage.setScene(new Scene(scene, 600, 440));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        if (tf_Name.getText().isEmpty()) {
            throw new DSAValidationException("Bitte Namen eingeben");
        }
        String name = tf_Name.getText();

        selectedCurrencySet.setName(name);
        selectedCurrencySet.clearCurrencies();
        for (ChoiceBox<Currency> choiceBox : choiceBoxes) {
            Currency currency = choiceBox.getSelectionModel().getSelectedItem();
            if (currency != null) {
                selectedCurrencySet.addCurrency(currency);
            }
        }

        if (isNew()) {
            currencySetService.add(selectedCurrencySet);
        } else {
            currencySetService.update(selectedCurrencySet);
        }
        saveCancelService.save();

        // return to currencies-list
        Stage stage = (Stage) tf_Name.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/currencysetlist.fxml", stage);
        CurrencySetListController ctrl = loader.getController();
        ctrl.reload();

        stage.setScene(new Scene(scene, 600, 440));
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

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }
}
