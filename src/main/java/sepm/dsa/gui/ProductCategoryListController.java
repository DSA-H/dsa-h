package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.ProductService;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ProductCategoryListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProductCategoryListController.class);
    SpringFxmlLoader loader;

    private ProductCategoryService productCategoryService;
    private SessionFactory sessionFactory;

    @FXML
    private TreeView<ProductCategory> treeview;
    @FXML
    private TableView<Product> tableview;
    @FXML
    private TableColumn productColumn;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize ProductListController");
        // init table

        /*productColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ObservableList<Product> data = FXCollections.observableArrayList(productService.getAll());
        productTable.setItems(data);
        */

        List<ProductCategory> productCategoryList = productCategoryService.getAll();

        TreeItem<ProductCategory> root = new TreeItem<ProductCategory>();
        root.setExpanded(true);
        getTreeChildren(productCategoryList, root);

        treeview.setRoot(root);

    }

    private void getTreeChildren(List<ProductCategory> productCategoryList, TreeItem root){

        for (ProductCategory item: productCategoryList) {
            TreeItem<ProductCategory> node = new TreeItem<ProductCategory>((ProductCategory)item);

            if (item.getChilds()!=null){
                Set<ProductCategory> childs = item.getChilds();
                Iterator i = childs.iterator();
                while (i.hasNext()){
                    ProductCategory nextElem = (ProductCategory)i.next();
                    node.getChildren().add(getTreeChildren(nextElem));
                }
            }

            root.getChildren().add(node);
        }
    }

    private TreeItem<ProductCategory> getTreeChildren(ProductCategory elem){
        TreeItem<ProductCategory> node = new TreeItem<ProductCategory>(elem);//.getName());

        Set<ProductCategory> childs = elem.getChilds();
        Iterator i = childs.iterator();
        while (i.hasNext()){
            ProductCategory nextElem = (ProductCategory)i.next();
            node.getChildren().add(getTreeChildren(nextElem));
        }
        return node;
    }



    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateClicked - open Waren Window");

        EditProductCategoryController.setProductCategory(null);

        Stage stage = (Stage) tableview.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editproductcategory.fxml");

        stage.setTitle("Warenkategorie");
        stage.setScene(new Scene(scene, 600, 414));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onWarenClicked - open Waren Window");

        //EditProductCategoryController.setProductCategory(tableview.getFocusModel().getFocusedCell());

        Stage stage = (Stage) tableview.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editproductcategory.fxml");

        stage.setTitle("Warenkategorie");
        stage.setScene(new Scene(scene, 600, 414));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        /*Product selectedProduct = (productTable.getSelectionModel().getSelectedItem());

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
        }*/
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
