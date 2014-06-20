package sepm.dsa.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.service.CurrencyService;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class CurrencyCalculateController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(CurrencyCalculateController.class);
    SpringFxmlLoader loader;

    private CurrencyService currencyService;

    @FXML
    private ChoiceBox<Currency> choiceFirst;
    @FXML
    private ChoiceBox<Currency> choiceSecond;
    @FXML
    private TextField textFirst;
    @FXML
    private Label labelresult;
    @FXML
    private Label labelvon;
    @FXML
    private Label labelin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        choiceFirst.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Currency>() {
            @Override
            public void changed(ObservableValue<? extends Currency> observable, Currency oldValue, Currency newValue) {
                if(newValue == null) {
                    return;
                }
                labelvon.setText(newValue.getName());
            }
        });
        choiceSecond.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Currency>() {
            @Override
            public void changed(ObservableValue<? extends Currency> observable, Currency oldValue, Currency newValue) {
                if(newValue == null) {
                    return;
                }
                labelin.setText(newValue.getName());
            }
        });
    }

    @Override
    public void reload() {
        log.debug("reload CurrencyCalculateController");
        // init table
        List<Currency> currencies = currencyService.getAll();
        Currency selectedFirst = choiceFirst.getSelectionModel().getSelectedItem();
	    choiceFirst.getItems().setAll(currencies);
        choiceFirst.getSelectionModel().select(selectedFirst);
        if(selectedFirst == null) {
            choiceFirst.getSelectionModel().selectFirst();
        }
        Currency secondSelected = choiceSecond.getSelectionModel().getSelectedItem();
	    choiceSecond.getItems().setAll(currencies);
        choiceSecond.getSelectionModel().select(secondSelected);
        if(secondSelected == null) {
            choiceSecond.getSelectionModel().selectFirst();
        }

        labelvon.setText(choiceFirst.getSelectionModel().getSelectedItem().getName());
        labelin.setText(choiceSecond.getSelectionModel().getSelectedItem().getName());
    }

    @FXML
    private void onCalculatePressed() {
        log.debug("onCalculateClicked - calculate Currency");
        Integer amountToExchange;
        try {
            amountToExchange = Integer.parseInt(textFirst.getText());
        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Menge von zu Wechselndem Geld muss eine ganze Zahl sein!");
        }

        Currency currencyFrom = choiceFirst.getSelectionModel().getSelectedItem();
        Currency currencyTo = choiceSecond.getSelectionModel().getSelectedItem();

        float exchangeAmount = currencyFrom.getValueToBaseRate()*amountToExchange;
        exchangeAmount = exchangeAmount / currencyTo.getValueToBaseRate();

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);
        labelresult.setText(String.format(df.format(exchangeAmount)));
    }

    @FXML
    private void onCloseClicked() {
        Stage stage = (Stage)choiceFirst.getScene().getWindow();
        stage.close();
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

}
