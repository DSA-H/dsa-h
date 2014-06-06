package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TradeBuyFromPlayerController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TradeBuyFromPlayerController.class);

    private PlayerService playerService;
    private TraderService traderService;
    private UnitService unitService;
    private CurrencyService currencyService;
    private ProductService productService;
    private SaveCancelService saveCancelService;

    private static Trader trader;

    @FXML
    private ChoiceBox<Unit> selectedUnit;
    @FXML
    private ChoiceBox<Player> selectedPlayer;
    @FXML
    private ChoiceBox selectedQuality;
    @FXML
    private TextField selectedAmount;
    @FXML
    private ChoiceBox<Currency> selectedCurrency;
    @FXML
    private TextField selectedPrice;
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, String> pricecolumn;
    @FXML
    private TableColumn<Product, String> productColumn;
    @FXML
    private TextField searchField;
    @FXML
    private CheckBox qualityChoice;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("initialize TradeBuyFromPlayerController");
        //initialize table
        initialzeTableWithColums();

        selectedCurrency.setItems(FXCollections.observableArrayList(currencyService.getAll()));
        selectedUnit.setItems(FXCollections.observableArrayList(unitService.getAll()));
        selectedPlayer.setItems(FXCollections.observableArrayList(playerService.getAll()));

        List<String> qualityList = new ArrayList<>();
        for (ProductQuality q : ProductQuality.values()) {
            qualityList.add(q.getName());
        }
        selectedQuality.setItems(FXCollections.observableArrayList(qualityList));
    }

    @FXML
    private void checkFocus() {
        Product selProduct = productsTable.getSelectionModel().getSelectedItem();
        ProductQuality quality = ProductQuality.parse(selectedQuality.getSelectionModel().getSelectedIndex());
        if (selProduct != null) {
            if (quality != null) {
                int setQuality = traderService.calculatePricePerUnit(quality, productsTable.getSelectionModel().getSelectedItem(), trader);
                selectedPrice.setText(Integer.toString(setQuality));
            } else {
                int priceDefault = traderService.calculatePricePerUnit(ProductQuality.NORMAL, productsTable.getSelectionModel().getSelectedItem(), trader);
                selectedPrice.setText(Integer.toString(priceDefault));
            }
            selectedUnit.getSelectionModel().select(selProduct.getUnit());
        }
    }

    @FXML
    private void onEnableQualityClicked() {
        log.debug("calling onEnableQualityClicked");
        if (qualityChoice.isSelected()) {
            selectedQuality.setDisable(false);
        } else {
            selectedQuality.setDisable(true);
        }
    }

    @FXML
    private void onSearchPressed() {
        log.debug("calling onSearchPressed");
        if (searchField.getText().isEmpty()) {
            productsTable.setItems(FXCollections.observableArrayList(productService.getAll()));
        } else {
            productsTable.setItems(FXCollections.observableArrayList(productService.getBySearchTerm(searchField.getText())));
        }
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
        int amount;
        Player playerToCreateDealFor;

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
        if (productsTable.getSelectionModel().getSelectedItem() == null) {
            throw new DSAValidationException("Ware muss gewählt werden");
        }

        //Calculate Price
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

        //######## Quality Stuff ########
        Product selProduct = productsTable.getSelectionModel().getSelectedItem();
        ProductQuality quality = ProductQuality.parse(selectedQuality.getSelectionModel().getSelectedIndex());
        if (selProduct != null) {
            if (quality != null) { //so quality was SET
                int setQuality = traderService.calculatePricePerUnit(quality, productsTable.getSelectionModel().getSelectedItem(), trader);
                selectedPrice.setText(Integer.toString(setQuality));
            } else {
                //no quality enabled -- NORMAL price
                int priceDefault = traderService.calculatePricePerUnit(ProductQuality.NORMAL, productsTable.getSelectionModel().getSelectedItem(), trader);
                selectedPrice.setText(Integer.toString(priceDefault));
            }
            selectedUnit.getSelectionModel().select(selProduct.getUnit());
        } else {
            throw new DSAValidationException("Bitte Produkt auswählen!!");
        }

        traderService.buyFromPlayer(trader, playerToCreateDealFor, productsTable.getSelectionModel().getSelectedItem(), quality, selectedUnit.getSelectionModel().getSelectedItem(), amount, price, selectedCurrency.getSelectionModel().getSelectedItem());
        saveCancelService.save();

        Stage stage = (Stage) selectedUnit.getScene().getWindow();
        stage.close();
    }

    private void initialzeTableWithColums() {

        pricecolumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productColumn.setCellValueFactory(d -> {
            if (d.getValue() != null) {
                Product product = d.getValue();
                StringBuilder sb = new StringBuilder();
                sb.append(product.getName());
                if (product.getQuality() != null) {
                    sb.append(" (").append(d.getValue().getQuality()).append(")");
                }
                return new SimpleStringProperty(sb.toString());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }

    public static void setTrader(Trader trader) {
        TradeBuyFromPlayerController.trader = trader;
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

    public void setProductService(ProductService productService) {
    }
}
