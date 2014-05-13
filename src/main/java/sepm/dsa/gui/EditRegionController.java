package sepm.dsa.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sepm.dsa.service.RegionBorderService;
import sepm.dsa.service.RegionService;

@Service("EditRegionController")
public class EditRegionController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditRegionController.class);

    @Autowired
    private RegionService regionService;
    @Autowired
    private RegionBorderService regionBorderService;

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
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditRegionController");
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("border"));
        borderColumn.setCellValueFactory(new PropertyValueFactory<>("borderCost"));
    }

    public void setRegionService(RegionService regionService) {
        this.regionService = regionService;
    }

    public void setRegionBorderService(RegionBorderService regionBorderService) {
        this.regionBorderService = regionBorderService;
    }

    @FXML
    private void onBorderCostColumnChanged() {
    }

    @FXML
    private void onCancelPressed() {
    }

    @FXML
    private void onSavePressed() {
    }

    @FXML
    private void onAddBorderPressed() {
    }

    @FXML
    private void onRemoveBorderPressed() {
    }
}
