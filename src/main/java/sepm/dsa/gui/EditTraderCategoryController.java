package sepm.dsa.gui;

import com.sun.deploy.uitoolkit.impl.fx.ui.FXConsole;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
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
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.service.TraderCategoryService;

import java.net.URL;
import java.util.*;

public class EditTraderCategoryController extends BaseControllerImpl {

    private TraderCategoryService traderCategoryService;
    private ProductCategoryService productCategoryService;
    private AssortmentNatureService assortmentNatureService;
    private SaveCancelService saveCancelService;


    private TraderCategory traderCategory;

    private static final Logger log = LoggerFactory.getLogger(EditTraderCategoryController.class);
    private SpringFxmlLoader loader;
    // true if the traderCategory is not editing
    private boolean isNewTraderCategory = false;

    @FXML
    private TextField nameField;
    @FXML
    private TextArea commentField;
    @FXML
    private ComboBox<ProductCategory> productCategoryComboBox;
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
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        assortmentColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
        defaultOccurenceColumn.setCellValueFactory(new PropertyValueFactory<>("defaultOccurence"));
    }

    @Override
    public void reload() {
        log.debug("reload EditTraderCategoryController");
        // init ChoiceBoxes
        List<ProductCategory> productCategories = productCategoryService.getAll();

        // set values if editing
        if (traderCategory != null) {
            removeAssortButton.setDisable(false);

            Collection<AssortmentNature> assortmentNaturesAlreadySelected = traderCategory.getAssortments().values();
            for(AssortmentNature as : assortmentTable.getItems()) {
                productCategories.remove(as.getProductCategory());
            }
            productCategoryComboBox.getItems().setAll(productCategories);
        } else {
            traderCategory = new TraderCategory();
            productCategoryComboBox.getItems().setAll(productCategories);
        }

        checkFocus();
    }

    @FXML
    private void removeSelectedAssortment() {
        log.debug("calling removeSelectedAssortment");
        AssortmentNature selAssortment = assortmentTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
        if (selAssortment != null) {
            assortmentTable.getItems().remove(selAssortment);
            productCategoryComboBox.getItems().add(selAssortment.getProductCategory());
        }
        checkFocus();
    }

    @FXML
    private void addAssortmentClicked() {
        log.debug("calling addAssortmentClicked");

        ProductCategory selectedProductCategory = productCategoryComboBox.getSelectionModel().getSelectedItem();
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

        reload();

        productCategoryComboBox.getItems().remove(selectedProductCategory);
        productCategoryComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void onAssortmentsSelectedChanged() {
        log.debug("calling onAssortmentsSelectedChanged()");
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
        log.debug("calling SaveButtonPressed");

        // save traderCategory
        traderCategory.setComment(commentField.getText());
        traderCategory.setName(nameField.getText());

        if (assortmentTable.getItems().size() == 0) {
            throw new DSAValidationException("Mindestens eine Warenkategorie muss zugewiesen sein");
        }

        if (isNewTraderCategory) {
            traderCategoryService.add(traderCategory);
        } else {
            traderCategoryService.update(traderCategory);
        }

        List<AssortmentNature> localAssortmentsList = assortmentTable.getItems();
        List<AssortmentNature> removeList = new ArrayList<>();
        Collection<AssortmentNature> assortmentNatures = traderCategory.getAssortments().values();
        for (AssortmentNature a : assortmentNatures) {
            boolean contain = false;
            for (AssortmentNature localAssortment : localAssortmentsList) {
                if (localAssortment.getProductCategory().equals(a.getProductCategory())) {
                    a.setDefaultOccurence(localAssortment.getDefaultOccurence());
                    assortmentNatureService.update(a);
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                removeList.add(a);
            }
            localAssortmentsList.remove(a);
        }
        for(AssortmentNature a : removeList) {
            assortmentNatureService.remove(a);
            traderCategory.getAssortments().remove(a);
        }
        for (AssortmentNature assortmentNature : localAssortmentsList) {
            assortmentNatureService.add(assortmentNature);
            traderCategory.getAssortments().put(assortmentNature.getProductCategory(), assortmentNature);
        }

        saveCancelService.save();

        // return to traderCategoryList
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void checkFocus() {
        log.debug("calling checkFocus()");
        AssortmentNature selected = assortmentTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
        if (selected == null) {
            removeAssortButton.setDisable(true);
        } else {
            removeAssortButton.setDisable(false);
        }
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setTraderCategory(TraderCategory traderCategory) {
        this.traderCategory = traderCategory;
        if(traderCategory != null) {
            isNewTraderCategory = false;
            nameField.setText(traderCategory.getName());
            commentField.setText(traderCategory.getComment());
            assortmentTable.getItems().setAll(traderCategory.getAssortments().values());
        }else {
            isNewTraderCategory = true;
        }
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

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
