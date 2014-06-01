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

public class EditProductController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditProductController.class);
    private SpringFxmlLoader loader;
    private ProductService productService;
    private ProductCategoryService productCategoryService;
 //   private ProductUnitService productUnitService;
    private RegionService regionService;
    private SaveCancelService saveCancelService;

    private static Product selectedProduct;
    private boolean isNewProduct;

    @FXML
    private TextField nameField;
    @FXML
    private TextField costField;
    @FXML
    private TableView<ProductCategory> categorieTable;
    @FXML
    private TableView<Region> regionTable;
    @FXML
    private TableColumn regionColumn;
    @FXML
    private TableColumn categorieColumn;
    @FXML
    private TextArea commentField;
    @FXML
    private ChoiceBox<ProductAttribute> attributeBox;
    @FXML
    private CheckBox qualityBox;
    @FXML
    private ChoiceBox<ProductCategory> categorieChoiceBox;
    @FXML
    private ChoiceBox<Region> regionChoiceBox;
    @FXML
    private Button addCategorieButton;
    @FXML
    private Button removeCategorieButton;
    @FXML
    private Button addRegionButton;
    @FXML
    private Button removeRegionButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize EditProductController");

        List<String> attributeList = new ArrayList<>();
        for(ProductAttribute t : ProductAttribute.values()) {
            attributeList.add(t.getName());
        }
        List<ProductCategory> categoryList = productCategoryService.getAll();
        List<Region> regionList = regionService.getAll();

    //    List<ProductUnit> unitList = productUnitService.getAll();
    //    choice_unit.setItems(FXCollections.observableArrayList(unitList));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        attributeBox.setItems(FXCollections.observableArrayList(ProductAttribute.values()));
        categorieChoiceBox.setItems(FXCollections.observableArrayList(categoryList));
        regionChoiceBox.setItems(FXCollections.observableArrayList(regionList));

        if (selectedProduct != null){
            isNewProduct = false;
            nameField.setText(selectedProduct.getName());
            costField.setText(selectedProduct.getCost().toString());
            attributeBox.getSelectionModel().select(selectedProduct.getAttribute());
            //choice_unit.getSelectionModel().select(productUnitService.get(selectedProduct.getUnit()));
            ObservableList<Region> regionData = FXCollections.observableArrayList(selectedProduct.getRegions());
            regionList.removeAll(selectedProduct.getRegions());
            regionTable.setItems(regionData);
            ObservableList<ProductCategory> categoryData = FXCollections.observableArrayList(selectedProduct.getCategories());
            categoryList.removeAll(selectedProduct.getCategories());
            categorieTable.setItems(categoryData);
            commentField.setText(selectedProduct.getComment());
            qualityBox.setSelected(selectedProduct.getQuality());
        }else {
            isNewProduct = true;
            selectedProduct = new Product();
            attributeBox.getSelectionModel().select(0);
        }
    }

    @FXML
    private void onAddCategoryPressed() {
        log.debug("AddCategoryPressed");
        ProductCategory pc = categorieChoiceBox.getSelectionModel().getSelectedItem();
        if (pc != null){
            categorieTable.getItems().add(pc);
            categorieChoiceBox.getItems().remove(pc);
        }else {
            throw new DSAValidationException("Keine Warenkategorie gewählt!");
        }
    }

    @FXML
    private void onRemoveCategoryPressed() {
        log.debug("RemoveCategoryPressed");

        ProductCategory pc = categorieTable.getSelectionModel().getSelectedItem();
        if (pc != null){
            categorieTable.getItems().remove(pc);
            categorieChoiceBox.getItems().add(pc);
        }
        checkFocusCategory();
    }

    @FXML
    private void onAddProductionPressed() {
        log.debug("AddProductionPressed");

        Region r = regionChoiceBox.getSelectionModel().getSelectedItem();
        if (r != null){
            regionTable.getItems().add(r);
            regionChoiceBox.getItems().remove(r);
        }else {
            throw new DSAValidationException("Keine Region gewählt!");
        }
    }

    @FXML
    private void onRemoveProductionPressed() {
        log.debug("RemoveProductionPressed");

        Region r = regionTable.getSelectionModel().getSelectedItem();
        if (r != null){
            regionTable.getItems().remove(r);
            regionChoiceBox.getItems().add(r);
        }
        checkFocusRegion();
    }

    @FXML
    private void checkFocusCategory() {
        ProductCategory selected = categorieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            removeCategorieButton.setDisable(true);

        } else{
            removeCategorieButton.setDisable(false);
        }
    }

    @FXML
    private void checkFocusRegion() {
        Region selected = regionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            removeRegionButton.setDisable(true);
        } else{
            removeRegionButton.setDisable(false);
        }
    }


    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        saveCancelService.cancel();

        Stage stage = (Stage) nameField.getScene().getWindow();

        Parent scene = (Parent) loader.load("/gui/productslist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        // save product
        String name = nameField.getText();
        int cost = 0;
        try {
            cost = Integer.parseInt(costField.getText());
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Basiskosten müssen eine ganze Zahl sein!");
        }
     //   ProductUnit unit = (ProductUnit) choice_unit.getSelectionModel().getSelectedItem();
        ProductAttribute attribute = attributeBox.getValue();
        Set<ProductCategory> localCategoryList = new HashSet<>(categorieTable.getItems());
        Set<Region> localRegionList =  new HashSet<>(regionTable.getItems());

        selectedProduct.setName(name);
        selectedProduct.setCost(cost);
        //selectedProduct.setUnit(unit);
        selectedProduct.setAttribute(attribute);
        selectedProduct.setQuality(qualityBox.isSelected());
        selectedProduct.setComment(costField.getText());
        selectedProduct.setCategories(localCategoryList);
        selectedProduct.setRegions(localRegionList);

        if(isNewProduct) {
            productService.add(selectedProduct);
        }else {
            productService.update(selectedProduct);
        }
        saveCancelService.save();

        // return to productslist
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/productslist.fxml");
        stage.setScene(new Scene(scene, 600, 438));
    }


    public static void setProduct(Product product) {
        selectedProduct = product;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

  /*  public void setProductUnitService(ProductUnitService productUnitService) {
        this.productUnitService = productUnitService;
    }*/

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
