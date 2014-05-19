package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.TraderCategoryService;
import sepm.dsa.service.TraderService;

import java.util.List;

@Service("TraderCategoryService")
public class TraderCategoryListController {
    private TraderCategoryService traderCategoryService;
    private static final Logger log = LoggerFactory.getLogger(TraderCategoryListController.class);
    private SpringFxmlLoader loader;
    private TraderService traderService;

    @FXML
    private TableView<TraderCategory> traderCategoryTable;
    @FXML
    private TableColumn traderCategoryColumn;
    @FXML
    private TableColumn traderColumn;
    @FXML
    private Button createButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise TraderCategoryListController");

        // init table
        traderCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        traderColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Region, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Region, String> r) {
                if (r.getValue() != null) {
                    int regionId = r.getValue().getId();
                    List<RegionBorder> borders = regionBorderService.getAllByRegion(regionId);
                    StringBuilder sb = new StringBuilder();
                    for (RegionBorder rb : borders) {
                        // not this region
                        if (rb.getRegion1().getId() != regionId) {
                            sb.append(rb.getRegion1().getName());
                        } else {
                            sb.append(rb.getRegion2().getName());
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

        ObservableList<TraderCategory> data = FXCollections.observableArrayList(traderCategoryService.getAll());
        traderCategoryTable.setItems(data);
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateButtonPressed - open Trader-Category-Details Window");

        EditTraderCategoryController.setTraderCategory(null);

        Stage stage = (Stage) traderCategoryTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/edittradercategory.fxml");

        stage.setTitle("Neuen Händler erstellen");
        stage.setScene(new Scene(root, 600, 438));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onEditButtonPressed - open Gebiet-Details Window");

        TraderCategory selectedTraderCategory = traderCategoryTable.getFocusModel().getFocusedItem();
        ((EditTraderCategoryController) loader.getController()).setTraderCategory(selectedTraderCategory);

        Stage stage = (Stage) traderCategoryTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/edittradercategory.fxml");

        stage.setTitle("Händler Kategorie bearbeiten");
        stage.setScene(new Scene(root, 600, 438));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        TraderCategory selectedTraderCategory = traderCategoryTable.getFocusModel().getFocusedItem();

        if (selectedTraderCategory != null) {
            log.debug("open Confirm-Delete-Region Dialog");
            Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie die Händlerkategorie '" + selectedTraderCategory.getName() + "' und alle zugehörigen Händler wirklich löschen?")
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                //TODO check if traders are removed as well !!!! ######################### <<<<< ####
                traderCategoryService.remove(selectedTraderCategory);
                traderCategoryTable.getItems().remove(selectedTraderCategory);
            }
        }
        checkFocus();
    }

    @FXML
    private void checkFocus() {
        TraderCategory selectedTraderCategory = traderCategoryTable.getFocusModel().getFocusedItem();
        if (selectedTraderCategory == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
    }

    public void setTraderCategoryService(TraderCategoryService traderCategoryService) {
        this.traderCategoryService = traderCategoryService;
    }

    public TraderCategoryService getTraderCategoryService() {
        return traderCategoryService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }

    public TraderService getTraderService() {
        return traderService;
    }
}
