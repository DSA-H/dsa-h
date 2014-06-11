package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TradeBuyFromPlayerController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(TradeBuyFromPlayerController.class);

    private PlayerService playerService;
    private TraderService traderService;
    private UnitService unitService;
    private CurrencyService currencyService;
    private CurrencySetService currencySetService;
    private ProductService productService;
    private SaveCancelService saveCancelService;

    private Trader trader;

    private List<Currency> currencies;

    @FXML
    private ChoiceBox<Unit> selectedUnit;
    @FXML
    private ChoiceBox<Player> selectedPlayer;
    @FXML
    private ChoiceBox<ProductQuality> selectedQuality;
    @FXML
    private TextField selectedAmount;
    @FXML
    private ChoiceBox<CurrencySet> selectedCurrency;
//    @FXML
//    private TextField selectedPrice;
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, String> productColumn;
    @FXML
    private TextField searchField;

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
    public void initialize(URL location, ResourceBundle resources) {
        log.debug("initialize TradeBuyFromPlayerController");
    }

    @Override
    public void reload() {
        log.debug("reload TradeBuyFromPlayerController");
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

        selectedAmount.setText("1");

        //initialize table
        initialzeTableWithColums();

        selectedCurrency.setItems(FXCollections.observableArrayList(currencySetService.getAll()));
        selectedUnit.setItems(FXCollections.observableArrayList(unitService.getAll()));
        selectedPlayer.setItems(FXCollections.observableArrayList(playerService.getAll()));

        List<ProductQuality> qualityList = new ArrayList<>();
        for (ProductQuality q : ProductQuality.values()) {
            qualityList.add(q);
        }
        selectedQuality.setItems(FXCollections.observableArrayList(qualityList));
        selectedQuality.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice();
        });

        selectedAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice();
        });

        selectedCurrency.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice();
        });

        refreshPriceView();
    }

    private ProductQuality selectedQuality() {
        return selectedQuality.getSelectionModel().getSelectedItem();
    }

    private Product selectedProduct() {
        return productsTable.getSelectionModel().getSelectedItem();
    }

    private void updatePrice() {
        log.info("calling updatePrice()");
        refreshPriceView();
        if (productsTable.getSelectionModel().getSelectedItem() != null) {
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
            int baseRatePrice = setQuality * amount;    // TODO is this really the baseRate price?
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
    private void checkFocus() {
        log.info("calling checkFocus (uncommented code)");
        selectedAmount.setText("1");
        Product selProduct = productsTable.getSelectionModel().getSelectedItem();
        ProductQuality quality;
        if (selProduct != null) {
            if (selProduct.getQuality()) {
                selectedQuality.setDisable(false);
                quality = ProductQuality.parse(selectedQuality.getSelectionModel().getSelectedIndex());
                int setQuality = 0;
                if (quality != null) {
//                    setQuality = traderService.calculatePricePerUnit(quality, productsTable.getSelectionModel().getSelectedItem(), trader);
                } else {
                    selectedQuality.getSelectionModel().select(2);
//                    setQuality = traderService.calculatePricePerUnit(ProductQuality.NORMAL, productsTable.getSelectionModel().getSelectedItem(), trader);
                }
//                selectedPrice.setText(Integer.toString(setQuality));
//                updatePrice(); TODO just commented it out, now broken?
            } else {
                selectedQuality.getSelectionModel().select(ProductQuality.NORMAL);
//                int priceDefault = traderService.calculatePricePerUnit(ProductQuality.NORMAL, productsTable.getSelectionModel().getSelectedItem(), trader);
//                selectedPrice.setText(Integer.toString(priceDefault));
                selectedQuality.setDisable(true);
            }
            updatePrice();
            selectedUnit.setItems(FXCollections.observableArrayList(unitService.getAllByType(selProduct.getUnit().getUnitType())));
            selectedUnit.getSelectionModel().select(selProduct.getUnit());
        }
    }

    private CurrencySet selectedCurrencySet() {
        return selectedCurrency.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void onSearchPressed() {
        log.debug("calling onSearchPressed");
        if (searchField.getText().isEmpty()) {
            productsTable.setItems(FXCollections.observableArrayList(productService.getAll()));
            productsTable.getSelectionModel().selectFirst();
            checkFocus();
        } else {
            productsTable.setItems(FXCollections.observableArrayList(productService.getBySearchTerm(searchField.getText())));
            productsTable.getSelectionModel().selectFirst();
            checkFocus();
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");

        saveCancelService.refresh(trader);

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


        if (selectedCurrency.getSelectionModel().getSelectedItem() == null) {
            throw new DSAValidationException("Die Währung wurde nicht gewählt!");
        }

        //Calculate Price
//        BigDecimal price;
//        if (selectedPrice.getText().isEmpty()) {
//            throw new DSAValidationException("Bitte Preis eingeben");
//        }
//        try {
//            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
//            df.setParseBigDecimal(true);
//            price = (BigDecimal) df.parse(selectedPrice.getText());
//
//        } catch (NumberFormatException ex) {
//            throw new DSAValidationException("Preis muss eine Zahl sein!");
//        } catch (ParseException e) {
//            throw new DSAValidationException("Kann Zahl nicht parsen! Probiers mit , statt .");
//        }
//        if (price.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new DSAValidationException("Preis muss > 0 sein");
//        }

//        CurrencySet currencySet = selectedCurrency.getSelectionModel().getSelectedItem();
//        List<Currency> currencies = currencyService.getAllByCurrencySet(currencySet);
        List<CurrencyAmount> currencyAmounts = new ArrayList<>(5);
        try {
            for (int i = 0; i < currencies.size(); i++) {
                CurrencyAmount a = new CurrencyAmount();
                a.setCurrency(currencies.get(i));
                Integer currencyAmount = Integer.parseInt(tf_CurrencyAmounts[i].getText());
                if (currencyAmount < 0) {
                    throw new DSAValidationException("Preis muss > 0 sein");
                }
                a.setAmount(currencyAmount);
                currencyAmounts.add(a);
            }
        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Der Preis muss aus lauter Zahlen bestehen!");
        }


        if (selectedPlayer.getSelectionModel().getSelectedItem() != null) {
            playerToCreateDealFor = selectedPlayer.getSelectionModel().getSelectedItem();
        } else {
            throw new DSAValidationException("Ein Spieler muss gewählt werden!");
        }
        if (selectedUnit.getSelectionModel().getSelectedItem() == null) {
            throw new DSAValidationException("Die Einheit wurde nicht gewählt!");
        }
        //######## Quality Stuff ########

        ProductQuality quality = selectedQuality.getSelectionModel().getSelectedItem();

        Unit unit = selectedUnit.getSelectionModel().getSelectedItem();

        Product product = productsTable.getSelectionModel().getSelectedItem();
        if (product != null) {
            if (quality != null) { //so quality was SET
//                int setQuality = traderService.calculatePricePerUnit(quality, productsTable.getSelectionModel().getSelectedItem(), trader);
//                selectedPrice.setText(Integer.toString(setQuality));
            } else {
                //no quality enabled -- NORMAL price
//                int priceDefault = traderService.calculatePricePerUnit(ProductQuality.NORMAL, productsTable.getSelectionModel().getSelectedItem(), trader);
//                selectedPrice.setText(Integer.toString(priceDefault));
            }
            selectedUnit.getSelectionModel().select(product.getUnit());
            unit = selectedUnit.getSelectionModel().getSelectedItem();
        } else {
            throw new DSAValidationException("Bitte Produkt auswählen!!");
        }

        traderService.buyFromPlayer(trader, playerToCreateDealFor, product, quality, unit, amount, currencyAmounts);
        saveCancelService.save();

        saveCancelService.refresh(trader);
        saveCancelService.refresh(trader.getDeals());

        Stage stage = (Stage) selectedUnit.getScene().getWindow();
        stage.close();
    }

    private void initialzeTableWithColums() {

        productColumn.setCellValueFactory(d -> {
            if (d.getValue() != null) {
                Product product = d.getValue();
                return new SimpleStringProperty(product.getName());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
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
        this.productService = productService;
    }

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }
}
