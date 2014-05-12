package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionListController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(RegionListController.class);

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
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
    }

    @FXML
    private void onCreateButtonPressed() {}

    @FXML
    private void onEditButtonPressed() {}

    @FXML
    private void onDeleteButtonPressed() {}
}
