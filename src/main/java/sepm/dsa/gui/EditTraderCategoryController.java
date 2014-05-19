package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.TraderCategoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("EditTraderCategoryController")
public class EditTraderCategoryController implements Initializable {
    private TraderCategoryService traderCategoryService;
    private static TraderCategory traderCategory;

    private static final Logger log = LoggerFactory.getLogger(EditTraderCategoryController.class);
    private SpringFxmlLoader loader;
    // true if the traderCategory is not editing
    private boolean isNewTraderCategory;

    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<AssortmentNature> assortmentChoiceBox;
    @FXML
    private ChoiceBox<TraderCategory> subCategoryChoiceBox;
    @FXML
    private TableView<AssortmentNature> assortmentTable;
    @FXML
    private Button removeAssortButton;
    @FXML
    private TableColumn<AssortmentNature, String> assortmentColumn;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditRegionController");

        // init ChoiceBoxes
        List<TraderCategory> parentCategories = new ArrayList<>();
        // set values if editing
        if (traderCategory != null) {
            isNewTraderCategory = false;
            nameField.setText(traderCategory.getName());
            parentCategories.remove(traderCategory);
            subCategoryChoiceBox.setItems(FXCollections.observableArrayList(parentCategories));
            subCategoryChoiceBox.getSelectionModel().select(traderCategory);
            assortmentChoiceBox.setItems(FXCollections.observableArrayList(traderCategory.getAssortments()));
            assortmentColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
            assortmentTable.setItems(FXCollections.observableArrayList(traderCategory.getAssortments()));

        } else {
            isNewTraderCategory = true;
            traderCategory = new TraderCategory();
            subCategoryChoiceBox.setItems(FXCollections.observableArrayList(parentCategories));
        }

        // init assortment table based on the selection of the selection of parent category ;)
        assortmentColumn.setCellValueFactory(new PropertyValueFactory<>("productCategory"));
    }

    @FXML
    private void removeSelectedAssortment() {
        log.debug("calling removeSelectedAssortment");
        AssortmentNature selAssortment = assortmentTable.getFocusModel().getFocusedItem();
        if (selAssortment != null) {
            assortmentTable.getItems().remove(selAssortment);
            //TODO check macht das Sinn?
            assortmentChoiceBox.getItems().add(selAssortment);
        }
        checkFocus();
    }

    @FXML
    private void changedParent() {
        log.debug("calling changedParent()");
        assortmentChoiceBox.setItems(FXCollections.observableArrayList(subCategoryChoiceBox.getSelectionModel().getSelectedItem().getAssortments()));
    }

    @FXML
    private void addAssortmentClicked() {
        log.debug("calling addAssortmentClicked");

        AssortmentNature selectedChoiceAssortment = assortmentChoiceBox.getSelectionModel().getSelectedItem();
        if (selectedChoiceAssortment == null) {
            throw new DSAValidationException("Wählen sie eine Ware Aus aus, welche Händler dieser Kategorie anbieten.");
        }
        assortmentTable.getItems().add(selectedChoiceAssortment);

        assortmentChoiceBox.getItems().remove(selectedChoiceAssortment);
        assortmentChoiceBox.getSelectionModel().selectFirst();

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
        String name = nameField.getText();
        Temperature temperature = Temperature.parse(temperatureChoiceBox.getSelectionModel().getSelectedIndex());
        RainfallChance rainfallChance = RainfallChance.parse(rainfallChoiceBox.getSelectionModel().getSelectedIndex());
        String comment = commentArea.getText();
        Color selectedColor = colorPicker.getValue();
        String colorString =
                Integer.toHexString((int) (selectedColor.getRed() * 255)) + "" +
                        Integer.toHexString((int) (selectedColor.getGreen() * 255)) + "" +
                        Integer.toHexString((int) (selectedColor.getBlue() * 255));

        traderCategory.setColor(colorString);
        traderCategory.setName(name);
        traderCategory.setComment(comment);
        traderCategory.setTemperature(temperature);
        traderCategory.setRainfallChance(rainfallChance);

        if (isNewTraderCategory) {
            regionService.add(traderCategory);
        } else {
            regionService.update(traderCategory);
        }

        // save borders
        List<RegionBorder> localBorderList = borderTable.getItems();
        for (RegionBorder border : regionBorderService.getAllByRegion(traderCategory.getId())) {
            boolean contain = false;
            for (RegionBorder localBorder : localBorderList) {
                if (localBorder.equalsById(border)) {
                    regionBorderService.update(border);
                    contain = true;
                    break;
                }
            }
            if (!contain) {
                regionBorderService.remove(border);
            }
            localBorderList.remove(border);
        }
        for (RegionBorder border : localBorderList) {
            regionBorderService.add(border);
        }

        // return to regionlist
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/regionlist.fxml");

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
}
