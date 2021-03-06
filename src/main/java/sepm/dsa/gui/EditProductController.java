package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.model.Currency;
import sepm.dsa.service.*;

import java.util.*;

public class EditProductController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(EditProductController.class);
    private SpringFxmlLoader loader;
    private ProductService productService;
    private ProductCategoryService productCategoryService;
    private UnitService unitService;
    private RegionService regionService;
    private SaveCancelService saveCancelService;
    private CurrencySetService currencySetService;
    private CurrencyService currencyService;

    private Product selectedProduct;
    private boolean isNewProduct;

    @FXML
    private TextField nameField;
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
    private ComboBox<ProductCategory> categorieChoiceBox;
    @FXML
    private ComboBox<Region> regionChoiceBox;
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
    @FXML
    private TextField occurenceField;
    @FXML
    private ChoiceBox<Unit> unitBox;

    @FXML
    private TextField tf_CurrencyAmount1;
    @FXML
    private TextField tf_CurrencyAmount2;
    @FXML
    private TextField tf_CurrencyAmount3;
    @FXML
    private TextField tf_CurrencyAmount4;

    private TextField[] tf_CurrencyAmounts;

    @FXML
    private Label lbl_CurrencyAmount1;
    @FXML
    private Label lbl_CurrencyAmount2;
    @FXML
    private Label lbl_CurrencyAmount3;
    @FXML
    private Label lbl_CurrencyAmount4;

    private Label[] lbl_CurrencyAmounts;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
	    super.initialize(location, resources);

        log.debug("initialize EditProductController");
        lbl_CurrencyAmounts =
                new Label[] {
                        lbl_CurrencyAmount1,
                        lbl_CurrencyAmount2,
                        lbl_CurrencyAmount3,
                        lbl_CurrencyAmount4};

        tf_CurrencyAmounts =
                new TextField[] {
                        tf_CurrencyAmount1,
                        tf_CurrencyAmount2,
                        tf_CurrencyAmount3,
                        tf_CurrencyAmount4};
        List<String> attributeList = new ArrayList<>();
        for(ProductAttribute t : ProductAttribute.values()) {
            attributeList.add(t.getName());
        }
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

	    unitBox.getItems().setAll(unitService.getAll());
	    attributeBox.getItems().setAll(ProductAttribute.values());
    }

    @Override
    public void reload() {
        log.debug("reload EditProductController");

        List<ProductCategory> categoryList = productCategoryService.getAll();
        List<Region> regionList = regionService.getAll();

        categoryList.removeAll(categorieTable.getItems());
        regionList.removeAll(regionTable.getItems());

        if (selectedProduct != null && selectedProduct.getId() != null){
            isNewProduct = false;
            categoryList.removeAll(selectedProduct.getCategories());
            regionList.removeAll(selectedProduct.getRegions());
        }else if(selectedProduct != null && selectedProduct.getId() == null) {  // clone
            isNewProduct = true;
            categoryList.removeAll(selectedProduct.getCategories());
            regionList.removeAll(selectedProduct.getRegions());
        }else {
            isNewProduct = true;
            selectedProduct = new Product();
            attributeBox.getSelectionModel().select(0);
            refreshPriceView(0);
            unitBox.getSelectionModel().selectFirst();
        }
        categorieChoiceBox.getItems().setAll(categoryList);
        Collections.sort(regionList, (r1, r2) -> r1.getName().compareTo(r2.getName()));
	    regionChoiceBox.getItems().setAll(regionList);
    }

    @FXML
    private void onAddCategoryPressed() {
        log.debug("AddCategoryPressed");
        ProductCategory pc = categorieChoiceBox.getSelectionModel().getSelectedItem();
        if (pc != null){
            categorieTable.getItems().add(pc);
            categorieChoiceBox.getItems().remove(pc);
            categorieChoiceBox.getSelectionModel().selectFirst();
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
            regionChoiceBox.getSelectionModel().selectFirst();
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
        stage.close();
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        // save product
        String name = nameField.getText();
        int cost = 0;
        try {
            List<CurrencyAmount> currencyAmounts = new ArrayList<>(4);
            List<Currency> currencies = currencyService.getAllByCurrencySet(currencySetService.getDefaultCurrencySet());
            for (int i = 0; i < currencies.size() ; i++) {
                    CurrencyAmount a = new CurrencyAmount();
                    a.setCurrency(currencies.get(i));
                    Integer currencyAmount = Integer.parseInt(tf_CurrencyAmounts[i].getText());
                    if (currencyAmount < 0) {
                        throw new DSAValidationException("Preis muss > 0 sein");
                    }
                    a.setAmount(currencyAmount);
                    currencyAmounts.add(a);
            }
            cost = currencyService.exchangeToBaseRate(currencyAmounts);
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Kosten müssen eine ganze Zahl sein!");
        }
        int occurcene = 0;
        try {
            occurcene = Integer.parseInt(occurenceField.getText());
        }catch (NumberFormatException ex) {
            throw new DSAValidationException("Verbreitung muss eine ganze Prozentzahl (1-100) sein!");
        }
        ProductAttribute attribute = attributeBox.getValue();
        Set<ProductCategory> localCategoryList = new HashSet<>(categorieTable.getItems());
        Set<Region> localRegionList =  new HashSet<>(regionTable.getItems());

        selectedProduct.setName(name);
        selectedProduct.setCost(cost);
        selectedProduct.setUnit(unitBox.getValue());
        selectedProduct.setOccurence(occurcene);
        selectedProduct.setAttribute(attribute);
        selectedProduct.setQuality(qualityBox.isSelected());
        selectedProduct.setComment(commentField.getText());
        selectedProduct.setCategories(localCategoryList);
        selectedProduct.setRegions(localRegionList);

        if(isNewProduct) {
            productService.add(selectedProduct);
        }else {
            productService.update(selectedProduct);
        }
        saveCancelService.save();

        // return to productslist / productcategorie
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onAddAllProductionRegionsPressed() {
        log.debug("onAddAllProductionRegionsPressed");
        regionTable.getItems().addAll(regionChoiceBox.getItems());
        regionChoiceBox.getItems().clear();
        checkFocusRegion();
    }

    @FXML
    public void onRemoveAllProductionRegionsPressed() {
        log.debug("onRemoveAllProductionRegionsPressed");

        List<Region> regions = new ArrayList<>(regionChoiceBox.getItems().size() + regionTable.getItems().size());
        regions.addAll(regionChoiceBox.getItems());
        regions.addAll(regionTable.getItems());
        Collections.sort(regions, (r1, r2) -> r1.getName().compareTo(r2.getName()));
        regionChoiceBox.getItems().setAll(regions);
        regionTable.getItems().clear();
        checkFocusRegion();
    }

    private void refreshPriceView(int baseRatePrice) {
        log.info("calling refreshPriceView()");
        CurrencySet selected = currencySetService.getDefaultCurrencySet();
        List<Currency> currencies = currencyService.getAllByCurrencySet(selected);
        List<CurrencyAmount> currencyAmounts = currencySetService.toCurrencySet(selected, baseRatePrice);
        int i=0;
        for (Currency c : currencies) {
            log.info(lbl_CurrencyAmounts + ": " + lbl_CurrencyAmounts[i] + " " + c.getName());
            lbl_CurrencyAmounts[i].setText(c.getShortName());
            tf_CurrencyAmounts[i].setText(currencyAmounts.get(i).getAmount()+"");
            i++;
        }
        for (; i<4; i++) {
            lbl_CurrencyAmounts[i].setText("");
            tf_CurrencyAmounts[i].setText("");
        }

    }

    public void setProduct(Product product) {
        selectedProduct = product;
        if(selectedProduct != null) {
            nameField.setText(selectedProduct.getName());
            refreshPriceView(selectedProduct.getCost());
            attributeBox.getSelectionModel().select(selectedProduct.getAttribute());
	        regionTable.getItems().setAll(selectedProduct.getRegions());
	        categorieTable.getItems().setAll(selectedProduct.getCategories());
            commentField.setText(selectedProduct.getComment());
            qualityBox.setSelected(selectedProduct.getQuality());
            unitBox.getSelectionModel().select(selectedProduct.getUnit());
            occurenceField.setText(selectedProduct.getOccurence() + "");
        }
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setUnitService(UnitService unitService) {
        this.unitService = unitService;
    }

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }

    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

}
