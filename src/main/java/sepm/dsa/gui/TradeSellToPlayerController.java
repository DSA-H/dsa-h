package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;
import sepm.dsa.util.CurrencyFormatUtil;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TradeSellToPlayerController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(TradeSellToPlayerController.class);

    @FXML
    private Label selectedOffer;
    @FXML
    private ChoiceBox<Unit> selectedUnit;
    @FXML
    private ChoiceBox<Player> selectedPlayer;
    @FXML
    private TextField selectedAmount;
    @FXML
    private ChoiceBox<CurrencySet> selectedCurrency;
    @FXML
    private TextField selectedDiscount;

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

    private List<Currency> currencies;

    private Trader trader;
    private Offer offer;
    private TraderService traderService;
    private SaveCancelService saveCancelService;
    private UnitService unitService;
    private CurrencyService currencyService;
    private CurrencySetService currencySetService;
    private PlayerService playerService;

    @Override
    public void reload() {
        log.debug("reload TradeSellToPlayerController");
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

        selectedDiscount.setText("0");

        selectedCurrency.setItems(FXCollections.observableArrayList(currencySetService.getAll()));
        selectedUnit.setItems(FXCollections.observableArrayList(unitService.getAllByType(offer.getProduct().getUnit().getUnitType())));
//        selectedPrice.setText(offer.getPricePerUnit().toString());
        selectedPlayer.setItems(FXCollections.observableArrayList(playerService.getAll()));

        //select default unit & currency
        CurrencySet preferredCurrency = trader.getLocation().getRegion().getPreferredCurrencySet();
        if (preferredCurrency != null) {
            selectedCurrency.getSelectionModel().select(preferredCurrency);
        }
        selectedUnit.getSelectionModel().select(offer.getProduct().getUnit());

        StringBuilder sb = new StringBuilder();
        sb.append(offer.getProduct().getName());
        if (offer.getProduct().getQuality()) {
            sb.append(" (" + offer.getQuality().getName() + ")");
        }
        selectedOffer.setText(sb.toString());
        selectedAmount.setText("1");
        selectedAmount.textProperty().addListener((observable, oldValue, newValue) -> updatePrice());
        selectedCurrency.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePrice());

        refreshPriceView();
    }

    private CurrencySet selectedCurrencySet() {
        return selectedCurrency.getSelectionModel().getSelectedItem();
    }

    private ProductQuality selectedQuality() {
        return offer.getQuality();
    }

    private Product selectedProduct() {
        return offer.getProduct();
    }

    private void updatePrice() {
        log.info("calling updatePrice()");
        refreshPriceView();
        if (offer.getProduct() != null) {
            log.info(selectedQuality() + ", " + selectedProduct() + " " + trader);
            int setQuality = traderService.calculatePricePerUnit(selectedQuality(), selectedProduct(), trader);
            int amount = 0;

            //##### get amount
            if (!selectedAmount.getText().isEmpty()) {
                try {
                    amount = new Integer(selectedAmount.getText());

                } catch (NumberFormatException ex) {
                    throw new DSAValidationException("Menge muss eine ganze Zahl sein!");
                }
            }
            int baseRatePrice = setQuality * amount;
//            selectedPrice.setText(Integer.toString());

            List<CurrencyAmount> currencyAmounts = currencySetService.toCurrencySet(selectedCurrencySet(), baseRatePrice);
            int i=0;
            for (CurrencyAmount c : currencyAmounts) {
//                lbl_CurrencyAmounts[i].setText(c.getCurrency().getName());
                tf_CurrencyAmounts[i].setText(c.getAmount().toString());
                i++;
            }
        }
    }

    private void refreshPriceView() {
        log.info("calling refreshPriceView()");
        CurrencySet selected = selectedCurrencySet();
        if (selected == null) {
            selected = currencySetService.getDefaultCurrencySet();
        }
        currencies = currencyService.getAllByCurrencySet(selectedCurrencySet());
        int i=0;
        for (Currency c : currencyService.getAllByCurrencySet(selected)) {
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

    }

    @FXML
    private void calculateDiscount() {

        Player selPlayer = this.selectedPlayer.getSelectionModel().getSelectedItem();
        int amount = 1;

        if (selectedAmount.getText().isEmpty()) {
            throw new DSAValidationException("Bitte Menge eingeben");
        }
        try {
            amount = new Integer(selectedAmount.getText());

        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Menge muss eine ganze Zahl sein!");
        }
        if (amount <= 0) {
            throw new DSAValidationException("Menge muss > 0 sein");
        }
        if (selectedUnit.getSelectionModel().getSelectedItem() == null) {
            throw new DSAValidationException("Die Einheit wurde nicht gewählt!");
        }

        if (selPlayer == null) {
            throw new DSAValidationException("Ein Spieler muss gewählt werden!");
        }
        Integer discount = traderService.suggesstDiscount(trader, selPlayer, offer.getProduct(), offer.getQuality(), selectedUnit.getSelectionModel().getSelectedItem(), amount);

//        Integer discountAmount = null;
//        try {
//            discountAmount = (Integer.parseInt(selectedPrice.getText()) * discount) / 100;
//        } catch (NumberFormatException ex) {
//            throw new DSAValidationException("Der Preis muss angegeben sein.");
//        }
//        selectedDiscount.setText("" + (discountAmount == null ? 0 : discountAmount));
        selectedDiscount.setText("" + discount);
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");

        Stage stage = (Stage) selectedUnit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSavePressed() {

        log.debug("calling SaveButtonPressed");
        Player playerToCreateDealFor;

        if (selectedAmount.getText().isEmpty()) {
            throw new DSAValidationException("Bitte Menge eingeben");
        }
        int amount = 0;
        try {
            amount = new Integer(selectedAmount.getText());

        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Menge muss eine ganze Zahl sein!");
        }
        if (amount <= 0) {
            throw new DSAValidationException("Menge muss > 0 sein");
        }
        if (selectedPlayer.getSelectionModel().getSelectedItem() != null) {
            playerToCreateDealFor = selectedPlayer.getSelectionModel().getSelectedItem();
        } else {
            throw new DSAValidationException("Ein Spieler muss gewählt werden!");
        }
        if (selectedUnit.getSelectionModel().getSelectedItem() == null) {
            throw new DSAValidationException("Die Einheit wurde nicht gewählt!");
        }
        if (selectedCurrency.getSelectionModel().getSelectedItem() == null) {
            throw new DSAValidationException("Die Währung wurde nicht gewählt!");
        }

        List<CurrencyAmount> currencyAmounts = retrieveCurrencyAmounts();

        //######## Discount stuff --
        //PRICE STUFF ###########
        Integer discount = retrieveDiscount();

        traderService.sellToPlayer(trader, playerToCreateDealFor, offer.getProduct(), offer.getQuality(), selectedUnit.getSelectionModel().getSelectedItem(), amount, currencyAmounts, discount);
        saveCancelService.save();

        saveCancelService.refresh(trader);//, offer, offer.getProduct());
        saveCancelService.refresh(trader.getDeals());

        Stage stage = (Stage) selectedUnit.getScene().getWindow();
        stage.close();
    }

    private List<CurrencyAmount> retrieveCurrencyAmounts() {
        List<CurrencyAmount> result = new ArrayList<>(5);
        try {
            for (int i = 0; i < currencies.size(); i++) {
                CurrencyAmount a = new CurrencyAmount();
                a.setCurrency(currencies.get(i));
                Integer currencyAmount = Integer.parseInt(tf_CurrencyAmounts[i].getText());
                if (currencyAmount < 0) {
                    throw new DSAValidationException("Preis muss > 0 sein");
                }
                a.setAmount(currencyAmount);
                result.add(a);
            }
        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Der Preis muss aus lauter Zahlen bestehen!");
        }
        return result;
    }

    private int retrieveDiscount() {
        int result = 0;
        if (!selectedDiscount.getText().isEmpty()) {
            try {
                result = Integer.parseInt(selectedDiscount.getText());
            } catch (NumberFormatException ex) {
                throw new DSAValidationException("Discount muss eine Zahl sein!");
            }

            if (result < 0 || result > 100) {
                throw new DSAValidationException("Discount muss darf nur ein Wert von 0 bis 100 sein");
            }
        }
        return result;
    }

    @FXML
    public void onShowReducedPriceClicked() {
        log.debug("calling onShowReducedPriceClicked");

        int discount = retrieveDiscount();
        int baseRatePrice = currencyService.exchangeToBaseRate(retrieveCurrencyAmounts());
        int reducedPrice = (baseRatePrice * (100 - discount)) / 100;
        String reducePriceString = CurrencyFormatUtil.currencySetString(currencySetService.toCurrencySet(selectedCurrencySet(), reducedPrice), "\n");

        Dialogs.create()
                .title("neuer Preis nach Rabatt")
                .masthead(null)
                .message(reducePriceString)
                .showWarning();
    }


    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }

    public void setSaveCancelService(SaveCancelServiceImpl saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setUnitService(UnitService unitService) {
        this.unitService = unitService;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }
}
