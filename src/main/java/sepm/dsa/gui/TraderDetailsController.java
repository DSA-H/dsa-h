package sepm.dsa.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.CurrencyAmount;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;
import sepm.dsa.util.CurrencyFormatUtil;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TraderDetailsController extends BaseControllerImpl {

    private static final Logger log = LoggerFactory.getLogger(TraderDetailsController.class);
    private SpringFxmlLoader loader;
    private TraderService traderService;
    private RollDiceService rollDiceService;

    private Trader trader;
    private Offer selectedOffer;
    private TimeService timeService;
    private DealService dealService;
    private CurrencySetService currencySetService;
    private SaveCancelService saveCancelService;

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
    private Button tradeButtonSell;
    @FXML
    private Button changePriceButton;
    @FXML
    private TextField difficultyField;
    @FXML
    private Label resultLabel;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button removeButton;
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

    private CurrencySet defaultCurrencySet;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        super.initialize(location, resources);

        log.debug("initialize TraderDetailsController");

        amountColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Offer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Offer, String> r) {
                if (r.getValue() != null) {
                    Offer offer = r.getValue();
                    Unit unit = offer.getProduct().getUnit();
                    Double amount = offer.getAmount();

                    DecimalFormat format = new DecimalFormat("#0.##");

                    StringBuilder sb = new StringBuilder();
                    sb.append(format.format(amount)).append(" ").append(unit.getShortName());
                    return new SimpleStringProperty(sb.toString());
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });

        productColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Offer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Offer, String> r) {
                if (r.getValue() != null) {
                    Offer offer = r.getValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append(offer.getProduct().getName());
                    if (offer.getProduct().getQuality()) {
                        sb.append(" (" + r.getValue().getQuality().getName() + ")");
                    }
                    return new SimpleStringProperty(sb.toString());
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        localPriceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Offer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Offer, String> r) {
                if (r.getValue() != null) {
                    Offer offer = r.getValue();
                    CurrencySet traderCurrencySet = trader.getLocation().getRegion().getPreferredCurrencySet();
                    if (traderCurrencySet == null) {
                        traderCurrencySet = defaultCurrencySet;
                    }
                    List<CurrencyAmount> ca = currencySetService.toCurrencySet(traderCurrencySet, offer.getPricePerUnit());
                    String str = CurrencyFormatUtil.currencySetShortString(ca);
                    return new SimpleStringProperty(str);
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        standardPriceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Offer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Offer, String> r) {
                if (r.getValue() != null) {
                    Offer offer = r.getValue();
                    List<CurrencyAmount> ca = currencySetService.toCurrencySet(defaultCurrencySet, offer.getPricePerUnit());
                    String str = CurrencyFormatUtil.currencySetShortString(ca);
                    return new SimpleStringProperty(str);
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });
        defaultCurrencySet = currencySetService.getDefaultCurrencySet();
        initialzeTableWithColums();
    }

    @Override
    public void reload() {
        log.debug("reload TraderDetailsController");
        refreshView();
        checkFocus();
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

        priceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Deal, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Deal, String> r) {
                if (r.getValue() != null) {
                    Deal deal = r.getValue();
                    List<CurrencyAmount> ca = currencySetService.toCurrencySet(defaultCurrencySet, deal.priceWithDiscount());
                    String str = CurrencyFormatUtil.currencySetShortString(ca);
                    return new SimpleStringProperty(str);
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });


        playerColumn.setCellValueFactory(d -> {
            Player player = d.getValue().getPlayer();
            String pName = player.getName();

            StringBuilder sb = new StringBuilder();
            sb.append(pName);
            return new SimpleStringProperty(sb.toString());
        });

        productDealColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Deal, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Deal, String> r) {
                if (r.getValue() != null) {
                    Deal deal = r.getValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append(deal.getProduct().getName());
                    if (deal.getProduct().getQuality()) {
                        sb.append(" (" + deal.getQuality().getName() + ")");
                    }
                    return new SimpleStringProperty(sb.toString());
                } else {
                    return new SimpleStringProperty("");
                }
            }
        });

        amountDealColumn.setCellValueFactory(d -> {
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

    @FXML
    private void onBackPressed() {
        log.debug("called onBackPressed");

        Stage stage = (Stage) offerTable.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void onEditPressed() {
        log.debug("called onEditPressed");
        Stage stage = (Stage) offerTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/edittrader.fxml", stage);
        EditTraderController controller = loader.getController();
        controller.setTrader(trader);
        controller.setLocation(trader.getLocation());
        controller.setPosition(new Point2D(trader.getxPos(), trader.getyPos()));
        controller.reload();
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
        String result = rollDiceService.talentThrow(trader.getMut(),
                trader.getIntelligence(),
                trader.getCharisma(),
                trader.getConvince(),
                difficulty);
        resultLabel.setText(result);
        // color the result text
        try{
            if(Integer.parseInt(result) >= 0) {
                resultLabel.setTextFill(Color.GREEN);
            }else {
                resultLabel.setTextFill(Color.RED);
            }
        }catch (NumberFormatException ex) {
            if(result.equals("MEISTERHAFT")) {
                resultLabel.setTextFill(Color.GREEN);
            }else {
                resultLabel.setTextFill(Color.RED);
            }
        }
    }

    @FXML
    private void onAddPressed() {
        log.debug("called onAddPressed");
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        Parent scene = (Parent) loader.load("/gui/addoffer.fxml", stage);
        AddOfferController controller = loader.getController();
        controller.setTrader(trader);
        controller.reload();

        stage.setScene(new Scene(scene, 600, 400));
        stage.show();
    }

    @FXML
    private void onDeletePressed() {
        log.debug("called onDeletePressed");
        Offer o = offerTable.getSelectionModel().getSelectedItem();
        if(o == null) {
            throw new DSAValidationException("Kein Angebot ausgewählt");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Wie viel ");
        sb.append(o.getProduct().getUnit().getName());
        sb.append(" ");
        sb.append(o.getProduct().getName());
        if(o.getProduct().getQuality()) {
            sb.append(" (").append(o.getQuality()).append(")");
        }
        sb.append(" wollen sie entfernen?");

        Optional<String> response = Dialogs.create()
                .title("Löschen?")
                .owner(null)
                .masthead(null)
                .message(sb.toString())
                .showTextInput("1");

        Double amount = 0d;
        if (response.isPresent()) {
            try {
                DecimalFormat df = new DecimalFormat();
                amount = df.parse(response.get()).doubleValue();
            } catch (ParseException nfe) {
                throw new DSAValidationException("Die zu entfernende Menge muss eine Zahl sein!");
            }
        } else {
            return;
        }
        if (amount <= 0) {
            throw new DSAValidationException("Die zu entfernende Menge muss eine positive Zahl sein!");
        }

        traderService.removeManualOffer(trader, o, amount);
        saveCancelService.save();
    }

    @FXML
    private void onDeleteDealClicked() {
        log.debug("called onDeleteDealClicked");
        Deal selectedDeal = dealsTable.getSelectionModel().getSelectedItem();
        if (selectedDeal == null) {
            throw new DSAValidationException("Bitte einen Deal zum Löschen auswählen");
        }
        dealService.remove(selectedDeal);
        saveCancelService.save();
        refreshView();
    }

    @FXML
    private void onTradePressed() {
        log.debug("called onTradePressed");
        //trader wants to sell stuff to the player
        Offer selectedItem = offerTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            Stage dialog = new Stage(StageStyle.DECORATED);
            dialog.initModality(Modality.APPLICATION_MODAL);

            Parent scene = (Parent) loader.load("/gui/tradeSell.fxml", dialog);
            TradeSellToPlayerController ctrl = loader.getController();
            ctrl.setOffer(selectedItem);
            ctrl.setTrader(trader);
            ctrl.reload();

            dialog.setTitle("Kauf von Waren");
            dialog.setScene(new Scene(scene, 334, 458));
            dialog.setResizable(false);
            dialog.show();
        } else {
            throw new DSAValidationException("Kein Angebot ausgewählt");
        }
    }

    @FXML
    private void onChangePricePressed() {
        log.debug("called onChangePricePressed");
        Offer o = offerTable.getSelectionModel().getSelectedItem();

        if (o != null) {
            // open modal window
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            Parent scene = (Parent) loader.load("/gui/changeOfferPrice.fxml", dialog);
            ChangeOfferPriceController ctrl = loader.getController();
            ctrl.setOffer(o);
            ctrl.reload();

            dialog.setTitle("Preis ändern");
            dialog.setScene(new Scene(scene, 307, 321));
            dialog.setResizable(false);
            dialog.show();
        } else {
            throw new DSAValidationException("Kein Angebot ausgewählt");
        }
    }

    @FXML
    private void onTradeBuyPressed() {
        log.debug("called onTradeBuyPressed");
        //Player wants to sell stuff to the trader
        Stage dialog = new Stage(StageStyle.DECORATED);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(dealsTable.getParent().getScene().getWindow());
        Parent scene = (Parent) loader.load("/gui/traderBuy.fxml", dialog);
        TradeBuyFromPlayerController ctrl = loader.getController();
        ctrl.setTrader(trader);
        ctrl.reload();

        dialog.setTitle("Verkauf von Waren an Händler");
        dialog.setScene(new Scene(scene, 565, 476));
        dialog.setResizable(false);

        dialog.show();
    }

    @FXML
    private void checkFocus() {
        Offer selectedOffer = offerTable.getSelectionModel().getSelectedItem();
        if (selectedOffer == null) {
            tradeButtonSell.setDisable(true);
            changePriceButton.setDisable(true);
            removeButton.setDisable(true);
        } else {
            tradeButtonSell.setDisable(false);
            changePriceButton.setDisable(false);
            removeButton.setDisable(false);
        }
    }

    private void refreshView() {
        if (trader != null && (trader = traderService.get(trader.getId())) != null) {
            saveCancelService.refresh(trader);
        } else {
            onBackPressed();
            return;
        }

        nameLabel.setText(trader.getName());
        categoryLabel.setText(trader.getCategory().getName());
        commentArea.setText(trader.getComment());

        List<Offer> offers = new ArrayList<>(trader.getOffers());
        offers = offers.stream().sorted((o1, o2) -> {
            int result = o1.getProduct().getId() - o2.getProduct().getId();
            if (result != 0) {
                return result;
            }
            result = o1.getQualityId() - o2.getQualityId();
            return result;
        }).collect(Collectors.toList());

        offerTable.getItems().setAll(offers);
        dealsTable.getItems().setAll(trader.getDeals());
    }

    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
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

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

    public void setCurrencySetService(CurrencySetService currencySetService) {
        this.currencySetService = currencySetService;
    }

    public void setRollDiceService(RollDiceService rollDiceService) {
        this.rollDiceService = rollDiceService;
    }
}
