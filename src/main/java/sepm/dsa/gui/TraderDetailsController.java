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
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.dao.OfferDao;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Trader;
import sepm.dsa.service.SaveCancelService;
import sepm.dsa.service.TraderService;
import sepm.dsa.service.TraderServiceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TraderDetailsController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TraderDetailsController.class);
    private SpringFxmlLoader loader;
	private TraderService traderService;

    private Trader trader;
    private Offer selectedOffer;
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
    private TextField difficultyField;
    @FXML
    private Label resultLabel;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button removeButton;


	@Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialize TraderDetailsController");

        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        productColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Offer, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Offer, String> r) {
                if (r.getValue() != null) {
                    Offer offer = r.getValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append(offer.getProduct().getName());
                    if(offer.getProduct().getQuality()) {
                        sb.append("(" + r.getValue().getQuality().getName() + ")");
                    }
                    return new SimpleStringProperty(sb.toString());
                }else {
                    return new SimpleStringProperty("");
                }
            }
        });
        localPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        standardPriceColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        checkFocus();
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
        stage.setScene(new Scene(scene, 600, 400));
    }

    @FXML
    private void onRolePressed() {
        log.debug("called onRolePressed");
        //TODO not part of version 1
    }

    @FXML
    private void checkFocus(){
        Offer o = offerTable.getSelectionModel().getSelectedItem();
        if (o!=null){
            removeButton.setDisable(false);
        }else{
            removeButton.setDisable(true);
        }
    }

    @FXML
    private void onAddPressed() {
        log.debug("called onAddPressed");
        Stage stage = (Stage) offerTable.getScene().getWindow();
        Parent scene = (Parent) loader.load("/gui/addoffer.fxml");
        AddOfferController controller = loader.getController();
        controller.setTrader(trader);
        stage.setScene(new Scene(scene, 600, 400));
    }

    @FXML
    private void onDeletePressed() {
        log.debug("called onDeletePressed");
        Offer o = offerTable.getSelectionModel().getSelectedItem();

        Optional<String> response = Dialogs.create()
                .title("Löschen?")
                .masthead(null)
                .message("Wie viel wollen sie entfernen?")
                .showTextInput();

        int amount = 0;
        if (response.isPresent()){
            try{
                amount = Integer.parseInt(response.get());
                if (amount < 0){
                    Dialogs.create()
                            .title("Ungültige Eingabe")
                            .masthead(null)
                            .message("Ungültige Eingabe")
                            .showError();
                    return;
                }
            }catch (NumberFormatException nfe){
                Dialogs.create()
                        .title("Ungültige Eingabe")
                        .masthead(null)
                        .message("Ungültige Eingabe")
                        .showError();
                return;
            }
        }else {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Ungültige Eingabe")
                    .showError();
            return;
        }

        boolean remove = false;
        if (amount>=o.getAmount()){
            remove = true;
        }

        traderService.removeManualOffer(trader, o, amount);

        if (remove){
            offerTable.getItems().remove(o);
        }else{
            offerTable.getItems().set(offerTable.getItems().indexOf(o),o);
        }

        checkFocus();
        saveCancelService.save();
    }

    @FXML
    private void onTradePressed() {
        log.debug("called onTradePressed");
        //TODO not part of version 1
    }

    public void setTrader(Trader trader) {
        this.trader = trader;

        nameLabel.setText(trader.getName());
        categoryLabel.setText(trader.getCategory().getName());
        commentArea.setText(trader.getComment());

        List<Offer> offers = new ArrayList<>(traderService.getOffers(trader));
        offers = offers.stream().sorted(new Comparator<Offer>() {
            @Override
            public int compare(Offer o1, Offer o2) {
                /*if (o2==null || o2.getProduct()==null){
                    return -1;
                }
                if (o1==null || o1.getProduct()==null){
                    return 1;
                }*/
                int result = o1.getProduct().getId() - o2.getProduct().getId();
                if(result != 0) {
                    return result;
                }
                result = o1.getQualityId() - o2.getQualityId();
                return result;
            }
        }).collect(Collectors.toList());

        offerTable.setItems(FXCollections.observableArrayList(offers));
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }
}
