package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Currency;
import sepm.dsa.model.ProductAttribute;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;
import sepm.dsa.service.CurrencyService;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.ProductService;

@Service("EditProductController")
public class EditCurrencyController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditCurrencyController.class);
    private SpringFxmlLoader loader;
    private ProductService productService;
    private ProductCategoryService productCategoryService;
    private SessionFactory sessionFactory;
    private CurrencyService currencyService;

    private static Currency selectedCurrency;
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
//        log.debug("initialize EditProductController");
//
//        List<String> attributeList = new ArrayList<>();
//        for(ProductAttribute t : ProductAttribute.values()) {
//            attributeList.add(t.getName());
//        }
//        List<ProductCategory> categoryList = productCategoryService.getAll();
//        List<Region> regionList = currencyService.getAll();
//
//    //    List<ProductUnit> unitList = productUnitService.getAll();
//    //    choice_unit.setItems(FXCollections.observableArrayList(unitList));
//        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        regionColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//
//        if (selectedCurrency != null){
//            isNewProduct = false;
//            nameField.setText(selectedCurrency.getName());
//            costField.setText(selectedCurrency.getCost().toString());
//            attributeBox.getSelectionModel().select(selectedCurrency.getAttribute());
//            //choice_unit.getSelectionModel().select(productUnitService.get(selectedCurrency.getUnit()));
//            ObservableList<Region> regionData = FXCollections.observableArrayList(selectedCurrency.getRegions());
//            regionList.removeAll(selectedCurrency.getRegions());
//            regionTable.setItems(regionData);
//            ObservableList<ProductCategory> categoryData = FXCollections.observableArrayList(selectedCurrency.getCategories());
//            categoryList.removeAll(selectedCurrency.getCategories());
//            categorieTable.setItems(categoryData);
//            commentField.setText(selectedCurrency.getComment());
//        }else {
//            isNewProduct = true;
//            selectedCurrency = new Currency();
//        }
//
//        attributeBox.setItems(FXCollections.observableArrayList(ProductAttribute.values()));
//        categorieChoiceBox.setItems(FXCollections.observableArrayList(categoryList));
//        regionChoiceBox.setItems(FXCollections.observableArrayList(regionList));
    }

    @FXML
    private void onAddCategoryPressed() {
        log.debug("AddCategoryPressed");
        ProductCategory pc = categorieChoiceBox.getSelectionModel().getSelectedItem();
        if (pc != null) {
            categorieTable.getItems().add(pc);
            categorieChoiceBox.getItems().remove(pc);
        } else {
            throw new DSAValidationException("Keine Warenkategorie gewählt!");
        }
    }

    @FXML
    private void onRemoveCategoryPressed() {
        log.debug("RemoveCategoryPressed");

        ProductCategory pc = categorieTable.getSelectionModel().getSelectedItem();
        if (pc != null) {
            categorieTable.getItems().remove(pc);
            categorieChoiceBox.getItems().add(pc);
        }
        checkFocusCategory();
    }

    @FXML
    private void onAddProductionPressed() {
        log.debug("AddProductionPressed");

        Region r = regionChoiceBox.getSelectionModel().getSelectedItem();
        if (r != null) {
            regionTable.getItems().add(r);
            regionChoiceBox.getItems().remove(r);
        } else {
            throw new DSAValidationException("Keine Region gewählt!");
        }
    }

    @FXML
    private void onRemoveProductionPressed() {
        log.debug("RemoveProductionPressed");

        Region r = regionTable.getSelectionModel().getSelectedItem();
        if (r != null) {
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

        } else {
            removeCategorieButton.setDisable(false);
        }
    }

    @FXML
    private void checkFocusRegion() {
        Region selected = regionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            removeRegionButton.setDisable(true);
        } else {
            removeRegionButton.setDisable(false);
        }
    }


    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) nameField.getScene().getWindow();

        Parent scene = (Parent) loader.load("/gui/productslist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

//        // save currency
//        String name = nameField.getText();
//        int cost = 0;
//        try {
//            cost = Integer.parseInt(costField.getText());
//        }catch (NumberFormatException ex) {
//            throw new DSAValidationException("Basiskosten müssen eine ganze Zahl sein!");
//        }
//     //   ProductUnit unit = (ProductUnit) choice_unit.getSelectionModel().getSelectedItem();
//        ProductAttribute attribute = attributeBox.getValue();
//        Set<ProductCategory> localCategoryList = new HashSet<>(categorieTable.getItems());
//        Set<Region> localRegionList =  new HashSet<>(regionTable.getItems());
//
//        selectedCurrency.setName(name);
//        selectedCurrency.setCost(cost);
//        //selectedCurrency.setUnit(unit);
//        selectedCurrency.setAttribute(attribute);
//        selectedCurrency.setQuality(qualityBox.isSelected());
//        selectedCurrency.setComment(costField.getText());
//        selectedCurrency.setCategories(localCategoryList);
//        selectedCurrency.setRegions(localRegionList);
//
//        if(isNewProduct) {
//            productService.add(selectedCurrency);
//        }else {
//            productService.update(selectedCurrency);
//        }
//
//        // return to productslist
//        Stage stage = (Stage) nameField.getScene().getWindow();
//        Parent scene = (Parent) loader.load("/gui/productslist.fxml");
//        stage.setScene(new Scene(scene, 600, 438));
    }


    public static void setCurrency(Currency currency) {
        selectedCurrency = currency;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
