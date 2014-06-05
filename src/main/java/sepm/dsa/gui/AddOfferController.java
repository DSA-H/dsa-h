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

public class AddOfferController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(AddOfferController.class);
    private SpringFxmlLoader loader;
    private SaveCancelService saveCancelService;
    private TraderService traderService;
    private ProductService productService;
    private List<Product> allProducts;
    private Trader selectedTrader;


    @FXML
    private TextField textName;
    @FXML
    private TextField textAmount;
    @FXML
    private TextField textPrice;
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
    private void onSavePressed() {
        log.debug("called onSavePressed");
        Offer o = new Offer();
        o.setProduct(productTable.getSelectionModel().getSelectedItem());
        o.setAmount(Integer.parseInt(textAmount.getText()));
        o.setPricePerUnit(Integer.parseInt(textPrice.getText()));
        o.setTrader(selectedTrader);
        if (!choiceQuality.isDisabled()){
            o.setQuality(ProductQuality.NORMAL); //TODO: to be implemented
        }else{
            o.setQuality(ProductQuality.NORMAL);
        }

        Set<Offer> offers = selectedTrader.getOffers();
        offers.add(o);
        selectedTrader.setOffers(offers);
        traderService.update(selectedTrader);
        Stage stage = (Stage) textName.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void checkFocus(){
        log.debug("called checkFocus");
        Product p = productTable.getSelectionModel().getSelectedItem();

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
    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }
    public void setTrader(Trader t) {
        this.selectedTrader = t;
    }
}
