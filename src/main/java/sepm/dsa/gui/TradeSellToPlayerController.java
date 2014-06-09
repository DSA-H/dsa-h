package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TradeSellToPlayerController implements Initializable {

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
    private TextField selectedPrice;
    @FXML
    private TextField selectedDiscount;

    @FXML
    private TextField tf_currency1Amount;
    @FXML
    private TextField tf_currency2Amount;
    @FXML
    private TextField tf_currency3Amount;
    @FXML
    private TextField tf_currency4Amount;
    @FXML
    private TextField tf_currency5Amount;

    private TextField[] tf_currencyAmounts =
            new TextField[] {
                    tf_currency1Amount,
                    tf_currency2Amount,
                    tf_currency3Amount,
                    tf_currency4Amount,
                    tf_currency5Amount };


    private static Trader trader;
    private static Offer offer;
    private TraderService traderService;
    private SaveCancelService saveCancelService;
    private UnitService unitService;
    private CurrencyService currencyService;
    private CurrencySetService currencySetService;
    private PlayerService playerService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectedDiscount.setText("0");

        selectedCurrency.setItems(FXCollections.observableArrayList(currencySetService.getAll()));
        selectedUnit.setItems(FXCollections.observableArrayList(unitService.getAllByType(offer.getProduct().getUnit().getUnitType())));
        selectedPrice.setText(offer.getPricePerUnit().toString());
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
        selectedAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            int setQuality = traderService.calculatePricePerUnit(offer.getQuality(), offer.getProduct(), trader);
            int amount;

            //##### get amount
            if (selectedAmount.getText().isEmpty()) {
            } else {
                try {
                    amount = new Integer(selectedAmount.getText());

                } catch (NumberFormatException ex) {
                    throw new DSAValidationException("Menge muss eine ganze Zahl sein!");
                }

                selectedPrice.setText(Integer.toString(setQuality * amount));
            }
        });
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

        Integer discountAmount = null;
        try {
            discountAmount = (Integer.parseInt(selectedPrice.getText()) * discount) / 100;
        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Der Preis muss angegeben sein.");
        }
        selectedDiscount.setText("" + (discountAmount == null ? 0 : discountAmount));
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
        //PRICE STUFF ###########
//        BigDecimal price;
//        if (selectedPrice.getText().isEmpty()) {
//            throw new DSAValidationException("Bitte Preis eingeben");
//        }
//        try {
//            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
//            df.setParseBigDecimal(true);
//
//            try {
//                price = (BigDecimal) df.parse(selectedPrice.getText());
//            } catch (IllegalArgumentException e) {
//                throw new DSAValidationException("Preis kann so nicht als Zahl eingegeben werden");
//            } catch (ParseException e) {
//                throw new DSAValidationException("Ungültiger Preis. ");
//            }
//
//        } catch (NumberFormatException ex) {
//            throw new DSAValidationException("Preis muss eine Zahl sein!");
//        }
//        if (price.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new DSAValidationException("Preis muss > 0 sein");
//        }

        CurrencySet currencySet = selectedCurrency.getSelectionModel().getSelectedItem();
        List<Currency> currencies = currencyService.getAllByCurrencySet(currencySet);
        List<CurrencyAmount> currencyAmounts = new ArrayList<>(5);
        try {
            for (int i = 0; i < currencies.size(); i++) {
                CurrencyAmount a = new CurrencyAmount();
                a.setCurrency(currencies.get(i));
                Integer currencyAmount = Integer.parseInt(tf_currencyAmounts[i].getText());
                if (currencyAmount < 0) {
                    throw new DSAValidationException("Preis muss > 0 sein");
                }
                a.setAmount(currencyAmount);
                currencyAmounts.add(a);
            }
        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Der Preis muss aus lauter Zahlen bestehen!");
        }

        //######## Discount stuff --
        //PRICE STUFF ###########
        Integer discount = 0;
        if (!selectedDiscount.getText().isEmpty()) {
            try {
                discount = Integer.parseInt(selectedDiscount.getText());
            } catch (NumberFormatException ex) {
                throw new DSAValidationException("Discount muss eine Zahl sein!");
            }

            if (discount < 0 || discount > 100) {
                throw new DSAValidationException("Discount muss darf nur ein Wert von 0 bis 100 sein");
            }

        }

        traderService.sellToPlayer(trader, playerToCreateDealFor, offer.getProduct(), offer.getQuality(), selectedUnit.getSelectionModel().getSelectedItem(), amount, currencyAmounts, discount);
        saveCancelService.save();

        saveCancelService.refresh(trader);//, offer, offer.getProduct());
        saveCancelService.refresh(trader.getDeals());

        Stage stage = (Stage) selectedUnit.getScene().getWindow();
        stage.close();
    }

    public static void setTrader(Trader trader) {
        TradeSellToPlayerController.trader = trader;
    }

    public static void setOffer(Offer offer) {
        TradeSellToPlayerController.offer = offer;
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
