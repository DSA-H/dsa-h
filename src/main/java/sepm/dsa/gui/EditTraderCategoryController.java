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
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.AssortmentNatureService;
import sepm.dsa.service.ProductCategoryService;
import sepm.dsa.service.TraderCategoryService;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EditTraderCategoryController implements Initializable {

    private TraderCategoryService traderCategoryService;
    private ProductCategoryService productCategoryService;
    private AssortmentNatureService assortmentNatureService;

    private static TraderCategory traderCategory;

    private static final Logger log = LoggerFactory.getLogger(EditTraderCategoryController.class);
    private SpringFxmlLoader loader;
    // true if the traderCategory is not editing
    private boolean isNewTraderCategory;

    @FXML
    private TextField nameField;
    @FXML
    private TextArea commentField;
    @FXML
    private ChoiceBox<ProductCategory> productCategoryChoiceBox;
    @FXML
    private TableView<AssortmentNature> assortmentTable;
    @FXML
    private Button removeAssortButton;
    @FXML
    private TableColumn<String, ProductCategory> assortmentColumn;
    @FXML
    private TableColumn<String, Integer> defaultOccurenceColumn;
    @FXML
    private TextField defaultOccurence;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize EditRegionController");

        // init ChoiceBoxes
        List<ProductCategory> productCategories = productCategoryService.getAll();
        assortmentColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        defaultOccurenceColumn.setCellValueFactory(new PropertyValueFactory<>("defaultOccurence"));

        // set values if editing
        if (traderCategory != null) {
            isNewTraderCategory = false;
            removeAssortButton.setDisable(false);
            nameField.setText(traderCategory.getName());
            commentField.setText(traderCategory.getComment());
            ArrayList<AssortmentNature> assortmentNaturesAlreadySelected = new ArrayList<>(traderCategory.getAssortments().values());
            for (AssortmentNature as : assortmentNaturesAlreadySelected) {
                productCategories.remove(as.getProductCategory());
            }

            productCategoryChoiceBox.setItems(FXCollections.observableArrayList(productCategories));
            assortmentTable.setItems(FXCollections.observableArrayList(traderCategory.getAssortments().values()));

        } else {
            isNewTraderCategory = true;
            traderCategory = new TraderCategory();
            productCategoryChoiceBox.setItems(FXCollections.observableArrayList(productCategories));
        }

        checkFocus();
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
        assortToSave.setTraderCategory(traderCategory);
        int defaultOcc = 100;
        if (!defaultOccurence.getText().isEmpty()) {
            try {
                defaultOcc = Integer.parseInt(defaultOccurence.getText());
            } catch (NumberFormatException ex) {
                throw new DSAValidationException("Häufigkeit muss eine ganze Zahl zwischen 1 und 100 sein.");
            }
        } else {
            //Empty case
            //do nothing just use the 100 as default
        }
        assortToSave.setDefaultOccurence(defaultOcc);
        assortmentNatureService.validate(assortToSave);
        assortmentTable.getItems().add(assortToSave);

        productCategoryChoiceBox.getItems().remove(selectedProductCategory);
        productCategoryChoiceBox.getSelectionModel().selectFirst();
        //TODO besser / schöner gestalten -> evtl. inkrementelle Suche oder sonstwas
        //TODO multiple select ermöglichen ???? Wie soll das gehen ??
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

        // save traderCategory
        traderCategory.setComment(commentField.getText());
        traderCategory.setName(nameField.getText());

        Map<ProductCategory, AssortmentNature> assortmentNatures = traderCategory.getAssortments();
        assortmentNatures.clear();
        assortmentTable.getItems().forEach(a -> assortmentNatures.put(a.getProductCategory(), a));

        if (assortmentNatures.size() <= 0) {
            throw new DSAValidationException("Mindestens eine Warenkategorie muss gewählt werden");
        }

        if (isNewTraderCategory) {
            traderCategoryService.add(traderCategory);
        } else {
            traderCategoryService.update(traderCategory);
        }

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

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    public void setAssortmentNatureService(AssortmentNatureService assortmentNatureService) {
        this.assortmentNatureService = assortmentNatureService;
    }
}
