package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.DealService;
import sepm.dsa.service.TimeService;
import sepm.dsa.service.TraderService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TraderDetailsController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TraderDetailsController.class);
    private SpringFxmlLoader loader;
    private TraderService traderService;

    private Trader trader;
    private TimeService timeService;
    private DealService dealService;


    @FXML
    private TableView<Offer> offerTable;
    @FXML
    private TableColumn amountColumn;
    @FXML
    private TableColumn productColumn;
    @FXML
    private TableColumn localPriceColumn;
    @FXML
    private TableColumn standardPriceColumn;

    @FXML
    private Label nameLabel;
    @FXML
    private Label categoryLabel;

    @FXML
    private TextField difficultyField;
    @FXML
    private Label resultLabel;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button tradeButtonSell;
    @FXML
    private Button tradeButtonBuy;
    @FXML
    private TableView<Deal> dealsTable;
    @FXML
    private TableColumn<Deal, String> playerColumn;
    @FXML
    private TableColumn<Deal, String> productDealColumn;
    @FXML
    private TableColumn<Deal, String> priceColumn;
    @FXML
    private TableColumn<Deal, String> amountDealColumn;
    @FXML
    private TableColumn<Deal, String> dateColumn;


    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize TraderDetailsController");

        //init deals table
        initialzeTableWithColums();

        //init offer table
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        productColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Offer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Offer, String> r) {
                if (r.getValue() != null) {
                    Offer offer = r.getValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append(offer.getProduct().getName());
                    if (offer.getProduct().getQuality()) {
                        sb.append("(" + r.getValue().getQuality().getName() + ")");
                    }
                    return new SimpleStringProperty(sb.toString());
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        localPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        standardPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));

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
//        dateColumn.setSortType(TableColumn.SortType.DESCENDING);

        priceColumn.setCellValueFactory(new PropertyValueFactory<Deal, String>("price"));

        playerColumn.setCellValueFactory(d -> {
            Player player = d.getValue().getPlayer();
            String pName = player.getName();

            StringBuilder sb = new StringBuilder();
            sb.append(pName);
            return new SimpleStringProperty(sb.toString());
        });

        productDealColumn.setCellValueFactory(new PropertyValueFactory<Deal, String>("productName"));

        amountDealColumn.setCellValueFactory(d -> {
            Unit unit = d.getValue().getUnit();
            Integer amount = d.getValue().getAmount();

            StringBuilder sb = new StringBuilder();
            sb.append(amount).append(" ").append(unit.getShortName());
            return new SimpleStringProperty(sb.toString());
        });
    }

    @FXML
    private void onBackPressed() {
        log.debug("called onBackPressed");

        Stage stage = (Stage) offerTable.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onEditPressed() {
        log.debug("called onEditPressed");
        Stage stage = (Stage) offerTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/edittrader.fxml");
        EditTraderController controller = loader.getController();
        controller.setTrader(trader);
        stage.setScene(new Scene(scene, 509, 421));
    }

    @FXML
    private void onRolePressed() {
        log.debug("called onRolePressed");
        //TODO not part of version 1
    }

    @FXML
    private void onAddPressed() {
        log.debug("called onAddPressed");
        //TODO not part of version 1
    }

    @FXML
    private void onDeletePressed() {
        log.debug("called onDeletePressed");
        //TODO not part of version 1
    }

    @FXML
    private void onDeleteDealClicked() {
        log.debug("called onDeleteDealClicked");
        Deal selectedDeal = dealsTable.getSelectionModel().getSelectedItem();
        if(selectedDeal == null){
            throw new DSAValidationException("Bitte einen Deal zum Löschen auswählen");
        }
        dealService.remove(selectedDeal);
    }


    @FXML
    private void onTradePressed() {
        log.debug("called onTradePressed");
        //trader wants to sell stuff to the player
        //TODO as popover
        TradeSellToPlayerController.setTrader(trader);
        Offer selectedItem = offerTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TradeSellToPlayerController.setOffer(selectedItem);
            Stage dialog = new Stage(StageStyle.DECORATED);
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(dealsTable.getParent().getScene().getWindow());
            Parent scene = (Parent) loader.load("/gui/tradeSell.fxml");

            dialog.setTitle("Kauf von Waren");
            dialog.setScene(new Scene(scene, 330, 310));
            dialog.setResizable(false);
            dialog.showAndWait();
            checkFocus();
            refreshView();
        }else {
            throw new DSAValidationException("Kein Angebot ausgewählt");
        }
    }

    @FXML
    private void onTradeBuyPressed() {
        log.debug("called onTradeBuyPressed");
        //Player wants to sell stuff to the trader
        //TODO as popover
        TradeBuyFromPlayerController.setTrader(trader);
        Stage dialog = new Stage(StageStyle.DECORATED);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(dealsTable.getParent().getScene().getWindow());
        Parent scene = (Parent) loader.load("/gui/traderBuy.fxml");

        dialog.setTitle("Verkauf von Waren an Händler");
        dialog.setScene(new Scene(scene, 565, 355));
        dialog.setResizable(false);

        dialog.showAndWait();
        checkFocus();
        refreshView();
    }

    @FXML
    private void checkFocus() {
        Offer selectedOffer = offerTable.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            tradeButtonSell.setDisable(true);
        } else {
            tradeButtonSell.setDisable(false);
        }
    }

    private void refreshView() {
        nameLabel.setText(trader.getName());
        categoryLabel.setText(trader.getCategory().getName());
        commentArea.setText(trader.getComment());

        List<Offer> offers = new ArrayList<>(trader.getOffers());//traderService.getOffers(trader));
        offers = offers.stream().sorted((o1, o2) -> {
            int result = o1.getProduct().getId() - o2.getProduct().getId();
            if (result != 0) {
                return result;
            }
            result = o1.getQualityId() - o2.getQualityId();
            return result;
        }).collect(Collectors.toList());

        offerTable.getItems().clear();
        offerTable.setItems(FXCollections.observableArrayList(offers));
        dealsTable.setItems(FXCollections.observableArrayList(trader.getDeals()));
    }

    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
        refreshView(); // in setter not very beautiful, do we need this here?
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
}
