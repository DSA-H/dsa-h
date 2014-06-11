package sepm.dsa.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.service.CurrencyService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

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
    public void reload() {
        log.debug("reload CurrencyCalculateController");
        // init table
        List<Currency> currencies = currencyService.getAll();
        choiceFirst.setItems(FXCollections.observableArrayList(currencies));
        choiceFirst.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Currency>() {
            @Override
            public void changed(ObservableValue<? extends Currency> observable, Currency oldValue, Currency newValue) {
                labelvon.setText(newValue.getName());
            }
        });
        choiceSecond.setItems(FXCollections.observableArrayList(currencies));
        choiceSecond.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Currency>() {
            @Override
            public void changed(ObservableValue<? extends Currency> observable, Currency oldValue, Currency newValue) {
                labelin.setText(newValue.getName());
            }
        });

        if (!currencies.isEmpty()) {
            choiceFirst.getSelectionModel().select(0);
            choiceSecond.getSelectionModel().select(0);

            labelvon.setText(choiceFirst.getSelectionModel().getSelectedItem().getName());
            labelin.setText(choiceSecond.getSelectionModel().getSelectedItem().getName());
        }
    }

    @FXML
    private void onCalculatePressed() {
        log.debug("onCalculateClicked - calculate Currency");
        Integer amountToExchange;
        try {
            amountToExchange = Integer.parseInt(textFirst.getText());

        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Menge von zu Wechselndem Geld muss eine Zahl sein!");
        }

        CurrencyAmount exchangeResult = currencyService.exchange(choiceFirst.getSelectionModel().getSelectedItem(), choiceSecond.getSelectionModel().getSelectedItem(), amountToExchange);
        Integer exchangeAmount = exchangeResult.getAmount();

//        exchangeAmount.setScale(2, BigDecimal.ROUND_DOWN);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(0);
        df.setGroupingUsed(false);
        labelresult.setText(String.format(df.format(exchangeAmount)));
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

}
