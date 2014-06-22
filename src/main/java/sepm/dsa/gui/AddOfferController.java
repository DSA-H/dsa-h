package sepm.dsa.gui;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AddOfferController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(AddOfferController.class);
    private SpringFxmlLoader loader;
    private SaveCancelService saveCancelService;
    private TraderService traderService;
    private ProductService productService;
    private List<Product> allProducts;
    private Trader selectedTrader;

    @FXML
    private Label lbl_Unit;
    @FXML
    private TextField textName;
    @FXML
    private TextField textAmount;
    //@FXML
    //private TextField textPrice;
    @FXML
    private ChoiceBox<ProductQuality> choiceQuality;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn columnProduct;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize EditTraderController");

        columnProduct.setCellValueFactory(new PropertyValueFactory<>("name"));
        allProducts = productService.getAll();
        showProducts(allProducts);

        //init choiceBoxes
        ProductQuality[] productQualities = ProductQuality.values();
        choiceQuality.setItems(FXCollections.observableArrayList(productQualities));
        choiceQuality.getSelectionModel().select(ProductQuality.NORMAL);
        checkFocus();
    }

    private void showProducts(List<Product> products){
        ObservableList<Product> data = FXCollections.observableArrayList(products);
        productTable.setItems(data);
    }

    private void setUp() {
        log.debug("calling setUp");


    }

    @FXML
    private void onFilterPressed() {
        log.debug("called onFilterPressed");
        List<Product> filteredProducts;
        String filter = textName.getText();

        filteredProducts = new ArrayList<>(productService.getBySearchTerm(filter));

        showProducts(filteredProducts);
        checkFocus();
    }

    @FXML
    private void onSavePressed() {
        log.debug("called onSavePressed");
        Offer o = new Offer();
        Product p = productTable.getSelectionModel().getSelectedItem();
        o.setProduct(p);

        Number amount = 0d;
        try {
            DecimalFormat df = new DecimalFormat();
            amount = df.parse(textAmount.getText());
        } catch (ParseException e) {
            throw new DSAValidationException("Anzahl muss eine ganze positive Zahl sein!");
        }
        if(amount.doubleValue() <= 0) {
            throw new DSAValidationException("Anzahl muss eine ganze positive Zahl sein!");
        }

        o.setAmount(amount.doubleValue());

        o.setTrader(selectedTrader);
        if (!choiceQuality.isDisabled()){
            o.setQuality(choiceQuality.getSelectionModel().getSelectedItem());
            o.setPricePerUnit(traderService.calculatePricePerUnit(o.getQuality(), p, selectedTrader));
        }else{
            o.setQuality(ProductQuality.NORMAL);
            o.setPricePerUnit(traderService.calculatePriceForProduct(p,selectedTrader));
        }

        traderService.addManualOffer(selectedTrader, o);
        saveCancelService.save();

        Stage stage = (Stage) textName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void checkFocus(){
        log.debug("called checkFocus");
        Product p = productTable.getSelectionModel().getSelectedItem();

        if (p != null) {
            lbl_Unit.setText(p.getUnit().getName());
        }

        saveButton.setDisable(true);
        choiceQuality.setDisable(true);

        if (p!=null){
            saveButton.setDisable(false);
            if (p.getQuality()){
                choiceQuality.setDisable(false);
            }
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed - return to TraderDetailsController");
        saveCancelService.cancel();
        Stage stage = (Stage) textName.getScene().getWindow();
        stage.close();
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }
    public void setTrader(Trader t) {
        this.selectedTrader = t;
    }

    @Override
    public void reload() {
        log.debug("reload TraderDetailsController");
        checkFocus();
    }
}
