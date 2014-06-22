package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.model.*;
import sepm.dsa.service.CurrencySetService;
import sepm.dsa.service.ProductService;
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.util.CurrencyFormatUtil;

import java.util.List;
import java.util.Set;

public class ProductListController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(ProductListController.class);
    private SpringFxmlLoader loader;

    private ProductService productService;
    private SaveCancelService saveCancelService;
    private CurrencySetService currencySetService;

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn productColumn;
    @FXML
    private TableColumn costColumn;
    @FXML
    private TableColumn categorieColumn;
    @FXML
    private TableColumn productionRegionColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private TextField tf_ProductOrCategoryName;
    @FXML
    private TextField tf_RegionName;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
	    super.initialize(location, resources);

	    log.debug("initialize ProductListController");
        // init table
        productColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        costColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Product, String> r) {
                if (r.getValue() != null) {
                    Product product = r.getValue();
                    CurrencySet currencySet = currencySetService.getDefaultCurrencySet();
                    List<CurrencyAmount> ca = currencySetService.toCurrencySet(currencySet, product.getCost());
                    String str = CurrencyFormatUtil.currencySetShortString(ca);
                    return new SimpleStringProperty(str);
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        categorieColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Product, String> r) {
                if (r.getValue() == null) {
                    return new SimpleStringProperty("");
                }
                Product product = r.getValue();
                StringBuilder sb = new StringBuilder();
                Set<ProductCategory> categories = product.getCategories();
                for (ProductCategory categorie : categories) {
                    sb.append(categorie.getName()).append(", ");
                }
                if (sb.length() >= 2) {
                    sb.delete(sb.length() - 2, sb.length());
                }
                return new SimpleStringProperty(sb.toString());
            }
        });
        productionRegionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Product, String> r) {
                if (r.getValue() == null) {
                    return new SimpleStringProperty("");
                }
                Product product = r.getValue();
                StringBuilder sb = new StringBuilder();
                Set<Region> regions = product.getRegions();
                for (Region region : regions) {
                    sb.append(region.getName()).append(", ");
                }
                if (sb.length() >= 2) {
                    sb.delete(sb.length() - 2, sb.length());
                }
                return new SimpleStringProperty(sb.toString());
            }
        });
    }

    @Override
    public void reload() {
        log.debug("reload ProductListController");
	    productTable.getItems().setAll(productService.getAll());

        checkFocus();
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateClicked - open Waren Window");

        Stage stage = (Stage) productTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editproduct.fxml", stage);
        EditProductController ctrl = loader.getController();
        ctrl.setProduct(null);
        ctrl.setCalledFromCategorie(false);
        ctrl.reload();

        stage.setTitle("Waren");
        stage.setScene(new Scene(scene, 600, 530));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onWarenClicked - open Waren Window");

        Stage stage = (Stage) productTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editproduct.fxml", stage);
        EditProductController ctrl = loader.getController();
        ctrl.setProduct(productTable.getSelectionModel().getSelectedItem());
        ctrl.setCalledFromCategorie(false);
        ctrl.reload();

        stage.setTitle("Waren");
        stage.setScene(new Scene(scene, 600, 530));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        Product selectedProduct = (productTable.getSelectionModel().getSelectedItem());

        if (selectedProduct != null) {
            log.debug("open Confirm-Delete-Product Dialog");
            org.controlsfx.control.action.Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie die Ware '" + selectedProduct.getName() + "' wirklich endgültig löschen?")
                    .actions(Dialog.Actions.NO, Dialog.Actions.YES)
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                productService.remove(selectedProduct);
                saveCancelService.save();
                productTable.getItems().remove(selectedProduct);
            }
        }

        checkFocus();
    }

    @FXML
    private void onFilterProductsPressed() {
        String productOrCategoryName = tf_ProductOrCategoryName.getText();
        String regionName = tf_RegionName.getText();
        if (productOrCategoryName.length() == 0) productOrCategoryName = null;
        if (regionName.length() == 0) regionName = null;

        productTable.getItems().setAll(productService.getAllByFilter(productOrCategoryName, regionName));
    }

    @FXML
    private void checkFocus() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
        if (selectedProduct == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
    }

    @FXML
    public void closeClicked() {
        Stage stage = (Stage) productTable.getScene().getWindow();
        stage.close();
    }


    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }
}
