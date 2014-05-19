package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.TraderCategoryService;

import java.util.ArrayList;
import java.util.List;

@Service("EditTraderCategoryController")
public class EditTraderCategoryController implements Initializable {
    private TraderCategoryService traderCategoryService;

    private ProductCategoryService productCategoryService;

    private static TraderCategory traderCategory;

    private static final Logger log = LoggerFactory.getLogger(EditTraderCategoryController.class);
    private SpringFxmlLoader loader;
    // true if the traderCategory is not editing
    private boolean isNewTraderCategory;

    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<ProductCategory> productCategoryChoiceBox;
    @FXML
    private TableView<AssortmentNature> assortmentTable;
    @FXML
    private Button removeAssortButton;
    @FXML
    private TableColumn<AssortmentNature, String> assortmentColumn;
    //TODO check wierum das sein muss
    @FXML
    private TableColumn<Integer, String> defaultOccurenceColumn;
    @FXML
    private TextField defaultOccurence;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditRegionController");

        // init ChoiceBoxes
        List<ProductCategory> productCategories = new ArrayList<>();
        /*
        //TODO erst verwenden wenn der service wirklich funktioniert
        productCategories = productCategoryService.getAll();
        */
        //Try on mock ############
        ProductCategory p1 = new ProductCategory();
        p1.setName("dfsdf");
        ProductCategory p2 = new ProductCategory();
        p1.setName("kjk");
        productCategories.add(p1);
        productCategories.add(p2);

        // end mock ############

        // set values if editing
        if (traderCategory != null) {
            isNewTraderCategory = false;
            nameField.setText(traderCategory.getName());
            productCategories.remove(traderCategory.getAssortments());

            productCategoryChoiceBox.setItems(FXCollections.observableArrayList(productCategories));
            assortmentColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
            assortmentTable.setItems(FXCollections.observableArrayList(traderCategory.getAssortments()));

        } else {
            isNewTraderCategory = true;
            traderCategory = new TraderCategory();
            productCategoryChoiceBox.setItems(FXCollections.observableArrayList(productCategories));
        }
    }

    @FXML
    private void removeSelectedAssortment() {
        log.debug("calling removeSelectedAssortment");
        AssortmentNature selAssortment = assortmentTable.getFocusModel().getFocusedItem();
        if (selAssortment != null) {
            assortmentTable.getItems().remove(selAssortment);
            productCategoryChoiceBox.getItems().add(selAssortment.getProductCategory());
        }
        checkFocus();
    }

    @FXML
    private void addAssortmentClicked() {
        log.debug("calling addAssortmentClicked");

        ProductCategory selectedProductCategory = productCategoryChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedProductCategory == null) {
            throw new DSAValidationException("Wählen sie eine Warenkategorie aus");
        }
        AssortmentNature assortToSave = new AssortmentNature();
        assortToSave.setProductCategory(selectedProductCategory);
        int defaultOcc = 100;
        if (!defaultOccurence.getText().isEmpty()) {
            try {
                defaultOcc = Integer.parseInt(defaultOccurence.getText());
            } catch (NumberFormatException ex) {
                throw new DSAValidationException("Vorkommen muss eine ganze Zahl zwischen 0 und 100 sein.");
            }
        } else {
            //Empty case
            //do nothing just use the 100 as default
        }
        assortToSave.setDefaultOccurence(defaultOcc);
        assortmentTable.getItems().add(assortToSave);

        productCategoryChoiceBox.getItems().remove(selectedProductCategory);
        productCategoryChoiceBox.getSelectionModel().selectFirst();

        //TODO besser / schöner gestalten -> evtl. inkrementelle Suche oder sonstwas
        //TODO multiple select ermöglichen
    }

    @FXML
    private void onAssortmentsSelectedChanged() {
        log.debug("calling onAssortmentsSelectedChanged()");

    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/tradercategorylist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("calling SaveButtonPressed");

        // save region
//        String name = nameField.getText();
//        Temperature temperature = Temperature.parse(temperatureChoiceBox.getSelectionModel().getSelectedIndex());
//        RainfallChance rainfallChance = RainfallChance.parse(rainfallChoiceBox.getSelectionModel().getSelectedIndex());
//        String comment = commentArea.getText();
//        Color selectedColor = colorPicker.getValue();
//        String colorString =
//                Integer.toHexString((int) (selectedColor.getRed() * 255)) + "" +
//                        Integer.toHexString((int) (selectedColor.getGreen() * 255)) + "" +
//                        Integer.toHexString((int) (selectedColor.getBlue() * 255));
//
//        traderCategory.setColor(colorString);
//        traderCategory.setName(name);
//        traderCategory.setComment(comment);
//        traderCategory.setTemperature(temperature);
//        traderCategory.setRainfallChance(rainfallChance);
//
//        if (isNewTraderCategory) {
//            regionService.add(traderCategory);
//        } else {
//            regionService.update(traderCategory);
//        }
//
//        // save borders
//        List<RegionBorder> localBorderList = borderTable.getItems();
//        for (RegionBorder border : regionBorderService.getAllByRegion(traderCategory.getId())) {
//            boolean contain = false;
//            for (RegionBorder localBorder : localBorderList) {
//                if (localBorder.equalsById(border)) {
//                    regionBorderService.update(border);
//                    contain = true;
//                    break;
//                }
//            }
//            if (!contain) {
//                regionBorderService.remove(border);
//            }
//            localBorderList.remove(border);
//        }
//        for (RegionBorder border : localBorderList) {
//            regionBorderService.add(border);
//        }

        // return to traderCategoryList
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/tradercategorylist.fxml");

        stage.setScene(new Scene(scene, 600, 438));
    }

    @FXML
    private void checkFocus() {
        log.debug("calling checkFocus()");
        AssortmentNature selected = assortmentTable.getFocusModel().getFocusedItem();
        if (selected == null) {
            removeAssortButton.setDisable(true);
        } else {
            removeAssortButton.setDisable(false);
        }
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public static void setTraderCategory(TraderCategory traderCategory) {
        EditTraderCategoryController.traderCategory = traderCategory;
    }

    public void setTraderCategoryService(TraderCategoryService traderCategoryService) {
        this.traderCategoryService = traderCategoryService;
    }

    public ProductCategoryService getProductCategoryService() {
        return productCategoryService;
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }
}
