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
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductAttribute;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.ProductService;
import sepm.dsa.service.RegionService;
import sepm.dsa.service.SaveCancelService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("EditProductController")
public class EditProductCategoryController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(EditProductCategoryController.class);
    private SpringFxmlLoader loader;
    private ProductCategoryService productCategoryService;
    private SaveCancelService saveCancelService;

    private ProductCategory selectedProductCategory;
    private boolean isNewProductCategory;

    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<ProductCategory> choiceParent;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize EditProductCategoryController");
    }

    @Override
    public void reload() {
        log.debug("reload EditProductCategoryController");
        List<ProductCategory> categoryList = productCategoryService.getAll();

        if (selectedProductCategory != null){
            isNewProductCategory = false;
            nameField.setText(selectedProductCategory.getName());
            categoryList.remove(selectedProductCategory);
            // remove all childs
            List<ProductCategory> childs = productCategoryService.getAllChilds(selectedProductCategory);
            for (ProductCategory child : childs) {
                categoryList.remove(child);
            }
            choiceParent.getSelectionModel().select(selectedProductCategory.getParent());
        }else {
            isNewProductCategory = true;
            selectedProductCategory = new ProductCategory();
        }

        choiceParent.setItems(FXCollections.observableArrayList(categoryList));
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        saveCancelService.cancel();
        Stage stage = (Stage) nameField.getScene().getWindow();

        Parent scene = (Parent) loader.load("/gui/productcategorylist.fxml");
        ProductCategoryListController ctrl = loader.getController();
        ctrl.reload();

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        String name = nameField.getText();
        ProductCategory parent = choiceParent.getSelectionModel().getSelectedItem();
        selectedProductCategory.setName(name);
        selectedProductCategory.setParent(parent);

        // save product
//        ProductCategory p = new ProductCategory();
//        p.setName(name);
//        p.setId(selectedProductCategory.getId());
//        p.setParent(choiceParent.getSelectionModel().getSelectedItem());

        if(isNewProductCategory) {
            productCategoryService.add(selectedProductCategory);
        }else {
            productCategoryService.update(selectedProductCategory);
        }
        saveCancelService.save();

        // return to productcategorylist
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/productcategorylist.fxml");
        ProductCategoryListController ctrl = loader.getController();
        ctrl.reload();
        stage.setScene(new Scene(scene, 600, 438));
    }


    public void setProductCategory(ProductCategory productCategory) {
        selectedProductCategory = productCategory;
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
