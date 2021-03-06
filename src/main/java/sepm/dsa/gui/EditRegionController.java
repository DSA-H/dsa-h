package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.RainfallChance;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.Temperature;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;
import sepm.dsa.service.SaveCancelService;

import java.net.URL;
import java.util.*;

public class EditRegionController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(EditRegionController.class);
    private SpringFxmlLoader loader;

    private Region selectedRegion;

    private RegionService regionService;
    private RegionBorderService regionBorderService;
    private SaveCancelService saveCancelService;
    // true if the region is not editing
    private boolean isNewRegion;

    private ArrayList<Region> remainingBorders;

    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<Temperature> temperatureChoiceBox;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private ChoiceBox<RainfallChance> rainfallChoiceBox;
    @FXML
    private ComboBox borderComboBox;
    @FXML
    private TextField borderCost;
    @FXML
    private TextArea commentArea;
    @FXML
    private TableView<RegionBorder> borderTable;
    @FXML
    private TableColumn borderColumn;
    @FXML
    private TableColumn borderCostColumn;
    @FXML
    private Button cancelButton;
    @FXML
    private Button removeBorderButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
	temperatureChoiceBox.getItems().setAll(Temperature.values());
	rainfallChoiceBox.getItems().setAll(RainfallChance.values());

        // init border table
        borderColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<RegionBorder, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<RegionBorder, String> r) {
                if (r.getValue() != null) {
                    if (r.getValue()
                            .getRegion1()
                            .equals(selectedRegion)) {
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
    }

    @Override
    public void reload() {
        log.debug("reload EditRegionController");

        // set values if editing
        if (selectedRegion != null) {
            isNewRegion = false;
        } else {
            isNewRegion = true;
            selectedRegion = new Region();
            temperatureChoiceBox.getSelectionModel().select(Temperature.MEDIUM.getValue());
            rainfallChoiceBox.getSelectionModel().select(RainfallChance.MEDIUM.getValue());

	        borderTable.getItems().retainAll();
        }


        // init border choice box
        List<Region> otherRegions = regionService.getAll();
        otherRegions.remove(selectedRegion);
        for(RegionBorder regionBorder : borderTable.getItems()) {
            otherRegions.remove(regionBorder.getRegion1());
            otherRegions.remove(regionBorder.getRegion2());
        }
        for(RegionBorder regionBorder : selectedRegion.getAllBorders()) {
            otherRegions.remove(regionBorder.getRegion1());
            otherRegions.remove(regionBorder.getRegion2());
        }
        remainingBorders = new ArrayList<>(otherRegions);
        borderComboBox.getItems().setAll(otherRegions);
    }

    public void setRegionService(RegionService regionService) {
        log.debug("calling setRegionService(" + regionService + ")");
        this.regionService = regionService;
    }

    public void setRegionBorderService(RegionBorderService regionBorderService) {
        log.debug("calling setRegionBorderService(" + regionBorderService + ")");
        this.regionBorderService = regionBorderService;
    }

    @FXML
    private void onBorderCostColumnChanged() {
        log.debug("calling onBorderCostColumnChanged()");

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

        // save region
        String name = nameField.getText();
        Temperature temperature = temperatureChoiceBox.getSelectionModel().getSelectedItem();
        RainfallChance rainfallChance = rainfallChoiceBox.getSelectionModel().getSelectedItem();
        String comment = commentArea.getText();
        Color selectedColor = colorPicker.getValue();
        String colorString = "";
        String red = Integer.toHexString((int) (selectedColor.getRed() * 255));
        if (red.length() == 1) {
            red = "0" + red;
        }
        String green = Integer.toHexString((int) (selectedColor.getGreen() * 255));
        if (green.length() == 1) {
            green = "0" + green;
        }
        String blue = Integer.toHexString((int) (selectedColor.getBlue() * 255));
        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        colorString = red + green + blue;

        selectedRegion.setColor(colorString);
        selectedRegion.setName(name);
        selectedRegion.setComment(comment);
        selectedRegion.setTemperature(temperature);
        selectedRegion.setRainfallChance(rainfallChance);

        if (isNewRegion) {
            regionService.add(selectedRegion);
        } else {
            regionService.update(selectedRegion);
        }

        // save borders
        List<RegionBorder> localBorderList = borderTable.getItems();
        for (RegionBorder border : regionBorderService.getAllByRegion(selectedRegion.getId())) {
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

        saveCancelService.save();

        // return to regionlist
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onAddBorderPressed() {
        log.debug("calling AddBorderPressed");

        RegionBorder border = new RegionBorder();
        int bcost = 1;

        try {
            border.setRegion1(selectedRegion);
            Region borderTo = (Region) borderComboBox.getSelectionModel().getSelectedItem();
            if (borderTo == null) {
                throw new DSAValidationException("Wählen sie ein Gebiet aus, welches an dieses Gebiet grenzen soll.");
            }
            border.setRegion2(borderTo);
            bcost = (Integer.parseInt(borderCost.getText()));
        } catch (NumberFormatException ex) {
            throw new DSAValidationException("Grenzkosten müssen eine Zahl sein.");
        }
        if (bcost < 1) {
            throw new DSAValidationException("Grenzkosten von <1 sind nicht gültig");
        }
        border.setBorderCost(bcost);

        borderTable.getItems().add(border);

        long startTime = System.currentTimeMillis();
        remainingBorders.remove(border.getRegion2());
        long endTime1 = System.currentTimeMillis();
        borderComboBox.setItems(FXCollections.observableArrayList(remainingBorders));//getItems().setAll(remainingBorders);
        long endTime2 = System.currentTimeMillis();
        log.info((endTime1 - startTime) + " ms, gesamt: " + (endTime2 - startTime));
        borderComboBox.getSelectionModel().selectFirst();

    }

    @FXML
    private void onRemoveBorderPressed() {
        log.debug("calling onRemoveBorderPressed");
        RegionBorder selectedborder = borderTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
        if (selectedborder != null) {
            borderTable.getItems().remove(selectedborder);
            if (selectedborder.getRegion1().equals(selectedRegion)) {
                borderComboBox.getItems().add(selectedborder.getRegion2());
            } else {
                borderComboBox.getItems().add(selectedborder.getRegion1());
            }
        }
        checkFocus();
    }

    @FXML
    private void checkFocus() {
        log.debug("calling checkFocus()");
        RegionBorder selected = borderTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
        if (selected == null) {
            removeBorderButton.setDisable(true);
        } else {
            removeBorderButton.setDisable(false);
        }
    }

    public void setRegion(Region region) {
        log.debug("calling setRegion(" + region + ")");
        selectedRegion = region;
        if(selectedRegion != null) {
            nameField.setText(selectedRegion.getName());
            colorPicker.setValue(new Color(
                            (double) Integer.valueOf(selectedRegion.getColor().substring(0, 2), 16) / 255,
                            (double) Integer.valueOf(selectedRegion.getColor().substring(2, 4), 16) / 255,
                            (double) Integer.valueOf(selectedRegion.getColor().substring(4, 6), 16) / 255,
                            1.0)
            );
            temperatureChoiceBox.getSelectionModel().select(selectedRegion.getTemperature().getValue());
            rainfallChoiceBox.getSelectionModel().select(selectedRegion.getRainfallChance().getValue());
            commentArea.setText(selectedRegion.getComment());

	    borderTable.getItems().setAll(regionBorderService.getAllByRegion(selectedRegion.getId()));
        }
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
