package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

public class EditRegionController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditRegionController.class);

    private RegionService regionService;
    private RegionBorderService borderService;

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
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setRegionBorderService(RegionBorderService borderService) {
        this.borderService = borderService;
    }

    @FXML
    private void onBorderCostColumnChanged() {}

    @FXML
    private void onCancelPressed() {}

    @FXML
    private void onSavePressed() {}

    @FXML
    private void onAddBorderPressed() {}

    @FXML
    private void onRemoveBorderPressed() {}
}
