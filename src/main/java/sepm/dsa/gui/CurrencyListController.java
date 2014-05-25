package sepm.dsa.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Currency;
import sepm.dsa.service.CurrencyService;

public class CurrencyListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(CurrencyListController.class);
    SpringFxmlLoader loader;

    private CurrencyService currencyService;
    private SessionFactory sessionFactory;

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
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize CurrencyListController");
        // init table
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        valueToBaseRateColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
//        attributeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<Product, String> r) {
//                if (r.getValue() == null) {
//                    return new SimpleStringProperty("");
//                }
//                Product product = r.getValue();
//                return new SimpleStringProperty(product.getAttribute().getName());
//            }
//        });
//        productionRegionColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Product, String>, ObservableValue<String>>() {
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<Product, String> r) {
//                Session session = sessionFactory.openSession();
//                if (r.getValue() == null) {
//                    return new SimpleStringProperty("");
//                }
//                Product product = r.getValue();
//                session.refresh(product);
//                StringBuilder sb = new StringBuilder();
//                Set<Region> regions = product.getRegions();
//                for (Region region : regions) {
//                    sb.append(region.getName()).append(", ");
//                }
//                if (sb.length() >= 2) {
//                    sb.delete(sb.length() - 2, sb.length());
//                }
//                session.close();
//                return new SimpleStringProperty(sb.toString());
//            }
//        });


        ObservableList<Currency> data = FXCollections.observableArrayList(currencyService.getAll());
        currencyTable.setItems(data);

        checkFocus();
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateClicked - open Currency Window");

        EditProductController.setProduct(null);

        Stage stage = (Stage) currencyTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editcurrency.fxml");

        stage.setTitle("Waren");
        stage.setScene(new Scene(scene, 600, 414));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onWarenClicked - open Waren Window");

        EditCurrencyController.setCurrency(currencyTable.getFocusModel().getFocusedItem());

        Stage stage = (Stage) currencyTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/editcurrency.fxml");

        stage.setTitle("Währungen");
        stage.setScene(new Scene(scene, 600, 414));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        Currency selectedCurrency = (currencyTable.getSelectionModel().getSelectedItem());

        if (selectedCurrency != null) {
            log.debug("open Confirm-Delete-Product Dialog");
            org.controlsfx.control.action.Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie die Währung '" + selectedCurrency.getName() + "' wirklich endgültig löschen?")
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                currencyService.remove(selectedCurrency);
                currencyTable.getItems().remove(selectedCurrency);
            }
        }
        checkFocus();
    }

    @FXML
    private void checkFocus() {
        Currency selectedCurrency = currencyTable.getFocusModel().getFocusedItem();
        if (selectedCurrency == null) {
            deleteButton.setDisable(true);
            editButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            editButton.setDisable(false);
        }
    }


    public void setCurrencyService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
