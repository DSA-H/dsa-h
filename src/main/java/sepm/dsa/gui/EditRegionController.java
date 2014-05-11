package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditRegionController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditRegionController.class);

    @FXML
    private TextField name;
    @FXML
    private ChoiceBox temperature;
    @FXML
    private ColorPicker color;
    @FXML
    private ChoiceBox rainfall;
    @FXML
    private ChoiceBox border;
    @FXML
    private TextField borderCost;
    @FXML
    private TextArea comment;
    @FXML
    private TableView borderTable;
    @FXML
    private TableColumn borderColumn;
    @FXML
    private TableColumn borderCostColumn;

    @Override
    public void initialize (java.net.URL location, java.util.ResourceBundle resources) {
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("border"));
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("borderCost"));
        //FOO
    }
}
