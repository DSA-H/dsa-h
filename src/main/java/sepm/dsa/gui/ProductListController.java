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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Product;
import sepm.dsa.service.ProductService;

@Service("RegionListController")
public class ProductListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProductListController.class);

    private ProductService productService;

    @FXML
    private TableView<Product> tableview_product;
    @FXML
    private TableColumn tablecolumn_product;
    @FXML
    private TableColumn tablecolumn_cost;
    @FXML
    private TableColumn tablecolumn_unit;
    @FXML
    private TableColumn tablecolumn_productions;
    @FXML
    private TableColumn tablecolumn_categories;
    @FXML
    private TableColumn tablecolumn_attributes;
    @FXML
    private Button button_create;
    @FXML
    private Button button_edit;
    @FXML
    private Button button_remove;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise ProductListController");
        // init table

        tablecolumn_product.setCellValueFactory(new PropertyValueFactory<>("name"));
        tablecolumn_cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        tablecolumn_unit.setCellValueFactory(new PropertyValueFactory<>("unit"));


        /*borderColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Region, String>, ObservableValue<String>>() {
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
                    }
                };
            }
        });*/

        ObservableList<Product> data = FXCollections.observableArrayList(productService.getAll());
        tableview_product.setItems(data);

    }

    @FXML
    private void onCreateButtonPressed() {
        log.debug("onCreateButtonPressed - open Product-List Window");
        Stage stage = new Stage();
        Parent scene = null;
        EditProductController.setProduct(null);

        SpringFxmlLoader loader = new SpringFxmlLoader();
        scene = (Parent) loader.load("/gui/editproduct.fxml");

        stage.setTitle("Waren-Details");
        stage.setScene(new Scene(scene, 600, 438));
        stage.setResizable(false);
        stage.show();
    }
    /*
    @FXML
    private void onEditButtonPressed() {
        log.debug("onEditButtonPressed - open Gebiet-Details Window");
        Stage stage =  (Stage) regionTable.getScene().getWindow();

        Parent root = null;
        SpringFxmlLoader loader = new SpringFxmlLoader();

        Region selectedRegion = regionTable.getFocusModel().getFocusedItem();
        EditRegionController.setRegion(selectedRegion);

        root = (Parent) loader.load("/gui/editregion.fxml");

        stage.setTitle("Gebiet-Details");
        stage.setScene(new Scene(root, 600, 438));
        stage.show();
    }

    @FXML
    private void onDeleteButtonPressed() {
        log.debug("onDeleteButtonPressed - deleting selected Region");
        Region selectedRegion = regionTable.getFocusModel().getFocusedItem();

        if (selectedRegion != null) {
            log.debug("open Confirm-Delete-Region Dialog");
            Action response = Dialogs.create()
                    .title("Löschen?")
                    .masthead(null)
                    .message("Wollen Sie die Region '" + selectedRegion.getName() + "' und alle zugehörigen Grenzen wirklich löschen?")
                    .showConfirm();
            if(response == Dialog.Actions.YES) {
                regionService.remove(selectedRegion);
                regionTable.getItems().remove(selectedRegion);
            }
        }

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

    */
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}
