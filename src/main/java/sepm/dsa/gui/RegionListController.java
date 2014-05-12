package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.service.BorderService;
import sepm.dsa.service.RegionService;

public class RegionListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(RegionListController.class);

    private RegionService regionService;
    private BorderService borderService;

    @FXML
    private TableView regionTable;
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
    public void initialize (java.net.URL location, java.util.ResourceBundle resources) {
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("border"));
      //  colorColumn.setCellFactory();
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setRegionBorderService(BorderService borderService) {
        this.borderService = borderService;
    }

    @FXML
    private void onCreateButtonPressed() {}

    @FXML
    private void onEditButtonPressed() {}

    @FXML
    private void onDeleteButtonPressed() {}
}
