package sepm.dsa.gui;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditTraderController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EditTraderController.class);
    private SpringFxmlLoader loader;

    private Location oldLocation;
    private Trader selectedTrader;
    private TraderService traderService;
    private TraderCategoryService categoryService;
    private LocationService locationService;
    private SaveCancelService saveCancelService;
	private TimeService timeService;

    private boolean isNewTrader;
	int currentType;
	int initialType;
	private static final int MOVINGTRADER = 0;
	private static final int TRADER = 1;

	private Point2D position;
	private DSADate lastmoved;

    //TODO fill with better names
    ArrayList<String> firstNames = new ArrayList<String>(
            Arrays.asList("Max", "Paul", "Tom"));
    ArrayList<String> lastNames = new ArrayList<String>(
            Arrays.asList("Huber", "Kurz", "Lang"));

    @FXML
    private TextField nameField;
    @FXML
    private TextField sizeField;
    @FXML
    private TextField muField;
    @FXML
    private TextField inField;
    @FXML
    private TextField chField;
    @FXML
    private TextField convinceField;
    @FXML
    private ChoiceBox locationBox;
    @FXML
    private ChoiceBox categoryBox;
    @FXML
    private TextArea commentArea;

	@FXML
	private Label stayTimeLabel;
	@FXML
	private Label citySizeLabel;
	@FXML
	private Label areaLabel;
	@FXML
	private Label daysLabel;
	@FXML
	private TextField stayTimeField;
	@FXML
	private ChoiceBox<DistancePreferrence> areaBox;
	@FXML
	private ChoiceBox<TownSize> citySizeBox;
	@FXML
	private CheckBox movingCheck;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        log.debug("initialise EditTraderController");

        //init choiceBoxes
        List<TraderCategory> categories = categoryService.getAll();
        List<Location> locations = locationService.getAll();
	    categoryBox.setItems(FXCollections.observableArrayList(categories));
        locationBox.setItems(FXCollections.observableArrayList(locations));
	    areaBox.setItems(FXCollections.observableArrayList(DistancePreferrence.values()));
	    citySizeBox.setItems(FXCollections.observableArrayList(TownSize.values()));

	    movingCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
		    @Override
		    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			    if (newValue) {
				    stayTimeField.setDisable(false);
				    stayTimeLabel.setDisable(false);
				    citySizeBox.setDisable(false);
				    citySizeLabel.setDisable(false);
				    daysLabel.setDisable(false);
				    areaBox.setDisable(false);
				    areaLabel.setDisable(false);
				    currentType = MOVINGTRADER;
			    } else {
				    stayTimeField.setDisable(true);
				    stayTimeLabel.setDisable(true);
				    citySizeBox.setDisable(true);
				    citySizeLabel.setDisable(true);
				    daysLabel.setDisable(true);
				    areaBox.setDisable(true);
				    areaLabel.setDisable(true);
				    currentType = TRADER;
			    }
		    }
	    });
    }

    private void setUp() {
        log.debug("calling setUp");

        if (isNewTrader) {
            generateRandoms();
        } else {
            nameField.setText(selectedTrader.getName());
            sizeField.setText("" + selectedTrader.getSize());
            categoryBox.getSelectionModel().select(selectedTrader.getCategory());
            locationBox.getSelectionModel().select(selectedTrader.getLocation());
            oldLocation = selectedTrader.getLocation();
            muField.setText("" + selectedTrader.getMut());
            inField.setText("" + selectedTrader.getIntelligence());
            chField.setText("" + selectedTrader.getCharisma());
            convinceField.setText("" + selectedTrader.getConvince());
            commentArea.setText(selectedTrader.getComment());
	        movingCheck.setSelected(false);

	        if (currentType == MOVINGTRADER) {
		        stayTimeField.setText("" + ((MovingTrader) selectedTrader).getAvgStayDays());
		        citySizeBox.getSelectionModel().select(((MovingTrader) selectedTrader).getPreferredTownSize());
				areaBox.getSelectionModel().select(((MovingTrader) selectedTrader).getPreferredDistance());
		        movingCheck.setSelected(true);
	        }
        }
    }

    private void generateRandoms() {
        double rand;
        double rand2;
        int result;

        String name = "";
        rand = Math.random() * firstNames.size();
        name += firstNames.get((int) rand);
        rand = Math.random() * lastNames.size();
        name += " " + lastNames.get((int) rand);
        nameField.setText(name);

        rand = Math.random();
        rand *= rand;
        rand *= 5;
        rand2 = Math.random();
        if (rand2 < 0.5) {
            result = (int) (15 + rand);
        } else {
            result = (int) (15 - rand);
        }
        muField.setText("" + result);

        rand = Math.random();
        rand *= rand;
        rand *= 5;
        rand2 = Math.random();
        if (rand2 < 0.5) {
            result = (int) (15 + rand);
        } else {
            result = (int) (15 - rand);
        }
        inField.setText("" + result);

        rand = Math.random();
        rand *= rand;
        rand *= 5;
        rand2 = Math.random();
        if (rand2 < 0.5) {
            result = (int) (15 + rand);
        } else {
            result = (int) (15 - rand);
        }
        chField.setText("" + result);

        rand = Math.random();
        rand *= rand;
        rand *= 6;
        rand2 = Math.random();
        if (rand2 < 0.5) {
            result = (int) (6 + rand);
        } else {
            result = (int) (6 - rand);
        }
        convinceField.setText("" + result);

    }

    @FXML
    private void onSavePressed() {
        log.debug("called onSavePressed");

	    if (initialType != currentType) {
		    if (currentType == MOVINGTRADER) {
			    if (!isNewTrader) {
				    traderService.remove(selectedTrader);
			    }
			    selectedTrader = new MovingTrader();
		    } else {
			    if (!isNewTrader) {
				    traderService.remove(selectedTrader);
			    }
				selectedTrader = new Trader();
		    }
	    }

        //name
        int count = StringUtils.countOccurrencesOf(nameField.getText(), " ");
        if (count != nameField.getText().length()) {
            selectedTrader.setName(nameField.getText());
        } else {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Der Name des Händlers darf nicht nur aus Leerzeichen bestehen!")
                    .showWarning();
            return;
        }

        //size
        try {
            selectedTrader.setSize(Integer.parseInt(sizeField.getText()));
        } catch (NumberFormatException e) {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Die Größe des Händler muss eine Zahl sein, die die Anzahl an seiner Waren darstellt!")
                    .showWarning();
            return;
        }

        //mut
        try {
            selectedTrader.setMut(Integer.parseInt(muField.getText()));
        } catch (NumberFormatException e) {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Der Mut-Wert des Händler muss eine Zahl sein!")
                    .showWarning();
            return;
        }

        //intelligenz
        try {
            selectedTrader.setIntelligence(Integer.parseInt(inField.getText()));
        } catch (NumberFormatException e) {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Der Intelligenz-Wert des Händler muss eine Zahl sein!")
                    .showWarning();
            return;
        }

        //charisma
        try {
            selectedTrader.setCharisma(Integer.parseInt(chField.getText()));
        } catch (NumberFormatException e) {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Der Charisma-Wert des Händler muss eine Zahl sein!")
                    .showWarning();
            return;
        }

        //convince
        try {
            selectedTrader.setConvince(Integer.parseInt(convinceField.getText()));
        } catch (NumberFormatException e) {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Der Überreden-Wert des Händler muss eine Zahl sein!")
                    .showWarning();
            return;
        }

        //location
        Location location = (Location) locationBox.getSelectionModel().getSelectedItem();
        if (!isNewTrader && location != selectedTrader.getLocation()) {
            selectedTrader.setxPos(0);
            selectedTrader.setyPos(0);
        }
        if (location != null) {
            selectedTrader.setLocation(location);
        } else {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Ein Ort muss ausgewählt werden!")
                    .showWarning();
            return;
        }

        //category
        TraderCategory category = (TraderCategory) categoryBox.getSelectionModel().getSelectedItem();
        if (category != null) {
            selectedTrader.setCategory(category);
        } else {
            Dialogs.create()
                    .title("Ungültige Eingabe")
                    .masthead(null)
                    .message("Eine Händlerkategorie muss ausgewählt werden!")
                    .showWarning();
            return;
        }

        //comment
        selectedTrader.setComment(commentArea.getText());

	    //position
	    selectedTrader.setxPos((int) position.getX());
	    selectedTrader.setyPos((int) position.getY());

	    if (selectedTrader instanceof MovingTrader) {
		    //avg. stayTime
		    try {
			    ((MovingTrader) selectedTrader).setAvgStayDays(Integer.parseInt(stayTimeField.getText()));
		    } catch (NumberFormatException e) {
			    Dialogs.create()
					    .title("Ungültige Eingabe")
					    .masthead(null)
					    .message("Die Durchschnittliche Verweilzeit des Händler muss eine Zahl sein!")
					    .showWarning();
			    return;
		    }

		    //pref. townSize
		    TownSize size = citySizeBox.getValue();
		    if (size != null) {
			    ((MovingTrader) selectedTrader).setPreferredTownSize(size);
		    } else {
			    Dialogs.create()
					    .title("Ungültige Eingabe")
					    .masthead(null)
					    .message("Eine bevorzugte Stadtgröße muss ausgewählt werden!")
					    .showWarning();
			    return;
		    }

		    //travel area
		    DistancePreferrence area = areaBox.getValue();
		    if (area != null) {
			    ((MovingTrader) selectedTrader).setPreferredDistance(area);
		    } else {
			    Dialogs.create()
					    .title("Ungültige Eingabe")
					    .masthead(null)
					    .message("Ein Reisegebiet muss ausgewählt werden!")
					    .showWarning();
			    return;
		    }

		    //lastmoved
		    DSADate date = timeService.getCurrentDate();
		    ((MovingTrader) selectedTrader).setLastMoved(date);

	    }

        if (isNewTrader) {
            traderService.add(selectedTrader);
        } else {
            if (currentType == TRADER && !oldLocation.equals(selectedTrader.getLocation())) {
                Action response = Dialogs.create()
                        .title("Sortiment neu berechnen?")
                        .masthead(null)
                        .message("Der Händler hat sich an einen neuen Ort bewegt, soll ein neues Sortiment berechnet werden?")
                        .showConfirm();

                if (response == Dialog.Actions.YES) {
                    selectedTrader = traderService.recalculateOffers(selectedTrader);
                } else if (response == Dialog.Actions.NO) {

                    List<Dialogs.CommandLink> links = Arrays.asList(
                            new Dialogs.CommandLink("Alle Preise neu berechnen!", "Hierdurch werden die Preise von allen im Sortiment enthaltenen Waren neu berechnet."),
                            new Dialogs.CommandLink("Nur bestimmte Preise erhöhen!", "Hierdurch werden nur die Preise von Produkten die teurer werden neu berechnet")
                    );

                    Action response2 = Dialogs.create()
                            .title("Preise neu berechnen?")
                            .masthead(null)
                            .message("Sollen alle Preise neu berechnet werden?")
                            .showCommandLinks(links.get(1), links);

                    if (response2 == links.get(0)) {
                        //Recalculate pricing
                        traderService.reCalculatePriceForOffer(/*selectedTrader.getOffers(), */selectedTrader);
                    } else {
                        //Recalculate pricing if new price is higher
                        traderService.reCalculatePriceForOfferIfNewPriceIsHigher(/*selectedTrader.getOffers(), */selectedTrader);

                    }
                } else {
                    return;
                }
            }

	        if (currentType == initialType) {
		        traderService.update(selectedTrader);
	        } else {
		        traderService.add(selectedTrader);
	        }
        }
        saveCancelService.save();

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();


    }

    @FXML
    private void onCancelPressed() {
        log.debug("called onCancelPressed");
        saveCancelService.cancel();
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    public void setTraderService(TraderService traderService) {
        log.debug("calling setTraderService(" + traderService + ")");
        this.traderService = traderService;
    }

    public void setTraderCategoryService(TraderCategoryService traderCategoryService) {
        log.debug("calling setTraderCategoryService(" + traderCategoryService + ")");
        this.categoryService = traderCategoryService;
    }

    public void setLocationService(LocationService locationService) {
        log.debug("calling setLocationService(" + locationService + ")");
        this.locationService = locationService;
    }

    public void setTrader(Trader trader) {
        log.debug("calling setTrader(" + trader + ")");
        selectedTrader = trader;
        if (trader == null) {
            isNewTrader = true;
            selectedTrader = new Trader();
	        initialType = TRADER;
	        currentType = TRADER;
        } else {
            isNewTrader = false;
	        if (trader instanceof MovingTrader) {
		        lastmoved = new DSADate(((MovingTrader) trader).getLastMoved());
		        initialType = MOVINGTRADER;
		        currentType = MOVINGTRADER;
	        } else {
		        initialType = TRADER;
		        currentType = TRADER;
	        }
        }
        setUp();
    }

    public void setLocation(Location location) {
        locationBox.getSelectionModel().select(location);
        locationBox.setDisable(false);
    }

    public void setPosition(Point2D pos) {
        position = pos;
    }

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

    public void setSaveCancelService(SaveCancelService saveCancelService) {
        this.saveCancelService = saveCancelService;
    }

	public void setTimeService(TimeService timeService) {
		this.timeService = timeService;
	}
}
