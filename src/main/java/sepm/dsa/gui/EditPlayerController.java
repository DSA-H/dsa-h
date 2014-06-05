package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.DSADate;
import sepm.dsa.model.Deal;
import sepm.dsa.model.Player;
import sepm.dsa.model.Unit;
import sepm.dsa.service.DealService;
import sepm.dsa.service.PlayerService;
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.service.TimeService;

public class EditPlayerController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditPlayerController.class);
    private SpringFxmlLoader loader;

    private PlayerService playerService;
    private DealService dealService;
    private TimeService timeService;

    private SaveCancelService saveCancelService;

    private static Player selectedPlayer;
    private boolean isNewPlaper;

    @FXML
    private TextField nameField;
    @FXML
    private TextField valueToBaseRateField;
    @FXML
    private TextArea commentField;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private TableView<Deal> dealsTable;
    @FXML
    private TableColumn<Deal, String> dateColumn;
    @FXML
    private TableColumn<Deal, String> priceColumn;
    @FXML
    private TableColumn<Deal, String> productColumn;
    @FXML
    private TableColumn<Deal, String> amountColumn;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize EditPlayerController");
        //initialize table
        initialzeTableWithColums();

        if (selectedPlayer != null) {
            isNewPlaper = false;
            nameField.setText(selectedPlayer.getName());
            commentField.setText(selectedPlayer.getComment());
            if (selectedPlayer.getDeals().size() > 0) {
                dealsTable.setItems(FXCollections.observableArrayList(selectedPlayer.getDeals()));
            }
        } else {
            isNewPlaper = true;
            selectedPlayer = new Player();
        }
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");

        saveCancelService.save();
        Stage stage = (Stage) nameField.getScene().getWindow();

        Parent scene = (Parent) loader.load("/gui/playerlist.fxml");

        stage.setScene(new Scene(scene, 850, 438));
    }

    @FXML
    private void onSavePressed() {
        log.debug("SaveButtonPressed");

        if (nameField.getText().isEmpty()) {
            throw new DSAValidationException("Bitte Namen eingeben");
        }
        String name = nameField.getText();

        selectedPlayer.setName(name);
        selectedPlayer.setComment(commentField.getText());

        if (isNewPlaper) {
            playerService.add(selectedPlayer);
        } else {
            playerService.update(selectedPlayer);
        }
        saveCancelService.save();

        // return to players-list
        Stage stage = (Stage) nameField.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/playerlist.fxml");
        stage.setScene(new Scene(scene, 850, 438));
    }

    private void initialzeTableWithColums() {

        dateColumn.setCellValueFactory(d -> {
            DSADate date = d.getValue().getDate();
            long timestamp = d.getValue().getDate().getTimestamp();

            long current = timeService.getCurrentDate().getTimestamp();

            StringBuilder sb = new StringBuilder();
            sb.append("vor ").append(current - timestamp).append(" Tagen").append(" (").append(date).append(")");
            return new SimpleStringProperty(sb.toString());
        });

        priceColumn.setCellValueFactory(new PropertyValueFactory<Deal, String>("price"));
        productColumn.setCellValueFactory(new PropertyValueFactory<Deal, String>("productName"));

        amountColumn.setCellValueFactory(d -> {
            Unit unit = d.getValue().getUnit();
            Integer amount = d.getValue().getAmount();

            StringBuilder sb = new StringBuilder();
            sb.append(amount).append(" ").append(unit.getShortName());
            return new SimpleStringProperty(sb.toString());
        });
    }


    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public static void setPlayer(Player player) {
        selectedPlayer = player;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
}
