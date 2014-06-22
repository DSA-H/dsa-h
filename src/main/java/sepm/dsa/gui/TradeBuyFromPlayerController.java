package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.model.CurrencyAmount;
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
        super.initialize(location, resources);

        //initialize table
        initialzeTableWithColums();

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

        selectedQuality.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice(selectedUnit.getValue(), selectedCurrencySet(), newValue);
        });

        selectedAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice(selectedUnit.getValue(), selectedCurrencySet(), selectedQuality());
        });

        selectedCurrency.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice(selectedUnit.getValue(), newValue, selectedQuality());
        });
        selectedUnit.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updatePrice(newValue, selectedCurrencySet(), selectedQuality());
        });

        selectedAmount.setText("1");
        selectedQuality.getItems().setAll(ProductQuality.values());
        selectedQuality.getSelectionModel().select(ProductQuality.NORMAL);
    }

    @Override
    public void reload() {
        log.debug("reload TradeBuyFromPlayerController");

        CurrencySet currencySet = selectedCurrency.getValue();
        selectedCurrency.getItems().setAll(currencySetService.getAll());
        selectedCurrency.setValue(currencySet);
        Unit unit = selectedUnit.getValue();
        if(selectedProduct() != null) {
            selectedUnit.getItems().setAll(unitService.getAllByType(selectedProduct().getUnit().getUnitType()));
            selectedUnit.setValue(unit);
        }else {
            selectedUnit.getItems().clear();
            selectedUnit.setValue(null);
        }
        Player player = selectedPlayer.getValue();
        selectedPlayer.getItems().setAll(playerService.getAll());
        selectedPlayer.setValue(player);

        refreshPriceView();
    }

    private ProductQuality selectedQuality() {
        return selectedQuality.getSelectionModel().getSelectedItem();
    }

    private Product selectedProduct() {
        return productsTable.getSelectionModel().getSelectedItem();
    }

    private void updatePrice(Unit selectedUnit, CurrencySet selectedCurrencySet, ProductQuality productQuality) {
        log.info("calling updatePrice()");
        if(selectedUnit == null || selectedCurrencySet == null || productQuality == null) {
            return;
        }
        refreshPriceView();
        if (productsTable.getSelectionModel().getSelectedItem() != null) {
            log.info(productQuality + ", " + selectedProduct() + " " + trader);
            int pricePerUnit = traderService.calculatePricePerUnit(productQuality, selectedProduct(), trader);
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
    private void checkFocus() {
        log.info("calling checkFocus (uncommented code)");
        selectedAmount.setText("1");
        Product selProduct = productsTable.getSelectionModel().getSelectedItem();
        ProductQuality quality;
        if (selProduct != null) {
            if (selProduct.getQuality()) {
                selectedQuality.setDisable(false);
                quality = ProductQuality.parse(selectedQuality.getSelectionModel().getSelectedIndex());
                if (quality == null) {
                    selectedQuality.getSelectionModel().select(ProductQuality.NORMAL);
                }
            } else {
                selectedQuality.getSelectionModel().select(ProductQuality.NORMAL);
                selectedQuality.setDisable(true);
            }
	        selectedUnit.getItems().setAll(unitService.getAllByType(selProduct.getUnit().getUnitType()));
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
	    productsTable.getItems().setAll(productService.getAll());
            productsTable.getSelectionModel().selectFirst();
            checkFocus();
        } else {
	    productsTable.getItems().setAll(productService.getBySearchTerm(searchField.getText()));
            productsTable.getSelectionModel().selectFirst();
            checkFocus();
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");

        if(trader != null && (trader = traderService.get(trader.getId())) != null) {
            saveCancelService.refresh(trader);
        }

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
            throw new DSAValidationException("Der Preis muss aus lauter ganzen Zahlen bestehen!");
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

        Product product = productsTable.getSelectionModel().getSelectedItem();
        Unit unit = selectedUnit.getSelectionModel().getSelectedItem();

        traderService.buyFromPlayer(trader, playerToCreateDealFor, product, quality, unit, amount, currencyAmounts);
        saveCancelService.save();

        if(trader != null && (trader = traderService.get(trader.getId())) != null) {
            saveCancelService.refresh(trader);
        }
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
        if (trader != null) {
            CurrencySet defaultCurrencySet = trader.getLocation().getRegion().getPreferredCurrencySet();
            if (defaultCurrencySet == null) {
                defaultCurrencySet = currencySetService.getDefaultCurrencySet();
            }
            selectedCurrency.getSelectionModel().select(defaultCurrencySet);
        }
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
