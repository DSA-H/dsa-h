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
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;
import sepm.dsa.service.TraderCategoryService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private TableView<AssortmentNature> assortmentTable;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditRegionController");

        // init ChoiceBoxes
        List<String> parentCategories = new ArrayList<>();
        for (TraderCategory t : traderCategoryService.getAll()) {
            parentCategories.add(t.getName());
        }
        // set values if editing
        if (traderCategory != null) {
            isNewTraderCategory = false;
            nameField.setText(traderCategory.getName());
            parentCategories.remove(traderCategory);
            commentArea.setText(traderCategory.getComment());

            ObservableList<RegionBorder> data = FXCollections.observableArrayList(regionBorderService.getAllByRegion(traderCategory.getId()));
            borderTable.setItems(data);
        } else {
            isNewTraderCategory = true;
            traderCategory = new TraderCategory();
            temperatureChoiceBox.getSelectionModel().select(Temperature.MEDIUM.getValue());
            rainfallChoiceBox.getSelectionModel().select(RainfallChance.MEDIUM.getValue());

            ObservableList<RegionBorder> data = FXCollections.observableArrayList();
            borderTable.setItems(data);
        }

        // init border table
        borderColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RegionBorder, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RegionBorder, String> r) {
                if (r.getValue() != null) {
                    if (r.getValue()
                            .getRegion1()
                            .equals(traderCategory)) {
                        return new SimpleStringProperty(r.getValue().getRegion2().getName());
                    } else {
                        return new SimpleStringProperty(r.getValue().getRegion1().getName());
                    }
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        borderCostColumn.setCellValueFactory(new PropertyValueFactory<>("borderCost"));

        // init border choice box
        List<Region> otherRegions = regionService.getAll();
        otherRegions.remove(traderCategory);
        if(!isNewTraderCategory) {
            for (RegionBorder borders : regionBorderService.getAllByRegion(traderCategory.getId())) {
                if (borders.getRegion1().equals(traderCategory)) {
                    otherRegions.remove(borders.getRegion2());
                } else {
                    otherRegions.remove(borders.getRegion1());
                }
            }
        }
        borderChoiceBox.setItems(FXCollections.observableArrayList(otherRegions));
    }

    @FXML
    private void removeSelectedAssortment(){
        //TODO
    }

    @FXML
    private void addAssortmentClicked(){
        //TODO
    }

    @FXML
    private void onBorderCostColumnChanged() {
        log.debug("calling onBorderCostColumnChanged()");

    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/regionlist.fxml");

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
        for(RegionBorder border : regionBorderService.getAllByRegion(traderCategory.getId())) {
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
    private void onAddBorderPressed() {
        log.debug("calling AddBorderPressed");

        RegionBorder border = new RegionBorder();

        try {
            border.setRegion1(traderCategory);
            Region borderTo = (Region) borderChoiceBox.getSelectionModel().getSelectedItem();
            if (borderTo == null) {
                throw new DSAValidationException("Wählen sie ein Gebiet aus, welches an dieses Gebiet grenzen soll.");
            }
            border.setRegion2(borderTo);
            border.setBorderCost(Integer.parseInt(borderCost.getText()));
            borderTable.getItems().add(border);

            borderChoiceBox.getItems().remove(border.getRegion2());
            borderChoiceBox.getSelectionModel().selectFirst();
        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Grenzkosten müssen eine Zahl sein.");
        }
    }

    @FXML
    private void onRemoveBorderPressed() {
        log.debug("calling onRemoveBorderPressed");
        RegionBorder selectedborder = borderTable.getFocusModel().getFocusedItem();
        if (selectedborder != null) {
            borderTable.getItems().remove(selectedborder);
            if (selectedborder.getRegion1().equals(traderCategory)) {
                borderChoiceBox.getItems().add(selectedborder.getRegion2());
            } else {
                borderChoiceBox.getItems().add(selectedborder.getRegion1());
            }
        }
        checkFocus();
    }

    @FXML
    private void checkFocus() {
        log.debug("calling checkFocus()");
        RegionBorder selected = borderTable.getFocusModel().getFocusedItem();
        if (selected == null) {
            removeBorderButton.setDisable(true);
        } else {
            removeBorderButton.setDisable(false);
        }
    }

    public static void setRegion(Region region) {
        log.debug("calling setRegion(" + region + ")");
        traderCategory = region;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public static void setTraderCategory(TraderCategory traderCategory) {
        EditTraderCategoryController.traderCategory = traderCategory;
    }

    public static TraderCategory getTraderCategory() {
        return traderCategory;
    }

    public void setTraderCategoryService(TraderCategoryService traderCategoryService) {
        this.traderCategoryService = traderCategoryService;
    }

    public TraderCategoryService getTraderCategoryService() {
        return traderCategoryService;
    }

}
