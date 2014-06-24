package sepm.dsa.gui;

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
import sepm.dsa.model.Currency;
import sepm.dsa.service.CurrencyService;
import sepm.dsa.service.SaveCancelService;

import java.net.URL;
import java.util.ResourceBundle;

public class CurrencyListController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(CurrencyListController.class);
    SpringFxmlLoader loader;

    private CurrencyService currencyService;

    private SaveCancelService saveCancelService;

    @FXML
    private TableView<Currency> currencyTable;
    @FXML
    private TableColumn currencyColumn;
    @FXML
    private TableColumn valueToBaseRateColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        log.debug("initialize CurrencyListController");
        // init table
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        valueToBaseRateColumn.setCellValueFactory(new PropertyValueFactory<>("valueToBaseRate"));
    }

    @Override
    public void reload() {
        log.debug("reload CurrencyListController");
		currencyTable.getItems().setAll(currencyService.getAll());
        checkFocus();
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateClicked - open Currency Window");

        Stage stage = new Stage();
        Parent scene = (Parent) loader.load("/gui/editcurrency.fxml", stage);
        EditCurrencyController ctrl = loader.getController();
        ctrl.setCurrency(null);
        ctrl.reload();

        stage.setTitle("Währungen");
        stage.setScene(new Scene(scene, 464, 279));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onEditButtonPressed - open Currency Window");

        Stage stage = new Stage();
        Parent scene = (Parent) loader.load("/gui/editcurrency.fxml", stage);
        EditCurrencyController ctrl = loader.getController();
        ctrl.setCurrency(currencyTable.getSelectionModel().getSelectedItem());
        ctrl.reload();

        stage.setTitle("Währungen");
        stage.setScene(new Scene(scene, 464, 279));
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        Currency selectedCurrency = (currencyTable.getSelectionModel().getSelectedItem());

        if (selectedCurrency != null) {
            log.debug("open Confirm-Delete-Currency Dialog");
            org.controlsfx.control.action.Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie die Währung '" + selectedCurrency.getName() + "' wirklich endgültig löschen?")
                    .actions(Dialog.Actions.NO, Dialog.Actions.YES)
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                currencyService.remove(selectedCurrency);
                saveCancelService.save();
                currencyTable.getItems().remove(selectedCurrency);
            }
        }
        checkFocus();
    }

    @FXML
    private void checkFocus() {
        Currency selectedCurrency = currencyTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
        if (selectedCurrency == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
    }

    @FXML
    private void onClosePressed() {
        Stage stage = (Stage) currencyTable.getScene().getWindow();
        stage.close();
    }


    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
