package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.CurrencySet;
import sepm.dsa.service.CurrencySetService;
import sepm.dsa.service.SaveCancelService;

import java.net.URL;
import java.util.ResourceBundle;

public class CurrencySetListController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(ProductListController.class);
    private SpringFxmlLoader loader;

    private CurrencySetService currencySetService;
    private SaveCancelService saveCancelService;


    @FXML
    private TableView<CurrencySet> currencySetTable;
    @FXML
    private TableColumn<CurrencySet, String> nameColumn;
    @FXML
    private TableColumn<CurrencySet, String> currenciesColumn;

    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        currenciesColumn.setCellValueFactory(r -> {
            CurrencySet c = r.getValue();
            if (c != null) {
                return new SimpleStringProperty("" + c.currenciesSize());
            } else {
                return new SimpleStringProperty("");
            }
        });

        checkFocus();
    }

    @Override
    public void reload() {
        log.debug("reload CurrencySetListController");
        currencySetTable.getItems().setAll(currencySetService.getAll());

        checkFocus();
    }

    @FXML
    private void onTableClicked() {
        checkFocus();
    }

    private void checkFocus() {
        CurrencySet selected = currencySetTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateClicked - open CurrencySet Window");

        Stage stage = (Stage) currencySetTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editcurrencyset.fxml", stage);

        EditCurrencySetController ctrl = loader.getController();
        ctrl.setCurrencySet(null);
        ctrl.reload();

        stage.setTitle("Währungssystem");
        stage.setScene(new Scene(scene, 460, 370));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onWarenClicked - open CurrencySet Window");

        Stage stage = (Stage) currencySetTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editcurrencyset.fxml", stage);
        EditCurrencySetController ctrl = loader.getController();
        ctrl.setCurrencySet(currencySetTable.getSelectionModel().getSelectedItem());
        ctrl.reload();

        stage.setTitle("Währungssystem");
        stage.setScene(new Scene(scene, 460, 370));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected CurrencySet");
        CurrencySet selected = (currencySetTable.getSelectionModel().getSelectedItem());

        if (selected != null) {
            log.debug("open Confirm-Delete-Product Dialog");
            org.controlsfx.control.action.Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie das Währungssystem '" + selected.getName() + "' wirklich endgültig löschen?")
                    .actions(Dialog.Actions.NO, Dialog.Actions.YES)
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                currencySetService.remove(selected);
                saveCancelService.save();
                currencySetTable.getItems().remove(selected);
            }
        }

        checkFocus();
    }

    @FXML
    public void closeClicked() {
        Stage stage = (Stage) currencySetTable.getScene().getWindow();
        stage.close();
    }

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }
}
