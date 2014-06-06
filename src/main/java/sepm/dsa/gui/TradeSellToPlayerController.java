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
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.math.BigDecimal;
import java.net.URL;
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
    private ChoiceBox<Currency> selectedCurrency;
    @FXML
    private TextField selectedPrice;

    private static Trader trader;
    private static Offer offer;
    private TraderService traderService;
    private SaveCancelService saveCancelService;
    private UnitService unitService;
    private CurrencyService currencyService;
    private PlayerService playerService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectedCurrency.setItems(FXCollections.observableArrayList(currencyService.getAll()));
        selectedUnit.setItems(FXCollections.observableArrayList(unitService.getAll()));
        selectedPrice.setText(offer.getPricePerUnit().toString());
        selectedPlayer.setItems(FXCollections.observableArrayList(playerService.getAll()));

        //select default unit & currency
        Currency preferredCurrency = trader.getLocation().getRegion().getPreferredCurrency();
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
        BigDecimal price;
        if (selectedPrice.getText().isEmpty()) {
            throw new DSAValidationException("Bitte Preis eingeben");
        }
        try {
            //TODO Kommastellen via locale einlesen
//            Locale l = Locale.getDefault();
//            log.info("Locale " + l);
//
//             NumberFormat nf = NumberFormat.getNumberInstance(l);
//             DecimalFormat df = (DecimalFormat)nf;

            price = new BigDecimal(selectedPrice.getText());

        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Preis muss eine Zahl sein!");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DSAValidationException("Preis muss > 0 sein");
        }

        traderService.sellToPlayer(trader, playerToCreateDealFor, offer.getProduct(), selectedUnit.getSelectionModel().getSelectedItem(), amount, price);
        saveCancelService.save();

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
}
