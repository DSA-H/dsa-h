package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.dao.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;
import sepm.dsa.util.CurrencyFormatUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EditPlayerController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(EditPlayerController.class);
    private SpringFxmlLoader loader;

    private PlayerService playerService;
    private DealService dealService;
    private TimeService timeService;
    private CurrencySetService currencySetService;

    private SaveCancelService saveCancelService;

    private Player selectedPlayer;
    private boolean isNewPlaper;
    private CurrencySet defaultCurrencySet;

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
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        //initialize table
        initialzeTableWithColums();
        defaultCurrencySet = currencySetService.getDefaultCurrencySet();
    }

    @Override
    public void reload() {
        log.debug("reload EditPlayerController");
    }

    @FXML
    private void onCancelPressed() {
        log.debug("CancelButtonPressed");

        saveCancelService.save();
        Stage stage = (Stage) nameField.getScene().getWindow();

        Parent scene = (Parent) loader.load("/gui/playerlist.fxml", stage);
        PlayerListController ctrl = loader.getController();
        ctrl.reload();

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
        Parent scene = (Parent) loader.load("/gui/playerlist.fxml", stage);
        PlayerListController ctrl = loader.getController();
        ctrl.reload();
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

//        priceColumn.setCellValueFactory(new PropertyValueFactory<Deal, String>("price"));
        priceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Deal, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Deal, String> r) {
                if (r.getValue() != null) {
                    Deal deal = r.getValue();
                    List<CurrencyAmount> ca = currencySetService.toCurrencySet(defaultCurrencySet, deal.priceWithDiscount());
                    String str = CurrencyFormatUtil.currencySetShortString(ca, ", ");
                    return new SimpleStringProperty(str);
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });

        productColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Deal, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Deal, String> d) {
                if (d.getValue() != null) {
                    Deal deal = d.getValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append(deal.getProductName());
                    if (deal.getQuality() != null) {
                        sb.append(" (" + d.getValue().getQuality().getName() + ")");
                    }
                    return new SimpleStringProperty(sb.toString());
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });

        amountColumn.setCellValueFactory(d -> {
            Unit unit = d.getValue().getUnit();
            Integer amount = d.getValue().getAmount();
            boolean purchase = d.getValue().isPurchase();

            StringBuilder sb = new StringBuilder();
            if (purchase) {
                sb.append("(+) ");
            } else {
                sb.append("(-) ");
            }

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

    public void setPlayer(Player player) {
        selectedPlayer = player;
        if(selectedPlayer != null) {
            isNewPlaper = false;
            nameField.setText(selectedPlayer.getName());
            commentField.setText(selectedPlayer.getComment());
            if (selectedPlayer.getDeals().size() > 0) {
                dealsTable.getItems().setAll(selectedPlayer.getDeals());
            }
        }else {
            isNewPlaper = true;
            selectedPlayer = new Player();
        }
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

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }
}
