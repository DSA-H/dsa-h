package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RegionListController implements Initializable {

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

    }

    @FXML
    private void onCreateButtonPressed() {}

    @FXML
    private void onEditButtonPressed() {}

    @FXML
    private void onDeleteButtonPressed() {}

    //FOO
}
