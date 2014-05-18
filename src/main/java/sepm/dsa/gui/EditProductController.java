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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.util.ArrayList;
import java.util.List;

@Service("EditProductController")
public class EditProductController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditProductController.class);

    private static Product selectedProduct;
    private boolean isNewProduct;
    private ProductService productService;
    private ProductCategoryService productCategoryService;
    private ProductUnitService productUnitService;
    private RegionService regionService;

    @FXML
    private TextField text_name;
    @FXML
    private TextField text_cost;
    @FXML
    private ChoiceBox choice_attribute;
    @FXML
    private ChoiceBox choice_unit;
    @FXML
    private ChoiceBox<ProductCategory> choice_category;
    @FXML
    private ChoiceBox<Region> choice_production;
    @FXML
    private CheckBox check_quality;
    @FXML
    private TextArea textarea_comment;
    @FXML
    private TableView<ProductCategory> tableview_category;
    @FXML
    private TableView<Region> tableview_production;
    @FXML
    private TableColumn tablecolumn_category;
    @FXML
    private TableColumn tablecolumn_production;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize EditProductController");
        //DEBUG:
        /*ProductUnit pu = new ProductUnit();
        pu.setName("pu");
        pu.setUnitType("ut");
        pu.setValue(10);
        productUnitService.add(pu);*/

        // init ChoiceBoxes
        List<String> attributeList = new ArrayList<>();
        for(ProductAttribute t : ProductAttribute.values()) {
            attributeList.add(t.getName());
        }
        List<ProductCategory> categoryList = productCategoryService.getAll();
        List<Region> productionsList = regionService.getAll();
        List<ProductUnit> unitList = productUnitService.getAll();

        choice_attribute.setItems(FXCollections.observableArrayList(attributeList));
        choice_category.setItems(FXCollections.observableArrayList(categoryList));
        choice_production.setItems(FXCollections.observableArrayList(productionsList));
        choice_unit.setItems(FXCollections.observableArrayList(unitList));


        if (selectedProduct != null){
            isNewProduct = false;
            text_name.setText(selectedProduct.getName());
            text_cost.setText(selectedProduct.getCost().toString());
            choice_attribute.getSelectionModel().select(selectedProduct.getAttribute().getValue());
            //choice_unit.getSelectionModel().select(productUnitService.get(selectedProduct.getUnit()));
            ObservableList<Region> regionData = FXCollections.observableArrayList(selectedProduct.getRegions());
            tableview_production.setItems(regionData);
            ObservableList<ProductCategory> categoryData = FXCollections.observableArrayList(selectedProduct.getCategories());
            tableview_category.setItems(categoryData);
            textarea_comment.setText(selectedProduct.getComment());
        }else {
            isNewProduct = true;
            selectedProduct = new Product();

        }
    }


    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) text_name.getScene().getWindow();
        Parent scene = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        scene = (Parent) loader.load("/gui/productlist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        // save product
        String name = text_name.getText();
        Integer cost = Integer.parseInt(text_cost.getText());
        ProductUnit unit = (ProductUnit) choice_unit.getSelectionModel().getSelectedItem();
        ProductAttribute attribute = ProductAttribute.parse(choice_attribute.getSelectionModel().getSelectedIndex());
        List<ProductCategory> localCategoryList = tableview_category.getItems();
        List<Region> localRegionList = tableview_production.getItems();

        selectedProduct.setName(name);
        selectedProduct.setCost(cost);

        //selectedProduct.setUnit(unit);

        selectedProduct.setAttribute(attribute);
        selectedProduct.setQuality(check_quality.isSelected());
        selectedProduct.setComment(textarea_comment.getText());
        selectedProduct.setCategories(localCategoryList);
        selectedProduct.setRegions(localRegionList);

        if(isNewProduct) {
            productService.add(selectedProduct);
        }else {
            productService.update(selectedProduct);
        }

        // return to productslist
        Stage stage = (Stage) text_name.getScene().getWindow();
        Parent scene = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        scene = (Parent) loader.load("/gui/productslist.fxml");
        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onAddCategoryPressed() {
        log.debug("AddCategoryPressed");

        try {
            ProductCategory pc = choice_category.getSelectionModel().getSelectedItem();
            if (pc!=null){
                tableview_category.getItems().add(pc);
            }
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Fehler beim Hinzufügen der Kategorie.");
        }
    }

    @FXML
    private void onRemoveCategoryPressed() {
        try {
            ProductCategory pc = choice_category.getSelectionModel().getSelectedItem();
            if (pc!=null){
                tableview_category.getItems().remove(pc);
                choice_category.getItems().add(pc);
            }
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Fehler beim Entfernen der Kategorie.");
        }

        //checkFocus();
    }

    @FXML
    private void onAddProductionPressed() {
        log.debug("AddCategoryPressed");

        try {
            Region r = choice_production.getSelectionModel().getSelectedItem();
            if (r!=null){
                tableview_production.getItems().add(r);
            }
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Fehler beim hinzufügen des Produktionsgebietes");
        }
    }
    @FXML
    private void onRemoveProductionPressed() {
        try {
            Region r = choice_production.getSelectionModel().getSelectedItem();
            if (r!=null){
                tableview_production.getItems().remove(r);
                choice_production.getItems().add(r);
            }
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Fehler beim Entfernen des Produktionsgebietes.");
        }

        //checkFocus();
    }
/*


    @FXML
    private void checkFocus() {
        RegionBorder selected = borderTable.getFocusModel().getFocusedItem();
        if (selected == null) {
            removeBorderButton.setDisable(true);
        } else{
            removeBorderButton.setDisable(false);
        }
    }
    */

    public static void setProduct(Product product) {
        selectedProduct = product;
    }
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }
public void setProductUnitService(ProductUnitService productUnitService) {
        this.productUnitService = productUnitService;
    }
    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

}
