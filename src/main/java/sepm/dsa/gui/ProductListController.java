package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Product;
import sepm.dsa.model.Region;
import sepm.dsa.service.ProductService;

import java.util.Set;

public class ProductListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProductListController.class);
    SpringFxmlLoader loader;

    private ProductService productService;
    private SessionFactory sessionFactory;

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn productColumn;
    @FXML
    private TableColumn costColumn;
    @FXML
    private TableColumn attributeColumn;
    @FXML
    private TableColumn productionRegionColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize ProductListController");
        // init table
        productColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        attributeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Product, String> r) {
                if (r.getValue() == null) {
                    return new SimpleStringProperty("");
                }
                Product product = r.getValue();
                return new SimpleStringProperty(product.getAttribute().getName());
            }
        });
        productionRegionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Product, String> r) {
                Session session = sessionFactory.openSession();
                if (r.getValue() == null) {
                    return new SimpleStringProperty("");
                }
                Product product = r.getValue();
                session.refresh(product);
                StringBuilder sb = new StringBuilder();
                Set<Region> regions = product.getRegions();
                for (Region region : regions) {
                    sb.append(region.getName()).append(", ");
                }
                if (sb.length() >= 2) {
                    sb.delete(sb.length() - 2, sb.length());
                }
                session.close();
                return new SimpleStringProperty(sb.toString());
            }
        });


        ObservableList<Product> data = FXCollections.observableArrayList(productService.getAll());
        productTable.setItems(data);

        checkFocus();
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateClicked - open Waren Window");

        EditProductController.setProduct(null);

        Stage stage = (Stage) productTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editproduct.fxml");

        stage.setTitle("Waren");
        stage.setScene(new Scene(scene, 600, 414));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onWarenClicked - open Waren Window");

        EditProductController.setProduct(productTable.getFocusModel().getFocusedItem());

        Stage stage = (Stage) productTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editproduct.fxml");

        stage.setTitle("Waren");
        stage.setScene(new Scene(scene, 600, 414));
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
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                productService.remove(selectedProduct);
                productTable.getItems().remove(selectedProduct);
            }
        }

        checkFocus();
    }

    @FXML
    private void checkFocus() {
        Product selectedProduct = productTable.getFocusModel().getFocusedItem();
        if (selectedProduct == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
    }


    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
