package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;
import sepm.dsa.util.CurrencyFormatUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TradeSellToPlayerController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(TradeSellToPlayerController.class);

    @FXML
    private Label selectedOffer;
    @FXML
    private ComboBox<Unit> selectedUnit;
    @FXML
    private ComboBox<Player> selectedPlayer;
    @FXML
    private TextField selectedAmount;
    @FXML
    private ComboBox<CurrencySet> selectedCurrency;
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
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

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
        selectedAmount.setText("1");
    }

    @Override
    public void reload() {
        log.debug("reload TradeSellToPlayerController");

        CurrencySet currencySet = selectedCurrency.getValue();
        selectedCurrency.getItems().setAll(currencySetService.getAll());
        selectedCurrency.setValue(currencySet);
        Unit unit = selectedUnit.getValue();
        selectedUnit.getItems().setAll(unitService.getAllByType(offer.getProduct().getUnit().getUnitType()));
        selectedUnit.setValue(unit);
        Player player = selectedPlayer.getValue();
        selectedPlayer.getItems().setAll(playerService.getAll());
        selectedPlayer.setValue(player);

        StringBuilder sb = new StringBuilder();
        sb.append(offer.getProduct().getName());
        if (offer.getProduct().getQuality()) {
            sb.append(" (" + offer.getQuality().getName() + ")");
        }
        selectedOffer.setText(sb.toString());

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

    private void updatePrice(Unit selectedUnit, CurrencySet selectedCurrencySet) {
        log.info("calling updatePrice()");
        if(selectedUnit == null || selectedCurrencySet == null) {
            return;
        }
        refreshPriceView();
        if (offer.getProduct() != null) {
            log.info(selectedQuality() + ", " + selectedProduct() + " " + trader);
            int pricePerUnit = traderService.calculatePricePerUnit(selectedQuality(), selectedProduct(), trader, false);
            int amount = 0;
            //##### get amount
            if (!selectedAmount.getText().isEmpty()) {
                try {
                    amount = new Integer(selectedAmount.getText());
                } catch (NumberFormatException ex) {
                    throw new DSAValidationException("Menge muss eine ganze Zahl sein!");
                }
            }
            Unit unit = selectedUnit;
            Unit defaultUnit = selectedProduct().getUnit();

            double unitfactor = unit.getValueToBaseUnit() / defaultUnit.getValueToBaseUnit();

            int baseRatePrice = (int)Math.round(pricePerUnit * amount * unitfactor);

            List<CurrencyAmount> currencyAmounts = currencySetService.toCurrencySet(selectedCurrencySet, baseRatePrice);
            int i=0;
            for (CurrencyAmount c : currencyAmounts) {
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

        selectedDiscount.setText("" + discount);
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");

        Stage stage = (Stage) selectedOffer.getScene().getWindow();
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

        Product product = offer.getProduct();
        Unit unit = selectedUnit.getSelectionModel().getSelectedItem();
        Double offerAmountDifference = unit.exchange((double) amount, product.getUnit());
        boolean removeRemainingOfferAmount = false;
        if (offer.getAmount() - offerAmountDifference < 0) {
            // ask to buy all remaining
            Unit productUnit = offer.getProduct().getUnit();
            int newAmount = offer.getAmount().intValue();

            if (newAmount == 0) {
                Dialogs.create()
                        .title("Händler hat nicht genug von dieser Ware?")
                        .masthead(null)
                        .message("Der Händler hat nicht die gewollte Menge dieser Ware.\n" +
                                "Benutzen Sie ggf. eine feinere Einheit, um noch etwas vom Rest der Ware zu erhalten.")
                        .showInformation();
                return;
            }
            Action response = Dialogs.create()
                    .title("Händler hat nicht genug von dieser Ware?")
                    .masthead(null)
                    .message("Der Händler hat nicht die gewollte Menge dieser Ware. Soll der Rest von " +
                            newAmount + " " + productUnit.getName() + " trotzdem gekauft werden?\n\n" +
                            "Hinweis: Der Preis wird nicht neu berechnet, bitte geben Sie ggf. einen entsprechenden Preis für die " +
                            "reduzierte Menge ein.")
                    .actions(Dialog.Actions.NO, Dialog.Actions.YES)
                    .showConfirm();

            if (response == Dialog.Actions.YES) {
                amount = newAmount;
                unit = productUnit;
                removeRemainingOfferAmount = true;
            } else {
                return;
            }
        }

        traderService.sellToPlayer(trader, playerToCreateDealFor, product, offer.getQuality(), unit, amount, currencyAmounts, discount, removeRemainingOfferAmount);
        saveCancelService.save();

        if(trader != null && (trader = traderService.get(trader.getId())) != null) {
            saveCancelService.refresh(trader);
        }
        saveCancelService.refresh(trader.getDeals());

        Stage stage = (Stage) selectedOffer.getScene().getWindow();
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
                .showInformation();
    }


    public void setTrader(Trader trader) {
        this.trader = trader;
        if (trader != null) {
            CurrencySet defaultCurrencySet = trader.getLocation().getRegion().getPreferredCurrencySet();
            if (defaultCurrencySet == null) {
                defaultCurrencySet = currencySetService.getDefaultCurrencySet();
            }
            selectedCurrency.getSelectionModel().select(defaultCurrencySet);
        }
    }

    public void setOffer(Offer offer) {
        if(offer == null) {
            throw new IllegalArgumentException("Offer cannot be null");
        }
        this.offer = offer;
        selectedUnit.getSelectionModel().select(offer.getProduct().getUnit());

        selectedAmount.textProperty().addListener((observable, oldValue, newValue) -> updatePrice(selectedUnit.getValue(), selectedCurrencySet()));
        selectedCurrency.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePrice(selectedUnit.getValue(), newValue));
        selectedUnit.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePrice(newValue, selectedCurrencySet()));
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
