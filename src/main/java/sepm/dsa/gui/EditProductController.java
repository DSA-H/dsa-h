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
    private ChoiceBox choice_category;
    @FXML
    private ChoiceBox choice_production;
    @FXML
    private CheckBox check_quality;
    @FXML
    private TextArea textarea_comment;
    @FXML
    private TableView<ProductCategory> tableview_category;
    @FXML
    private TableView<String> tableview_production;
    @FXML
    private TableColumn tablecolumn_category;
    @FXML
    private TableColumn tablecolumn_production;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditProductController");

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
            choice_unit.getSelectionModel().select(productUnitService.get(selectedProduct.getUnit()));

            //tablecolumn_category <= selectedProduct.getCategories();
            //ObservableList<RegionBorder> data = FXCollections.observableArrayList(regionBorderService.getAllForRegion(selectedRegion.getId()));
            //borderTable.setItems(data);
            //tablecolumn_production <= selectedProduct.getProductions();
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

        selectedProduct.setName(name);
        selectedProduct.setCost(cost);
        selectedProduct.setUnit(unit);
        selectedProduct.setAttribute(attribute);
        selectedProduct.setQuality(check_quality.isSelected());
        selectedProduct.setComment(textarea_comment.getText());

        if(isNewProduct) {
            productService.add(selectedProduct);
        }else {
            productService.update(selectedProduct);
        }

        // save productcategories & productproductions
        List<ProductCategory> localCategoryList = tableview_category.getItems();
        /*
        for(RegionBorder border : regionBorderService.getAllForRegion(selectedRegion.getId())) {
            boolean contain = false;
            for(RegionBorder localBorder : localBorderList) {
                if (localBorder.getPk().equals(border.getPk())) {
                    regionBorderService.update(border);
                    contain = true;
                    break;
                }
            }
            if(!contain) {
                regionBorderService.remove(border);
            }
            localBorderList.remove(border);
        }
        for(RegionBorder border : localBorderList) {
            regionBorderService.add(border);
        }

        // return to regionlist
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Parent scene = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        scene = (Parent) loader.load("/gui/regionlist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
        */
    }
/*
    @FXML
    private void onAddBorderPressed() {
        log.debug("AddBorderPressed");

        RegionBorder border = new RegionBorder();
        RegionBorderPk borderPk = new RegionBorderPk();

        try {
            borderPk.setRegion1(selectedRegion);
            Region borderTo = (Region) borderChoiceBox.getSelectionModel().getSelectedItem();
            if(borderTo == null) {
                throw new DSAValidationException("Wählen sie ein Gebiet aus, welches an dieses Gebiet grenzen soll.");
            }
            borderPk.setRegion2(borderTo);
            border.setBorderCost(Integer.parseInt(borderCost.getText()));
            border.setPk(borderPk);
            borderTable.getItems().add(border);

            borderChoiceBox.getItems().remove(border.getPk().getRegion2());
            borderChoiceBox.getSelectionModel().selectFirst();
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Grenzkosten müssen eine Zahl sein.");
        }
    }

    @FXML
    private void onRemoveBorderPressed() {
        RegionBorder selectedborder = borderTable.getFocusModel().getFocusedItem();
        if(selectedborder != null) {
            borderTable.getItems().remove(selectedborder);
            if(selectedborder.getPk().getRegion1().equals(selectedRegion)) {
                borderChoiceBox.getItems().add(selectedborder.getPk().getRegion2());
            }else {
                borderChoiceBox.getItems().add(selectedborder.getPk().getRegion1());
            }
        }

        checkFocus();
    }

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
