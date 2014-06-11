package sepm.dsa.gui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.model.*;
import sepm.dsa.service.*;
import sepm.dsa.util.CurrencyFormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CalculatePriceController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(CalculatePriceController.class);
    private SpringFxmlLoader loader;
    private SaveCancelService saveCancelService;
    private TraderService traderService;
    private LocationService locationService;
    private ProductService productService;
    private CurrencySetService currencySetService;
    private List<Product> allProducts;
    private Trader calculationTrader = new Trader();

    private CurrencySet defaultCurrencySet;

    @FXML
    private TextField textName;
    @FXML
    private Label labelPrice;
    @FXML
    private ChoiceBox<ProductQuality> choiceQuality;
    @FXML
    private ChoiceBox<Location> choiceLocation;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn columnProduct;
    @FXML
    private Button cancelButton;
    @FXML
    private Button calcButton;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize CalculatePriceController");
    }

    @Override
    public void reload() {
        log.debug("reload CalculatePriceController");
        defaultCurrencySet = currencySetService.getDefaultCurrencySet();

        columnProduct.setCellValueFactory(new PropertyValueFactory<>("name"));
        allProducts = productService.getAll();
        showProducts(allProducts);

        choiceLocation.setItems(FXCollections.observableArrayList(locationService.getAll()));
        choiceLocation.getSelectionModel().select(0);

        //init choiceBoxes
        ProductQuality[] productQualities = ProductQuality.values();
        choiceQuality.setItems(FXCollections.observableArrayList(productQualities));
        choiceQuality.getSelectionModel().select(ProductQuality.NORMAL);
        List<CurrencyAmount> currencyAmounts = currencySetService.toCurrencySet(defaultCurrencySet, 0);
        labelPrice.setText(CurrencyFormatUtil.currencySetShortString(currencyAmounts, ", "));
        checkFocus();
    }

    private void showProducts(List<Product> products){
        ObservableList<Product> data = FXCollections.observableArrayList(products);
        productTable.setItems(data);
    }

    @FXML
    private void onFilterPressed() {
        log.debug("called onFilterPressed");
        List<Product> filteredProducts;
        String filter = textName.getText().toLowerCase();

        filteredProducts = new ArrayList<Product>();
        for (int i = 0; i<allProducts.size();i++){
            if (allProducts.get(i).getName().toLowerCase().contains(filter)){
                filteredProducts.add(allProducts.get(i));
            }
        }

        showProducts(filteredProducts);
        checkFocus();
    }

    @FXML
    private void onCalculatePressed() {
        log.debug("called onCalculatePressed");
        calculationTrader.setLocation(choiceLocation.getSelectionModel().getSelectedItem());

        int price = 0;
        if (choiceQuality.isDisabled()){
            price = traderService.calculatePriceForProduct(productTable.getSelectionModel().getSelectedItem(),calculationTrader);
        }else{
            price = traderService.calculatePricePerUnit(choiceQuality.getSelectionModel().getSelectedItem(),productTable.getSelectionModel().getSelectedItem(),calculationTrader);
        }

//        labelPrice.setText(price+"");
        List<CurrencyAmount> currencyAmounts = currencySetService.toCurrencySet(defaultCurrencySet, price);
        labelPrice.setText(CurrencyFormatUtil.currencySetShortString(currencyAmounts, ", "));

    }

    @FXML
    private void checkFocus(){
        log.debug("called checkFocus");
        Product p = productTable.getSelectionModel().getSelectedItem();

        calcButton.setDisable(true);
        choiceQuality.setDisable(true);

        if (p!=null){
            if (choiceLocation.getSelectionModel().getSelectedItem()!=null) {
                calcButton.setDisable(false);
            }

            if (p.getQuality()){
                choiceQuality.setDisable(false);
            }
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed");
        Stage stage = (Stage) textName.getScene().getWindow();
        stage.close();
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }
    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }
}
