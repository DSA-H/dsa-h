package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.service.CurrencyService;

import java.math.BigDecimal;
import java.util.List;

public class CurrencyCalculateController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(CurrencyCalculateController.class);
    SpringFxmlLoader loader;

    private CurrencyService currencyService;
    private SessionFactory sessionFactory;

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
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize CurrencyListController");
        // init table
        List<Currency> currencies = currencyService.getAll();
        choiceFirst.setItems(FXCollections.observableArrayList(currencies));
        choiceSecond.setItems(FXCollections.observableArrayList(currencies));

        choiceFirst.getSelectionModel().select(0);
        choiceSecond.getSelectionModel().select(0);

        labelvon.setText(choiceFirst.getSelectionModel().getSelectedItem().getName());
        labelin.setText(choiceSecond.getSelectionModel().getSelectedItem().getName());
    }

    @FXML
    private void onCalculatePressed() {
        log.debug("onCalculateClicked - calculate Currency");
        BigDecimal first;
        try {
            first = new BigDecimal(textFirst.getText());

        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Wechselkurs muss eine Zahl sein!");
        }

        BigDecimal rate = choiceFirst.getSelectionModel().getSelectedItem().getValueToBaseRate().divide(choiceSecond.getSelectionModel().getSelectedItem().getValueToBaseRate());
        BigDecimal second = first.multiply(rate);
        labelresult.setText(second.toString());

    }

    /*@FXML
    private void checkFocus() {

    }*/

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
