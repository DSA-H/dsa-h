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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.TraderCategory;
import sepm.dsa.service.TraderCategoryService;

public class TraderCategoryListController implements Initializable {
    private TraderCategoryService traderCategoryService;
    private static final Logger log = LoggerFactory.getLogger(TraderCategoryListController.class);
    private SpringFxmlLoader loader;
    private SessionFactory sessionFactory;

    @FXML
    private TableView<TraderCategory> traderCategoryTable;
    @FXML
    private TableColumn traderCategoryColumn;
    @FXML
    private TableColumn prodcutCategoryColumn;
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
        prodcutCategoryColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TraderCategory, String>, ObservableValue<String>>() {
            @Override
            @Transactional(readOnly = true)
            public ObservableValue<String> call(TableColumn.CellDataFeatures<TraderCategory, String> r) {
                if (r.getValue() != null) {
                    Session session = sessionFactory.openSession();
                    StringBuilder sb = new StringBuilder();
                    TraderCategory tc = r.getValue();
                    session.refresh(tc);
                    for (AssortmentNature assortmentNature : tc.getAssortments()) {
                        String productCategoryName = assortmentNature.getProductCategory().getName();
                        sb.append(productCategoryName + ", ");
                    }
                    if (sb.length() >= 2) {
                        sb.delete(sb.length() - 2, sb.length());
                    }
                    session.close();
                    return new SimpleStringProperty(sb.toString());
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });


        ObservableList<TraderCategory> data = FXCollections.observableArrayList(traderCategoryService.getAll());
        traderCategoryTable.setItems(data);

        checkFocus();
    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateButtonPressed - open Trader-Category-Details Window");

        EditTraderCategoryController.setTraderCategory(null);

        Stage stage = (Stage) traderCategoryTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/edittradercategory.fxml");

        stage.setTitle("Händlerkategorie");
        stage.setScene(new Scene(root, 600, 438));
        stage.show();
    }

    @FXML
    private void onEditButtonPressed() {
        log.debug("onEditButtonPressed - open Gebiet-Details Window");

        TraderCategory selectedTraderCategory = traderCategoryTable.getFocusModel().getFocusedItem();
        EditTraderCategoryController.setTraderCategory(selectedTraderCategory);

        Stage stage = (Stage) traderCategoryTable.getScene().getWindow();
        Parent root = (Parent) loader.load("/gui/edittradercategory.fxml");

        stage.setTitle("Händler Kategorie bearbeiten");
        stage.setScene(new Scene(root, 600, 438));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        TraderCategory selectedTraderCategory = traderCategoryTable.getSelectionModel().getSelectedItem();

        if (selectedTraderCategory != null) {
            log.debug("open Confirm-Delete-Region Dialog");
            Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie die Händlerkategorie '" + selectedTraderCategory.getName() + "' wirklich löschen?")
                    .showConfirm();
            if (response == Dialog.Actions.YES) {
                traderCategoryService.remove(selectedTraderCategory);
                traderCategoryTable.getItems().remove(selectedTraderCategory);
            }
        }
        checkFocus();
    }

    @FXML
    private void checkFocus() {
        TraderCategory selectedTraderCategory = traderCategoryTable.getSelectionModel().getSelectedItem();
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

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
