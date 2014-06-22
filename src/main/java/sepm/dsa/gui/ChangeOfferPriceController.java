package sepm.dsa.gui;


import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.util.List;

public class ChangeOfferPriceController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(ChangeOfferPriceController.class);
    private SaveCancelService saveCancelService;
    private CurrencySetService currencySetService;
    private CurrencyService currencyService;
    private Offer selectedOffer;
    private CurrencySet selectedCurrencySet;

    @FXML
    private Label offerLabel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private TextField tf_CurrencyAmount1;
    @FXML
    private TextField tf_CurrencyAmount2;
    @FXML
    private TextField tf_CurrencyAmount3;
    @FXML
    private TextField tf_CurrencyAmount4;
    @FXML
    private TextField tf_CurrencyAmount5;
    private TextField[] tf_CurrencyAmounts;
    @FXML
    private Label lbl_CurrencyAmount1;
    @FXML
    private Label lbl_CurrencyAmount2;
    @FXML
    private Label lbl_CurrencyAmount3;
    @FXML
    private Label lbl_CurrencyAmount4;
    @FXML
    private Label lbl_CurrencyAmount5;
    private Label[] lbl_CurrencyAmounts;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize ChangeOfferPriceController");

        lbl_CurrencyAmounts =
                new Label[] {
                        lbl_CurrencyAmount1,
                        lbl_CurrencyAmount2,
                        lbl_CurrencyAmount3,
                        lbl_CurrencyAmount4,
                        lbl_CurrencyAmount5};

        tf_CurrencyAmounts =
                new TextField[] {
                        tf_CurrencyAmount1,
                        tf_CurrencyAmount2,
                        tf_CurrencyAmount3,
                        tf_CurrencyAmount4,
                        tf_CurrencyAmount5};

        selectedCurrencySet = currencySetService.getDefaultCurrencySet();
    }

    @Override
    public void reload() {
        log.debug("reload ChangeOfferPriceController");
    }

    private void setUp() {
        log.info("calling setUp()");
        StringBuilder sb = new StringBuilder();
        sb.append(selectedOffer.getProduct().getName());
        if(selectedOffer.getProduct().getQuality()) {
            sb.append(" (").append(selectedOffer.getQuality()).append(")");
        }
        offerLabel.setText(sb.toString());

        int i=0;
        for (Currency c : currencyService.getAllByCurrencySet(selectedCurrencySet)) {
            log.info(lbl_CurrencyAmounts + ": " + lbl_CurrencyAmounts[i] + " " + c.getName());
            lbl_CurrencyAmounts[i].setText(c.getName());
            tf_CurrencyAmounts[i].setDisable(false);
            i++;
        }
        for (; i<5; i++) {
            lbl_CurrencyAmounts[i].setText("");
            tf_CurrencyAmounts[i].setText("");
            tf_CurrencyAmounts[i].setDisable(true);
        }

        List<CurrencyAmount> currencyAmounts = currencySetService.toCurrencySet(selectedCurrencySet, selectedOffer.getPricePerUnit());
        i=0;
        for (CurrencyAmount c : currencyAmounts) {
            tf_CurrencyAmounts[i].setText(c.getAmount().toString());
            i++;
        }
    }

    @FXML
    private void onSavePressed() {
        log.debug("called onSavePressed");

        int price = 0;
        int i = 0;
        try {
            for (Currency c : currencyService.getAllByCurrencySet(selectedCurrencySet)) {
                int value = currencyService.exchangeToBaseRate(c, Integer.parseInt(tf_CurrencyAmounts[i].getText()));
                if (value < 0) {
                    throw new DSAValidationException("Preis muss > 0 sein");
                }
                price += value;
                i++;
            }
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Der Preis muss aus lauter ganzen Zahlen bestehen!");
        }

        selectedOffer.setPricePerUnit(price);

        saveCancelService.save();
        Stage stage = (Stage) offerLabel.getScene().getWindow();
        stage.close();
    }


    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed - return to ChangeOfferPriceController");
        saveCancelService.cancel();
        Stage stage = (Stage) offerLabel.getScene().getWindow();
        stage.close();
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setOffer(Offer offer) {
        if(offer == null) {
            throw new IllegalArgumentException("Offer cannot be null");
        }
        this.selectedOffer = offer;
        setUp();
    }
}
