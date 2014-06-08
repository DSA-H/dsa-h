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
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CalculatePriceController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(CalculatePriceController.class);
    private SpringFxmlLoader loader;
    private SaveCancelService saveCancelService;
    private TraderService traderService;
    private ProductService productService;
    private List<Product> allProducts;
    private Trader selectedTrader;


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

        filteredProducts = new ArrayList<Product>();
        for (int i = 0; i<allProducts.size();i++){
            if (allProducts.get(i).getName().contains(filter)){
                filteredProducts.add(allProducts.get(i));
            }
        }

        showProducts(filteredProducts);
        checkFocus();
    }

    @FXML
    private void onCalculatePressed() {
        log.debug("called onCalculatePressed");



        Stage stage = (Stage) textName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void checkFocus(){
        log.debug("called checkFocus");
        Product p = productTable.getSelectionModel().getSelectedItem();

        calcButton.setDisable(true);
        choiceQuality.setDisable(true);

        if (p!=null){
            calcButton.setDisable(false);
            if (p.getQuality()){
                choiceQuality.setDisable(false);
            }
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed");
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
}
