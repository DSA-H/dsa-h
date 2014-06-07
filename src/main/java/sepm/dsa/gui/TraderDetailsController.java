package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.*;
import sepm.dsa.service.TimeService;
import sepm.dsa.service.TraderService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TraderDetailsController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TraderDetailsController.class);
    private SpringFxmlLoader loader;
    private TraderService traderService;

    private Trader trader;
    private Offer selectedOffer;
    private TimeService timeService;


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

        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        initialzeTableWithColums();



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
	    controller.setLocation(trader.getLocation());
	    controller.setPosition(new Point2D(trader.getxPos(), trader.getyPos()));
		stage.setScene(new Scene(scene, 785, 513));
    }

    @FXML
    private void onRolePressed() {
        log.debug("called onRolePressed");
	    int difficulty = 0;
	    try {
		    difficulty = Integer.parseInt(difficultyField.getText());
	    } catch (NumberFormatException e) {
		    Dialogs.create()
				    .title("Ungültige Eingabe")
				    .masthead(null)
				    .message("Die Erschwernis muss eine Zahl sein!")
				    .showWarning();
		    return;
	    }
	    int dice1 = (int) (Math.random()*20)+1;
	    int dice2 = (int) (Math.random()*20)+1;
	    int dice3 = (int) (Math.random()*20)+1;
	    int result = trader.getConvince()-difficulty;
	    if (result < 0) {
		    difficulty = result;
		    result = 0;
	    } else {
		    difficulty = 0;
	    }
	    if (dice1 > (trader.getMut()+difficulty)) {
		    result -= (dice1-(trader.getMut()+difficulty));
	    }
	    if (dice2 > (trader.getIntelligence()+difficulty)) {
		    result -= (dice2-(trader.getIntelligence()+difficulty));
	    }
	    if (dice3 > (trader.getCharisma()+difficulty)) {
		    result -= (dice3-(trader.getCharisma()+difficulty));
	    }
	    if (dice1 == 20 && dice2 == 20 || dice2 == 20 && dice3 == 20 || dice1 == 20 && dice3 == 20) {
		    resultLabel.setText("PATZER");
		    resultLabel.setTextFill(Color.RED);
	    } else if (dice1 == 1 && dice2 == 1 || dice2 == 1 && dice3 == 1 || dice1 == 1 && dice3 == 1) {
		    resultLabel.setText("MEISTERHAFT");
		    resultLabel.setTextFill(Color.GREEN);
	    } else if (result >= 0) {
		    resultLabel.setText(""+result);
		    resultLabel.setTextFill(Color.GREEN);
	    } else if (result < 0) {
		    resultLabel.setText(""+result);
		    resultLabel.setTextFill(Color.RED);
	    }
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
    private void onTradePressed() {
        log.debug("called onTradePressed");
        //TODO not part of version 1
    }

    @FXML
    private void onTradeBuyPressed() {
        log.debug("called onTradeBuyPressed");
        //Player wants to sell stuff to the trader
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

    public void setTrader(Trader trader) {
        this.trader = trader;

        nameLabel.setText(trader.getName());
        categoryLabel.setText(trader.getCategory().getName());
        commentArea.setText(trader.getComment());

        List<Offer> offers = new ArrayList<>(traderService.getOffers(trader));
        offers = offers.stream().sorted((o1, o2) -> {
            int result = o1.getProduct().getId() - o2.getProduct().getId();
            if (result != 0) {
                return result;
            }
            result = o1.getQualityId() - o2.getQualityId();
            return result;
        }).collect(Collectors.toList());

        offerTable.setItems(FXCollections.observableArrayList(offers));
	    dealsTable.setItems(FXCollections.observableArrayList(trader.getDeals()));
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
