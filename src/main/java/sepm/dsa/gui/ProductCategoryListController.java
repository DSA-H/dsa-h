package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.SaveCancelService;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ProductCategoryListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProductCategoryListController.class);
    SpringFxmlLoader loader;

    private ProductCategoryService productCategoryService;
    private SaveCancelService saveCancelService;

    @FXML
    private TreeView<ProductCategory> treeview;
    @FXML
    private TableView<Product> tableview;
    @FXML
    private TableColumn productColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize ProductListController");
        // init table

        /*productColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ObservableList<Product> data = FXCollections.observableArrayList(productService.getAll());
        productTable.setItems(data);
        */

        initializeTreeView();
    }

    private void initializeTreeView(){
        List<ProductCategory> productCategoryList = productCategoryService.getAll();


        TreeItem<ProductCategory> root = new TreeItem<ProductCategory>(new ProductCategory());
        root.setExpanded(true);
        getTreeChildren(productCategoryList, root);

        treeview.setRoot(root);
        treeview.setShowRoot(false);
        checkFocus();
    }

    private void getTreeChildren(List<ProductCategory> productCategoryList, TreeItem root){

        for (ProductCategory item: productCategoryList) {
            TreeItem<ProductCategory> node = createNode(item);
            root.getChildren().add(node);
        }
    }

    @FXML
    private void checkFocus() {
        if (treeview.getFocusModel().getFocusedItem() == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
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

        EditProductCategoryController.setProductCategory(treeview.getSelectionModel().getSelectedItem().getValue());

        Stage stage = (Stage) tableview.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editproductcategory.fxml");

        stage.setTitle("Warenkategorie");
        stage.setScene(new Scene(scene, 600, 414));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        ProductCategory selectedProductCategory = (treeview.getSelectionModel().getSelectedItem().getValue());

        if (selectedProductCategory != null) {
            log.debug("open Confirm-Delete-Product Dialog");
            org.controlsfx.control.action.Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie die Warenkategorie '" + selectedProductCategory.getName() + "' wirklich endgültig löschen?")
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                productCategoryService.remove(selectedProductCategory);
                saveCancelService.save();
                //could be replaced with a search-through-all-elements-childs algorithm
                initializeTreeView();
            }
        }

        checkFocus();
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    /**
     * based on: http://docs.oracle.com/javafx/2/api/javafx/scene/control/TreeItem.html
     * @param f
     * @return
     */
    private TreeItem<ProductCategory> createNode(final ProductCategory f) {
        return new TreeItem<ProductCategory>(f) {
            // We cache whether the File is a leaf or not. A File is a leaf if
            // it is not a directory and does not have any files contained within
            // it. We cache this as isLeaf() is called often, and doing the
            // actual check on File is expensive.
            private boolean isLeaf;

            // We do the children and leaf testing only once, and then set these
            // booleans to false so that we do not check again during this
            // run. A more complete implementation may need to handle more
            // dynamic file system situations (such as where a folder has files
            // added after the TreeView is shown). Again, this is left as an
            // exercise for the reader.
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override
            public ObservableList<TreeItem<ProductCategory>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;

                    // First getChildren() call, so we actually go off and
                    // determine the children of the File contained in this TreeItem.
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override
            public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    ProductCategory f = (ProductCategory) getValue();
                    isLeaf = (f.getChilds() == null);
                }

                return isLeaf;
            }

            private ObservableList<TreeItem<ProductCategory>> buildChildren(TreeItem<ProductCategory> TreeItem) {
                ProductCategory f = TreeItem.getValue();
                if (f != null && f.getChilds() != null) {
                    Set<ProductCategory> files = f.getChilds();
                    if (files != null & files.size() > 0) {
                        ObservableList<TreeItem<ProductCategory>> children = FXCollections.observableArrayList();

                        for (ProductCategory childFile : files) {
                            children.add(createNode(childFile));
                        }

                        return children;
                    }
                }

                return FXCollections.emptyObservableList();
            }
        };
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
