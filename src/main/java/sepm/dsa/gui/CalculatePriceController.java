package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.model.*;
import sepm.dsa.service.*;
import sepm.dsa.util.CurrencyFormatUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
    private Trader calculationTrader;

    private CurrencySet defaultCurrencySet;

    @FXML
    private TextField textName;
    @FXML
    private Label labelPrice;
    @FXML
    private ChoiceBox<ProductQuality> choiceQuality;
    @FXML
    private ComboBox<Location> choiceLocation;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn columnProduct;
    @FXML
    private Button cancelButton;
    @FXML
    private Button calcButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        calculationTrader = new Trader();
        calculationTrader.setName("CalculatePriceDummyTrader");
    }

    @Override
    public void reload() {
        log.debug("reload CalculatePriceController");
        defaultCurrencySet = currencySetService.getDefaultCurrencySet();

        columnProduct.setCellValueFactory(new PropertyValueFactory<>("name"));
        allProducts = productService.getAll();
        showProducts(allProducts);

	choiceLocation.getItems().setAll(locationService.getAll());
        choiceLocation.getSelectionModel().select(0);

        //init choiceBoxes
        ProductQuality[] productQualities = ProductQuality.values();
	choiceQuality.getItems().setAll(productQualities);
        choiceQuality.getSelectionModel().select(ProductQuality.NORMAL);
        List<CurrencyAmount> currencyAmounts = currencySetService.toCurrencySet(defaultCurrencySet, 0);
        labelPrice.setText(CurrencyFormatUtil.currencySetShortString(currencyAmounts, ", "));
        checkFocus();
    }

    private void showProducts(List<Product> products){
		productTable.getItems().setAll(products);
    }

    @FXML
    private void onFilterPressed() {
        log.debug("called onFilterPressed");
//        List<Product> filteredProducts;
        String filter = textName.getText();//.toLowerCase();
        Set<Product> filteredProducts = productService.getBySearchTerm(filter);
//        filteredProducts = new ArrayList<Product>();
//        for (int i = 0; i<allProducts.size();i++){
//            if (allProducts.get(i).getName().toLowerCase().contains(filter)){
//                filteredProducts.add(allProducts.get(i));
//            }
//        }

        showProducts(new ArrayList<>(filteredProducts));
        checkFocus();
    }

    @FXML
    private void onCalculatePressed() {
        log.debug("called onCalculatePressed");
        calculationTrader.setLocation(choiceLocation.getSelectionModel().getSelectedItem());

        int price = 0;
        if (choiceQuality.isDisabled()){
            price = traderService.calculatePriceForProduct(productTable.getSelectionModel().getSelectedItem(),calculationTrader, true);
        }else{
            price = traderService.calculatePricePerUnit(choiceQuality.getSelectionModel().getSelectedItem(),productTable.getSelectionModel().getSelectedItem(),calculationTrader, true);
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
