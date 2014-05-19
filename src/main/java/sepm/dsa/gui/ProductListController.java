package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Product;
import sepm.dsa.service.ProductService;

@Service("RegionListController")
public class ProductListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProductListController.class);

    private ProductService productService;

    @FXML
    private TableView<Product> tableview_product;
    @FXML
    private TableColumn tablecolumn_product;
    @FXML
    private TableColumn tablecolumn_cost;
    @FXML
    private TableColumn tablecolumn_unit;
    @FXML
    private TableColumn tablecolumn_productions;
    @FXML
    private TableColumn tablecolumn_categories;
    @FXML
    private TableColumn tablecolumn_attribute;
    @FXML
    private TableColumn tablecolumn_comment;
    @FXML
    private Button button_create;
    @FXML
    private Button button_edit;
    @FXML
    private Button button_remove;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise ProductListController");
        // init table

        tablecolumn_product.setCellValueFactory(new PropertyValueFactory<>("name"));
        tablecolumn_cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        tablecolumn_unit.setCellValueFactory(new PropertyValueFactory<>("unitId"));
        tablecolumn_attribute.setCellValueFactory(new PropertyValueFactory<>("attributeId"));
        tablecolumn_productions.setCellValueFactory(new PropertyValueFactory<>("productionRegions"));
        tablecolumn_categories.setCellValueFactory(new PropertyValueFactory<>("categories"));
        tablecolumn_comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

        ObservableList<Product> data = FXCollections.observableArrayList(productService.getAll());
        tableview_product.setItems(data);

    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateButtonPressed - open Product-List Window");
        Stage stage = (Stage) button_create.getScene().getWindow();
        Parent scene = null;
        EditProductController.setProduct(null);

        SpringFxmlLoader loader = new SpringFxmlLoader();
        scene = (Parent) loader.load("/gui/editproduct.fxml");

        stage.setTitle("Waren-Details");
        stage.setScene(new Scene(scene, 600, 438));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onEditButtonPressed - open Product-List Window");
        Stage stage = (Stage) button_create.getScene().getWindow();
        Parent scene = null;
        EditProductController.setProduct(tableview_product.getSelectionModel().getSelectedItem());
        //EditProductController.setProduct(tableview_product.getFocusModel().getFocusedItem());

        SpringFxmlLoader loader = new SpringFxmlLoader();
        scene = (Parent) loader.load("/gui/editproduct.fxml");

        stage.setTitle("Waren-Details");
        stage.setScene(new Scene(scene, 600, 438));
        stage.setResizable(false);
        stage.show();

    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        Product selectedProduct = (tableview_product.getSelectionModel().getSelectedItem());
        //Product selectedProduct = tableview_product.getFocusModel().getFocusedItem();

        if (selectedProduct != null) {
            log.debug("open Confirm-Delete-Product Dialog");
            org.controlsfx.control.action.Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie das Product '" + selectedProduct.getName() + "' wirklich löschen?")
                    .showConfirm();
            if(response == Dialog.Actions.YES) {
                productService.remove(selectedProduct);
                tableview_product.getItems().remove(selectedProduct);
            }
        }

        //checkFocus();
    }
/*
    @FXML
    private void checkFocus() {
        Region selectedRegion = regionTable.getFocusModel().getFocusedItem();
        if (selectedRegion == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        }
        else{
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }

    }

    */
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
