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
    private TextArea textarea_comment;
    @FXML
    private TableView<String> tableview_category;
    @FXML
    private TableView<String> tableview_production;
    @FXML
    private TableColumn tablecolumn_category;
    @FXML
    private TableColumn tablecolumn_production;
    @FXML
    private Button button_remove_category;
    @FXML
    private Button button_add_category;
    @FXML
    private Button button_remove_production;
    @FXML
    private Button button_add_production;
    @FXML
    private Button button_abort;
    @FXML
    private Button button_save;

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
        //unit

        choice_attribute.setItems(FXCollections.observableArrayList(attributeList));
        choice_category.setItems(FXCollections.observableArrayList(categoryList));
        choice_production.setItems(FXCollections.observableArrayList(productionsList));
        //unit

        if (selectedProduct != null){
            isNewProduct = false;
            text_name.setText(selectedProduct.getName());
            text_cost.setText(selectedProduct.getCost().toString());
            choice_attribute.getSelectionModel().select(selectedProduct.getAttribute().getValue());
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
/*
    @FXML
    private void onBorderCostColumnChanged() {
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        scene = (Parent) loader.load("/gui/regionlist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        // save region
        String name = nameField.getText();
        Temperature temperature = Temperature.parse(temperatureChoiceBox.getSelectionModel().getSelectedIndex());
        RainfallChance rainfallChance = RainfallChance.parse(rainfallChoiceBox.getSelectionModel().getSelectedIndex());
        String comment = commentArea.getText();
        Color selectedColor = colorPicker.getValue();
        String colorString =
                Integer.toHexString((int) (selectedColor.getRed()*255)) + "" +
                Integer.toHexString((int) (selectedColor.getGreen()*255)) + "" +
                Integer.toHexString((int) (selectedColor.getBlue()*255));

        selectedRegion.setColor(colorString);
        selectedRegion.setName(name);
        selectedRegion.setComment(comment);
        selectedRegion.setTemperature(temperature);
        selectedRegion.setRainfallChance(rainfallChance);

        if(isNewRegion) {
            regionService.add(selectedRegion);
        }else {
            regionService.update(selectedRegion);
        }

        // save borders
        List<RegionBorder> localBorderList = borderTable.getItems();
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
    }

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
