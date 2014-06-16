package sepm.dsa.gui;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;
import sepm.dsa.service.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class EditTraderController extends BaseControllerImpl {

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
    private ChoiceBox<Location> locationBox;
    @FXML
    private ChoiceBox<TraderCategory> categoryBox;
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
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        areaBox.getItems().setAll(DistancePreferrence.values());
        citySizeBox.getItems().setAll(TownSize.values());

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

    @Override
    public void reload() {
        log.debug("reload EditTraderController");

        if(!isNewTrader) {
            if (traderService.get(selectedTrader.getId()) == null) {
                onCancelPressed();
                return;
            }
            if(traderService.get(selectedTrader.getId()) instanceof MovingTrader) {
                if(currentType != MOVINGTRADER) {
                    onCancelPressed();
                    return;
                }
            }else {
                if(currentType != TRADER) {
                    onCancelPressed();
                    return;
                }
            }
            categoryBox.setDisable(true);
        }

        //init choiceBoxes
        TraderCategory category = categoryBox.getValue();
	    categoryBox.getItems().setAll(categoryService.getAll());
        if(categoryBox.getItems().contains(category)) {
            categoryBox.setValue(category);
        }else {
            categoryBox.setValue(null);
            categoryBox.getSelectionModel().clearSelection();
        }
        Location location = locationBox.getValue();
       	locationBox.getItems().setAll(locationService.getAll());
        if(locationBox.getItems().contains(location)) {
            locationBox.setValue(location);
        }else {
            locationBox.setValue(null);
            locationBox.getSelectionModel().clearSelection();
        }
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

    //TODO: move to service laer. This calculations should not be in GUI layer!
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
        String name;
        int count = StringUtils.countOccurrencesOf(nameField.getText(), " ");
        if (count != nameField.getText().length()) {
            name = nameField.getText();
        } else {
            throw new DSAValidationException("Der Name des Händlers darf nicht nur aus Leerzeichen bestehen!");
        }

        //size
        Integer size;
        try {
            size = Integer.parseInt(sizeField.getText());
        } catch (NumberFormatException e) {
            throw new DSAValidationException("Die Größe des Händler muss eine Zahl sein, die die Anzahl an seiner Waren darstellt!");
        }

        //mut
        Integer mut;
        try {
            mut = Integer.parseInt(muField.getText());
        } catch (NumberFormatException e) {
            throw new DSAValidationException("Der Mut-Wert des Händler muss eine Zahl sein!");
        }

        //intelligenz
        Integer in;
        try {
            in = Integer.parseInt(inField.getText());
        } catch (NumberFormatException e) {
            throw new DSAValidationException("Der Intelligenz-Wert des Händler muss eine Zahl sein!");
        }

        //charisma
        Integer ch;
        try {
            ch = Integer.parseInt(chField.getText());
        } catch (NumberFormatException e) {
            throw new DSAValidationException("Der Charisma-Wert des Händler muss eine Zahl sein!");
        }

        //convince
        Integer convince;
        try {
            convince = Integer.parseInt(convinceField.getText());
        } catch (NumberFormatException e) {
            throw new DSAValidationException("Der Überreden-Wert des Händler muss eine Zahl sein!");
        }

        //location
        Location location = (Location) locationBox.getSelectionModel().getSelectedItem();
        if (!isNewTrader && location != selectedTrader.getLocation()) {
            selectedTrader.setxPos(0);
            selectedTrader.setyPos(0);
        }
        if (location == null) {
            throw new DSAValidationException("Ein Ort muss ausgewählt werden!");
        }

        //category
        TraderCategory category = (TraderCategory) categoryBox.getSelectionModel().getSelectedItem();
        if (category == null) {
            throw new DSAValidationException("Eine Händlerkategorie muss ausgewählt werden!");
        }

        Integer avgStayDays = null;
        TownSize townsize = null;
        DistancePreferrence area = null;
	    if (selectedTrader instanceof MovingTrader) {
		    //avg. stayTime
		    try {
                avgStayDays = Integer.parseInt(stayTimeField.getText());
		    } catch (NumberFormatException e) {
                throw new DSAValidationException("Die Durchschnittliche Verweilzeit des Händler muss eine Zahl sein!");
		    }

		    //pref. townSize
            townsize = citySizeBox.getValue();
		    if (townsize == null) {
                throw new DSAValidationException("Eine bevorzugte Stadtgröße muss ausgewählt werden!");
		    }

            area = areaBox.getValue();
		    //travel area
		    if (area == null) {
                throw new DSAValidationException("Ein Reisegebiet muss ausgewählt werden!");
		    }
	    }

        if(!isNewTrader) {

        }

        if (isNewTrader) {
            selectedTrader.setName(name);
            selectedTrader.setSize(size);
            selectedTrader.setMut(mut);
            selectedTrader.setIntelligence(in);
            selectedTrader.setCharisma(ch);
            selectedTrader.setConvince(convince);
            selectedTrader.setLocation(location);
            selectedTrader.setCategory(category);
            selectedTrader.setComment(commentArea.getText());
            selectedTrader.setxPos((int) position.getX());
            selectedTrader.setyPos((int) position.getY());
            if(selectedTrader instanceof MovingTrader) {
                ((MovingTrader) selectedTrader).setAvgStayDays(avgStayDays);
                ((MovingTrader) selectedTrader).setPreferredTownSize(townsize);
                ((MovingTrader) selectedTrader).setPreferredDistance(area);
                //lastmoved
                DSADate date = timeService.getCurrentDate();
                ((MovingTrader) selectedTrader).setLastMoved(date);
            }
            traderService.add(selectedTrader);
        } else {
            if (!oldLocation.equals(location)) {
                Action response = Dialogs.create()
                        .title("Sortiment neu berechnen?")
                        .masthead(null)
                        .message("Der Händler hat sich an einen neuen Ort bewegt, soll ein neues Sortiment berechnet werden?")
                        .showConfirm();

                if (response == Dialog.Actions.YES) {
                    selectedTrader.setName(name);
                    selectedTrader.setSize(size);
                    selectedTrader.setMut(mut);
                    selectedTrader.setIntelligence(in);
                    selectedTrader.setCharisma(ch);
                    selectedTrader.setConvince(convince);
                    selectedTrader.setLocation(location);
                    selectedTrader.setCategory(category);
                    selectedTrader.setComment(commentArea.getText());
                    selectedTrader.setxPos((int) position.getX());
                    selectedTrader.setyPos((int) position.getY());
                    if(selectedTrader instanceof MovingTrader) {
                        ((MovingTrader) selectedTrader).setAvgStayDays(avgStayDays);
                        ((MovingTrader) selectedTrader).setPreferredTownSize(townsize);
                        ((MovingTrader) selectedTrader).setPreferredDistance(area);
                        //lastmoved
                        DSADate date = timeService.getCurrentDate();
                        ((MovingTrader) selectedTrader).setLastMoved(date);
                    }
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
                        selectedTrader.setName(name);
                        selectedTrader.setSize(size);
                        selectedTrader.setMut(mut);
                        selectedTrader.setIntelligence(in);
                        selectedTrader.setCharisma(ch);
                        selectedTrader.setConvince(convince);
                        selectedTrader.setLocation(location);
                        selectedTrader.setCategory(category);
                        selectedTrader.setComment(commentArea.getText());
                        selectedTrader.setxPos((int) position.getX());
                        selectedTrader.setyPos((int) position.getY());
                        if(selectedTrader instanceof MovingTrader) {
                            ((MovingTrader) selectedTrader).setAvgStayDays(avgStayDays);
                            ((MovingTrader) selectedTrader).setPreferredTownSize(townsize);
                            ((MovingTrader) selectedTrader).setPreferredDistance(area);
                            //lastmoved
                            DSADate date = timeService.getCurrentDate();
                            ((MovingTrader) selectedTrader).setLastMoved(date);
                        }
                        //Recalculate pricing
                        traderService.reCalculatePriceForOffer(/*selectedTrader.getOffers(), */selectedTrader);
                    } else if(response2 == links.get(1)) {
                        selectedTrader.setName(name);
                        selectedTrader.setSize(size);
                        selectedTrader.setMut(mut);
                        selectedTrader.setIntelligence(in);
                        selectedTrader.setCharisma(ch);
                        selectedTrader.setConvince(convince);
                        selectedTrader.setLocation(location);
                        selectedTrader.setCategory(category);
                        selectedTrader.setComment(commentArea.getText());
                        selectedTrader.setxPos((int) position.getX());
                        selectedTrader.setyPos((int) position.getY());
                        if(selectedTrader instanceof MovingTrader) {
                            ((MovingTrader) selectedTrader).setAvgStayDays(avgStayDays);
                            ((MovingTrader) selectedTrader).setPreferredTownSize(townsize);
                            ((MovingTrader) selectedTrader).setPreferredDistance(area);
                            //lastmoved
                            DSADate date = timeService.getCurrentDate();
                            ((MovingTrader) selectedTrader).setLastMoved(date);
                        }
                        //Recalculate pricing if new price is higher
                        traderService.reCalculatePriceForOfferIfNewPriceIsHigher(/*selectedTrader.getOffers(), */selectedTrader);
                    }else {
                        return;
                    }
                } else {
                    return;
                }
            }

            selectedTrader.setName(name);
            selectedTrader.setSize(size);
            selectedTrader.setMut(mut);
            selectedTrader.setIntelligence(in);
            selectedTrader.setCharisma(ch);
            selectedTrader.setConvince(convince);
            selectedTrader.setLocation(location);
            selectedTrader.setCategory(category);
            selectedTrader.setComment(commentArea.getText());
            selectedTrader.setxPos((int) position.getX());
            selectedTrader.setyPos((int) position.getY());
            if(selectedTrader instanceof MovingTrader) {
                ((MovingTrader) selectedTrader).setAvgStayDays(avgStayDays);
                ((MovingTrader) selectedTrader).setPreferredTownSize(townsize);
                ((MovingTrader) selectedTrader).setPreferredDistance(area);
                //lastmoved
                DSADate date = timeService.getCurrentDate();
                ((MovingTrader) selectedTrader).setLastMoved(date);
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

        if (selectedTrader == null) {
            isNewTrader = true;
            selectedTrader = new Trader();
            initialType = TRADER;
            currentType = TRADER;
        } else {
            isNewTrader = false;
            if (selectedTrader instanceof MovingTrader) {
                lastmoved = new DSADate(((MovingTrader) selectedTrader).getLastMoved());
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
