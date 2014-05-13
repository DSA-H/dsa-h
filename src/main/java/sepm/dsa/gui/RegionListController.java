package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

import java.util.List;

@Service("RegionListController")
public class RegionListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(RegionListController.class);

    @Autowired
    private RegionService regionService;
    @Autowired
    private RegionBorderService regionBorderService;

    @FXML
    private TableView<Region> regionTable;
    @FXML
    private TableColumn regionColumn;
    @FXML
    private TableColumn borderColumn;
    @FXML
    private TableColumn colorColumn;
    @FXML
    private Button createButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise RegionListController");

        regionColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        borderColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Region, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Region, String> r) {
                if (r.getValue() != null) {
                    int regionId = r.getValue().getId();
                    List<RegionBorder> borders = regionBorderService.getAllForRegion(regionId);
                    StringBuilder sb = new StringBuilder();
                    for (RegionBorder rb : borders) {
                        // not this region
                        if (rb.getPk().getRegion1().getId() != regionId) {
                            sb.append(rb.getPk().getRegion1().getName());
                        } else {
                            sb.append(rb.getPk().getRegion2().getName());
                        }
                        sb.append(", ");
                    }
                    if (sb.length() >= 2) {
                        sb.delete(sb.length() - 2, sb.length());
                    }
                    return new SimpleStringProperty(sb.toString());
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        colorColumn.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                return new TableCell<Region, String>() {
                    @Override
                    public void updateItem(String color, boolean empty) {
                        super.updateItem(color, empty);
                        if (!empty) {
                            color = "#" + color;
                            setStyle("-fx-background-color:" + color);
                        }else {
                            setStyle("-fx-background-color:#FFFFFF");
                        }
                        else {
                            setStyle("-fx-background-color:#FFFFFF");
                        }
                    }
                };
            }
        });

        updateRegionTable();
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateButtonPressed - open Gebiet-Details Window");
        Stage details = (Stage) regionTable.getScene().getWindow();
        Parent root = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        EditRegionController.setRegion(null);

        root = (Parent) loader.load("/gui/editregion.fxml");

        details.setTitle("Gebiet-Details");
        details.setScene(new Scene(root, 600, 438));
        details.show();
    }

    @FXML
    private void onEditButtonPressed() {

        log.debug("onEditButtonPressed - open Gebiet-Details Window");
        Stage details = (Stage) regionTable.getScene().getWindow();
        Parent root = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        Region selectedRegion = regionTable.getFocusModel().getFocusedItem();
        EditRegionController.setRegion(selectedRegion);

        root = (Parent) loader.load("/gui/editregion.fxml");

        details.setTitle("Gebiet-Details");
        details.setScene(new Scene(root, 600, 438));
        details.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        Region selectedRegion = regionTable.getFocusModel().getFocusedItem();

        if (selectedRegion != null) {
            regionService.remove(selectedRegion);
        }

        regionTable.getItems().remove(selectedRegion);

        checkFocus();
    }

    @FXML
    private void checkFocus() {
        Region selectedRegion = regionTable.getFocusModel().getFocusedItem();
        if (selectedRegion == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        }
        else{
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }

    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setRegionBorderService(RegionBorderService regionBorderService) {
        this.regionBorderService = regionBorderService;
    }
}
