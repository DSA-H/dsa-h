package sepm.dsa.gui;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.*;
import sepm.dsa.service.*;
import sepm.dsa.service.path.NoPathException;
import sepm.dsa.service.pdf.TraderPdfService;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuController extends BaseControllerImpl {

	private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);
	private static final int WORLDMODE = 0;
	private static final int LOCATIONMODE = 1;

	private SpringFxmlLoader loader;
	private LocationService locationService;
	private TraderService traderService;
	private TavernService tavernService;
	private MapService mapService;
	private SaveCancelService saveCancelService;
	private LocationConnectionService locationConnectionService;
	private TraderPdfService traderPdfService;

	private Canvas mapCanvas, selectionCanvas, pathCanvas; // 3 canvases on top of each other in zoomGroup; bottom: mapCanvas, top: pathCanvas

	private Location selectedLocation; // selection in WORLDMODE, current Location in LOCATIONMODE
	private Object selectedObject; // selection in LOCATIONMODE (Trader or Tavern)

	private Location fromLocation, toLocation; // for calculating and showing shortest route

	private int mode;   // 0..WORLDMODE, 1..LOCATIONMODE

	private Boolean creationMode = false; // to distinguish between placing something (true), and selecting something (false) on the map
	private Boolean nothingChanged = false; // to tell if a new path should be calculated, or the existing made invisible

	private double hVal = 0.0; // to set horizontal value of scrollPane
	private double vVal = 0.0; // to set vertical value of scrollPane
	private double worldScrollH; // jump back to this when switching to WORLDMODE
	private double worldScrollV; // jump back to this when switching to WORLDMODE
	private Boolean dontUpdateScroll = false; // to stop the scrollListener to undo it's own changes

	private Group zoomGroup; // Group of canvases that can be scaled
	private double scaleFactor = 1.0; // zoom factor

	private List<LocationConnection> connections; // all LocationConnections

	// menu-bar
	@FXML
	private MenuBar menuBar;
	@FXML
	private Menu dateiMenu;
	@FXML
	private MenuItem dateiImport;
	@FXML
	private MenuItem dateiExport;
	@FXML
	private MenuItem dateiExit;
	@FXML
	private Menu dateiVerwaltenMenu;
	@FXML
	private MenuItem verwaltenHaendlerKategorie;
	@FXML
	private MenuItem verwaltenGebieteGrenzen;
	@FXML
	private MenuItem verwaltenWaehrungen;
	@FXML
	private MenuItem verwaltenWaren;
	@FXML
	private Menu verwaltenWeltkarte;
	@FXML
	private MenuItem weltkarteImportieren;
	@FXML
	private MenuItem weltkarteExportieren;

	// lists, tables, maps
	@FXML
	private TableView<Location> locationTable;
	@FXML
	private TableColumn locationColumn;
	@FXML
	private TableColumn regionColumn;
	@FXML
	private ListView traderList;
	@FXML
	private ScrollPane scrollPane;

	// controle buttons
	@FXML
	private Button createButton;
	@FXML
	private Button editButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button chooseButton;

	// path calculation
	@FXML
	private Button fromButton;
	@FXML
	private Button toButton;
	@FXML
	private Button calcButton;
	@FXML
	private Label fromLabel;
	@FXML
	private Label toLabel;
	@FXML
	private Label resultLabel;
	@FXML
	private GridPane pathCalcGrid;
	// zoom
	@FXML
	private Slider zoomSlider;

	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		super.initialize(location, resources);

		// init location-table
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
		locationTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Location>() {
            @Override
            public void changed(ObservableValue<? extends Location> observable, Location oldValue, Location newValue) {
                if (mode == WORLDMODE) checkLocationFocus();
            }
        });

		// init trader-list
		traderList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				if (mode == LOCATIONMODE) checkTraderFocus();
			}
		});

		mode = WORLDMODE;

		chooseButton.setStyle("-fx-font-weight: bold;");

        updateTables();
        updateMap();
        // init zoom
        zoomSlider.setMin((536) / mapCanvas.getHeight());
        zoomSlider.setMax(2.69);
        zoomSlider.adjustValue(1.0);

        // zoom-value listener: zooms the map acording to slider
        zoomSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //double currentX = ( (scrollPane.getWidth()/2) + scrollPane.getHvalue()*(mapCanvas.getWidth()*scaleFactor-scrollPane.getWidth()) ) / (mapCanvas.getWidth()*scaleFactor);
                //double currentY = ( (scrollPane.getHeight()/2) + scrollPane.getVvalue()*(mapCanvas.getHeight()*scaleFactor-scrollPane.getHeight()) ) / (mapCanvas.getHeight()*scaleFactor);
                scaleFactor = newValue.doubleValue();
                double v = scrollPane.getVvalue();
                double h = scrollPane.getHvalue();
                dontUpdateScroll = true;
                zoomGroup.setScaleX(scaleFactor);
                zoomGroup.setScaleY(scaleFactor);
                dontUpdateScroll = true;
                scrollPane.setVvalue(v);
                scrollPane.setHvalue(h);
                updateZoom();
            }
        });

		// scroll-position listener: on sudden changes the scroll position is set to (hVal/vVal)
		scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!dontUpdateScroll) {
                    if (Math.abs(oldValue.doubleValue() - newValue.doubleValue()) > 0.1) {
                        dontUpdateScroll = true;
                        scrollPane.setVvalue(vVal);
                        scrollPane.setHvalue(hVal);
                        vVal = 0;
                        hVal = 0;
                    }
                } else {
                    dontUpdateScroll = false;
                }
            }
        });
	}

    @Override
    public void reload() {
        updateTables();
        updateMap();
        checkLocationFocus();
        checkTraderFocus();
        updateZoom();
    }

    /**
	 * WORLDMODE: Edit-Button
	 * LOCATIONMODE: Details-Button
	 */
	@FXML
	private void onEditButtonPressed() {
		log.debug("onEditButtonPressed - open Details Window");

		if (mode == WORLDMODE) {
			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/editlocation.fxml");
            EditLocationController ctrl = loader.getController();
            ctrl.setLocation(selectedLocation);
            ctrl.reload();

			stage.setTitle("Ort bearbeiten");
			stage.setScene(new Scene(scene, 900, 438));
			stage.setResizable(false);
			stage.showAndWait();
		} else {
			if (selectedObject instanceof Trader) {
				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/traderdetails.fxml");
				stage.setTitle("Händler-Details");

				TraderDetailsController controller = loader.getController();
				controller.setTrader((Trader) selectedObject);
                controller.reload();
				stage.setScene(new Scene(scene, 781, 830));
				stage.setResizable(false);
				stage.showAndWait();
			} else {
				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/edittavern.fxml");
				stage.setTitle("Wirtshaus");

				EditTavernController controller = loader.getController();
				controller.setTavern((Tavern) selectedObject);
                controller.reload();
				stage.setScene(new Scene(scene, 383, 400));
				stage.setResizable(false);
				stage.showAndWait();
			}

		}

		reload();
	}

	/**
	 * delete selected Object (Location in WORLDMODE, Trader/Tavern in LOCATIONMODE)
	 */
	@FXML
	private void onDeleteButtonPressed() {
		log.debug("onDeleteButtonPressed - deleting selected Data");

		if (mode == WORLDMODE) {
			Location selectedLocation = locationTable.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();

			if (selectedLocation != null) {
				log.debug("open Confirm-Delete-Location Dialog");
				int traderSize = traderService.getAllForLocation(selectedLocation).size();
				int tavernsSize = tavernService.getAllByLocation(selectedLocation.getId()).size();
				String connectedEntries = "";
				connectedEntries += "\n" + traderSize + " Händler";
				connectedEntries += "\n" + tavernsSize + " Wirtshäuser";
				// TODO ? make a 'DeleteService' to providing information of all entities, which connected entities exist

				Action response = Dialogs.create()
						.title("Löschen?")
						.masthead(null)
						.message("Wollen Sie den Ort '" + selectedLocation.getName() + "' wirklich löschen? Folgende verbundenden Einträge würden ebenfalls gelöscht werden:" + connectedEntries)
						.actions(Dialog.Actions.NO, Dialog.Actions.YES)
						.showConfirm(); // TODO was ist hier sinnvoll?
				if (response == Dialog.Actions.YES) {
					locationService.remove(selectedLocation);
					saveCancelService.save();
				}

				if (selectedLocation == fromLocation) {
					fromLocation = null;
					fromLabel.setText("kein Ort");
					calcButton.setDisable(true);
				}
				if (selectedLocation == toLocation) {
					toLocation = null;
					toLabel.setText("kein Ort");
					calcButton.setDisable(true);
				}
			}
		} else {
			if (selectedObject != null) {
				if (selectedObject instanceof Trader) {
					log.debug("open Confirm-Delete-Trader Dialog");
					Action response = Dialogs.create()
							.title("Löschen?")
							.masthead(null)
							.message("Wollen Sie den Händer '" + ((Trader) selectedObject).getName() + "' wirklich löschen")
							.actions(Dialog.Actions.NO, Dialog.Actions.YES)
							.showConfirm();
					if (response == Dialog.Actions.YES) {
						traderService.remove((Trader) selectedObject);
						saveCancelService.save();
						traderList.getItems().remove(selectedObject);
					}
				} else {
					log.debug("open Confirm-Delete-Tavern Dialog");
					Action response = Dialogs.create()
							.title("Löschen?")
							.masthead(null)
							.message("Wollen Sie das Wirtshaus '" + ((Tavern) selectedObject).getName() + "' wirklich löschen")
							.actions(Dialog.Actions.NO, Dialog.Actions.YES)
							.showConfirm();
					if (response == Dialog.Actions.YES) {
						tavernService.remove((Tavern) selectedObject);
						saveCancelService.save();
					}
				}
			}
		}

		reload();
	}

	/**
	 * create a new object
	 * if there is a map you have to select a position on this map, which opens the placement-window
	 * if there is no map, in WORLDMODE you are sent to create-location-window, in LOCATIONMODE you get a placement-window to choose between Trader and Tavern
	 */
	@FXML
	private void onCreateButtonPressed() {
		log.debug("onCreateButtonPressed - open Details Window");

		if (mode == WORLDMODE) {
			if (mapService.getWorldMap() == null) {

				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/editlocation.fxml");
                EditLocationController ctrl = loader.getController();
                ctrl.setLocation(null);
                ctrl.reload();

				stage.setTitle("Ort erstellen");
				stage.setScene(new Scene(scene, 900, 438));
				stage.setResizable(false);
				stage.showAndWait();

				updateTables();
			} else {
				locationTable.setDisable(true);
				createButton.setDisable(true);
				editButton.setDisable(true);
				deleteButton.setDisable(true);
				chooseButton.setText("Weltansicht");
				chooseButton.setDisable(false);
				creationMode = true;                    // -> waiting for scrollPane Click
			}
		} else {
			if (mapService.getLocationMap(selectedLocation) == null) {

				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/placement.fxml");
				PlacementController controller = loader.getController();

				stage.setTitle("Händler/Wirtshaus platzieren");
				controller.setUp(selectedLocation, new Point2D(0, 0), null, true);
                controller.reload();

				stage.setScene(new Scene(scene, 350, 190));
				stage.setResizable(false);
				stage.showAndWait();

				updateTables();
			} else {
				traderList.setDisable(true);
				createButton.setDisable(true);
				editButton.setDisable(true);
				deleteButton.setDisable(true);
				chooseButton.setText("Weltansicht");
				chooseButton.setDisable(false);
				creationMode = true;                    // -> wait for scrollPane Click
			}
		}

	}

	/**
	 * switches between modes / exits creation mode
	 */
	@FXML
	private void onChooseButtonPressed() {
		if (creationMode) {
			createButton.setDisable(false);
			creationMode = false;
			if (mode == WORLDMODE) {
				chooseButton.setText("Ortsansicht");
				checkLocationFocus();
				locationTable.setDisable(false);
			} else {
				chooseButton.setText("Weltansicht");
				checkTraderFocus();
				traderList.setDisable(false);
			}
		} else {
			if (selectedLocation == null) {     // should not be possible (checkFocus)
				Dialogs.create()
						.title("Fehler")
						.masthead(null)
						.message("Es muss ein Ort ausgewählt sein!")
						.showWarning();
				return;
			}
			if (mode == 0) {                    //switch to LOCATIONMODE
				worldScrollH = scrollPane.getHvalue();
				worldScrollV = scrollPane.getVvalue();
				mode = LOCATIONMODE;
				pathCalcGrid.setVisible(false);
				locationTable.setVisible(false);
				traderList.setVisible(true);
				selectedObject = null;
				deleteButton.setDisable(true);
				editButton.setDisable(true);
				chooseButton.setText("Weltansicht");
				createButton.setText("Händler / Wirtshaus platzieren");
				editButton.setText("Details");
				Stage stage = (Stage) editButton.getScene().getWindow();
				stage.setTitle("DSA-Händlertool - " + selectedLocation.getName());
			} else {                            // switch to WORLDMODE
				dontUpdateScroll = true;
				scrollPane.setHvalue(worldScrollH);
				scrollPane.setVvalue(worldScrollV);
				dontUpdateScroll = false;
				mode = WORLDMODE;
				pathCalcGrid.setVisible(true);
				locationTable.setVisible(true);
				traderList.setVisible(false);
				chooseButton.setText("Ortsansicht");
				createButton.setText("Ort platzieren");
				editButton.setText("Bearbeiten");
				Stage stage = (Stage) editButton.getScene().getWindow();
				stage.setTitle("DSA-Händlertool");
			}

			updateTables();
			if (mode == LOCATIONMODE) {
				traderList.getSelectionModel().select(0);
				checkTraderFocus();
			} else {
				checkLocationFocus();
			}
			updateMap();
			updateZoom();
		}
	}

	/**
	 * creation mode: opens placement-window and forwards the clicked position
	 * else: select location/location-connection/trader/tavern on map
	 */
	@FXML
	private void onScrollPaneClicked() {

		// calculate mouse position
		Scene SPscene = scrollPane.getScene();
		Point2D windowCoord = new Point2D(SPscene.getWindow().getX(), SPscene.getWindow().getY());
		Point2D sceneCoord = new Point2D(SPscene.getX(), SPscene.getY());
		Point2D nodeCoord = scrollPane.localToScene(0.0, 0.0);
		Point2D SPLocation = new Point2D(windowCoord.getX() + sceneCoord.getX() + nodeCoord.getX(), windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());

		Point mousePosition = MouseInfo.getPointerInfo().getLocation();
		Canvas mapCanvas = (Canvas) zoomGroup.getChildren().get(0);
		double scrollableX = mapCanvas.getWidth() * scaleFactor - scrollPane.getWidth();
		double scrollableY = mapCanvas.getHeight() * scaleFactor - scrollPane.getHeight();
		if (mapCanvas.getWidth() > scrollPane.getWidth()) {
			scrollableX += 12;
		}
		if (mapCanvas.getHeight() > scrollPane.getHeight()) {
			scrollableY += 12;
		}

		int xPos = (int) (mousePosition.getX() - SPLocation.getX() + scrollPane.getHvalue() * scrollableX);
		int yPos = (int) (mousePosition.getY() - SPLocation.getY() + scrollPane.getVvalue() * scrollableY);

		Point2D pos = new Point2D(xPos, yPos);
		Point2D realPos = new Point2D(xPos / scaleFactor, yPos / scaleFactor);


		if (creationMode) {                 // in creation mode: chose position to place location/trader/tavern
			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/placement.fxml");
			PlacementController controller = loader.getController();
			if (mode == WORLDMODE) {
				stage.setTitle("Ort platzieren");
				controller.setUp(null, realPos, selectedLocation, false);           // (locaiton, position, selectedObject, noMap)
			} else {
				stage.setTitle("Händler/Wirtshaus platzieren");
				controller.setUp(selectedLocation, realPos, selectedObject, false); // (locaiton, position, selectedObject, noMap)
			}
            controller.reload();

			stage.setScene(new Scene(scene, 350, 190));
			stage.setResizable(false);
			stage.showAndWait();

			if (mode == WORLDMODE) {
				checkLocationFocus();
			} else {
				checkTraderFocus();
			}

			creationMode = false;
			createButton.setDisable(false);
			if (mode == WORLDMODE) {
				chooseButton.setText("Ortsansicht");
				checkLocationFocus();
				locationTable.setDisable(false);
			} else {
				chooseButton.setText("Weltansicht");
				checkTraderFocus();
				traderList.setDisable(false);
			}
			updateMap();
			updateTables();
		} else {                            // selection mode
			if (mode == WORLDMODE) {
				int locX;
				int locY;
				List<Location> locations = locationService.getAll();
				for (Location l : locations) {                  // search all locations for hit
					locX = (int) (l.getxCoord() * scaleFactor);
					locY = (int) (l.getyCoord() * scaleFactor);
					if (xPos > locX - 20 && xPos < locX + 20 && yPos > locY - 20 && yPos < locY + 20) {
						locationTable.getSelectionModel().select(l);
						return;
					}
				}
				Location loc1;
				Location loc2;
				for (LocationConnection lc : connections) {     // seach all location-connections for hit
					loc1 = lc.getLocation1();
					loc2 = lc.getLocation2();
					if ( (xPos < loc1.getxCoord()+10 && xPos > loc2.getxCoord()-10) || (xPos > loc1.getxCoord()-10 && xPos < loc2.getxCoord()+10) ) {
						if ((yPos < loc1.getyCoord()+10 && yPos > loc2.getyCoord()-10) || (yPos > loc1.getyCoord()-10 && yPos < loc2.getyCoord()+10)) {
							if (loc1.getxCoord() > loc2.getxCoord()) {
								Location temp = loc1;
								loc1 = loc2;
								loc2 = temp;
							}
							double d = loc1.getyCoord();
							double k = ((double) (loc2.getyCoord()-loc1.getyCoord())) / ((double) (loc2.getxCoord()-loc1.getxCoord()));
							double x = xPos - loc1.getxCoord();
							if (yPos-10 < (int) (k * x + d) && yPos+10 > (int) (k * x + d)) {
								Stage stage = new Stage();
								Parent scene = (Parent) loader.load("/gui/movingtraderlist.fxml");
								MovingTraderListController controller = loader.getController();
								controller.setLocationConnection(lc);
                                controller.reload();
								stage.setTitle("Fahrende Händler");
								stage.setScene(new Scene(scene, 600, 400));
								stage.setResizable(false);
								stage.showAndWait();
								return;
							}
						}
					}
				}
			} else {
				int locX;
				int locY;
				List<Trader> traders = traderService.getAllForLocation(selectedLocation);
				for (Trader t : traders) {                      // search all traders for hit
					locX = (int) (t.getxPos() * scaleFactor);
					locY = (int) (t.getyPos() * scaleFactor);
					if (xPos > locX - 20 && xPos < locX + 20 && yPos > locY - 20 && yPos < locY + 20) {
						traderList.getSelectionModel().select(t);
					}
				}
				List<Tavern> taverns = tavernService.getAllByLocation(selectedLocation.getId());
				for (Tavern t : taverns) {                      // search all taverns for hit
					locX = (int) (t.getxPos() * scaleFactor);
					locY = (int) (t.getyPos() * scaleFactor);
					if (xPos > locX - 20 && xPos < locX + 20 && yPos > locY - 20 && yPos < locY + 20) {
						traderList.getSelectionModel().select(t);
					}
				}
			}
		}
	}


	@FXML
	public void onPlayerClicked(ActionEvent event) {
		log.debug("onPlayerClicked - open Player Window");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/playerlist.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Spieler verwalten");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
	}

	@FXML
	private void onCurrencyClicked() {
		log.debug("onWarenClicked - open Currency Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/currencyList.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Währungen");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
	}

	@FXML
	private void onCalculateCurrencyClicked() {
		log.debug("onCalculateCurrencyClicked - open Calculate Currency Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/calculatecurrency.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Währung umrechnen");
		stage.setScene(new Scene(scene, 600, 215));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
	}

    @FXML
    private void onCalculateProductPrice() {
        log.debug("onCalculateProductPriceClicked - open Calculate Product Price Window");
        Stage stage = new Stage();

        Parent scene = (Parent) loader.load("/gui/calculatePrice.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

        stage.setTitle("Preis berechnen");
        stage.setScene(new Scene(scene, 600, 400));
        stage.setResizable(false);
        stage.showAndWait();

        updateTables();
        updateMap();
    }

	@FXML
	private void onGrenzenGebieteClicked() {
		log.debug("onGrenzenGebieteClicked - open Grenzen und Gebiete Window");

		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/regionlist.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Grenzen und Gebiete");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
		checkLocationFocus();
		checkTraderFocus();
	}

	@FXML
	private void onTraderCategoriesClicked() {
		log.debug("onTraderCategoriesClicked - open Trader Categories Window");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/tradercategorylist.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Händlerkategorien");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
		checkLocationFocus();
		checkTraderFocus();
	}

	@FXML
	private void onWarenClicked() {
		log.debug("onWarenClicked - open Waren Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/productslist.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Waren");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
		checkLocationFocus();
		checkTraderFocus();
	}

	@FXML
	private void onWarenKategorieClicked() {
		log.debug("onWarenKategorieClicked - open Warenkategorie Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/productcategorylist.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Warenkategorie");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
		checkLocationFocus();
		checkTraderFocus();
	}

	@FXML
	private void onEditDateClicked() {
		log.debug("onEditDateClicked - open EditDate Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/edittime.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Datum umstellen");
		stage.setScene(new Scene(scene, 419, 150));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
		checkLocationFocus();
		checkTraderFocus();
	}

	@FXML
	private void onForwardTimeClicked() {
		log.debug("onForwardTimeClicked - open ForwardTime Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/forwardtime.fxml");
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Zeit vorstellen");
		stage.setScene(new Scene(scene, 462, 217));
		stage.setResizable(false);
		stage.showAndWait();

		updateTables();
		updateMap();
		checkLocationFocus();
		checkTraderFocus();
	}

	@FXML
	private void onExitClicked() {
		log.debug("onExitClicked - exit Programm Request");
		if (exitProgramm()) {
			Stage primaryStage = (Stage) menuBar.getScene().getWindow();
			primaryStage.close();
		}
	}

	@FXML
	private void onPrintTraderClicked() {
		log.debug("onPrintTraderClicked - ...");
		File file = null;
		try {
			file = File.createTempFile("dsa-trader-export", ".pdf");
			file.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(file);
			traderPdfService.generatePdf(fos);
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			Dialogs.create()
				.title("Fehler")
				.masthead(null)
				.message("Export fehlgeschlagen.")
				.showWarning();
		}
	}

	@FXML
	private void onWeltkarteImportierenPressed() {
		log.debug("onWeltkarteImportierenPressed called");

		File newmap = mapService.chooseMap();
		if (newmap == null) {
			return;
		}
		mapService.setWorldMap(newmap);

		updateMap();
		checkLocationFocus();
		checkTraderFocus();
	}

	@FXML
	private void onWeltkarteExportierenPressed() {
		log.debug("onWeltkarteExportierenPressed called");

		if (mapService.getWorldMap() != null) {
			mapService.exportMap("worldMap");
		} else {
			Dialogs.create()
					.title("Fehler")
					.masthead(null)
					.message("Es wurde keine Ortskarte gefunden!")
					.showWarning();
		}
	}

	/**
	 * sets currently selected location as from-location
	 */
	@FXML
	private void onFromPressed() {
		if (selectedLocation != null) {
			fromLocation = selectedLocation;
			fromLabel.setText(selectedLocation.getName());
			if (toLocation != null) {
				calcButton.setDisable(false);
			}
			resultLabel.setText("kein Ergebnis");
			nothingChanged = false;
			calcButton.setText("Berechnen");
		}
	}

	/**
	 * sets currently selected location as to-location
	 */
	@FXML
	private void onToPressed() {
		if (selectedLocation != null) {
			toLocation = selectedLocation;
			toLabel.setText(selectedLocation.getName());
			if (fromLocation != null) {
				calcButton.setDisable(false);
			}
			resultLabel.setText("kein Ergebnis");
			nothingChanged = false;
			calcButton.setText("Berechnen");
		}
	}

	/**
	 * calculates shortest route between from and to, an shows the route on the map
	 * if a route is already displayed it is instead not displayed
	 */
	@FXML
	private void onCalcPressed() {
		if (nothingChanged) {       // still old route displayed
			zoomGroup.getChildren().remove(pathCanvas);
			pathCanvas = new Canvas(1, 1);
			zoomGroup.getChildren().add(pathCanvas);
			nothingChanged = false;
			calcButton.setText("Berechnen");
			return;
		}

		// calculate cost
		List<LocationConnection> path;
		try {
			path = locationConnectionService.getShortestPathBetween(fromLocation, toLocation);
		} catch (NoPathException e) {
			resultLabel.setText("Keine Verbindung!");
			return;
		}
		int cost = 0;
		for (LocationConnection l : path) {
			cost += l.getTravelTime();
		}
		resultLabel.setText(cost + " Stunden");
		if (cost == 1) {
			resultLabel.setText(cost + " Stunde");
		}

		// mark start and end location on map
		zoomGroup.getChildren().remove(pathCanvas);
		pathCanvas = new Canvas(mapCanvas.getWidth(), mapCanvas.getHeight());
		GraphicsContext gc = pathCanvas.getGraphicsContext2D();
		gc.setFill(Color.GREEN);
		gc.setLineWidth(4);
		gc.fillRoundRect(fromLocation.getxCoord() - 13, fromLocation.getyCoord() - 13, 26, 26, 15, 15);
		gc.fillRoundRect(toLocation.getxCoord() - 13, toLocation.getyCoord() - 13, 26, 26, 15, 15);

		// mark path on map
		Location loc1, loc2;
		int posX1, posY1, posX2, posY2;
		for (LocationConnection lc : path) {
			loc1 = lc.getLocation1();
			loc2 = lc.getLocation2();
			posX1 = loc1.getxCoord();
			posY1 = loc1.getyCoord();
			posX2 = loc2.getxCoord();
			posY2 = loc2.getyCoord();
			gc.setStroke(Color.GREEN);
			gc.strokeLine(posX1, posY1, posX2, posY2);
		}

		// add same listener to pathCanvas as is on mapCanvas, to enable highlighting stuff "through" the pathCanvas
		List<Location> locations = locationService.getAll();
		pathCanvas.addEventHandler(MouseEvent.MOUSE_MOVED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						boolean onLocation = false;
						for (Location l : locations) {
							if (e.getX() > l.getxCoord() - 10 && e.getX() < l.getxCoord() + 10 &&
									e.getY() > l.getyCoord() - 10 && e.getY() < l.getyCoord() + 10) {
								Canvas highlight = new Canvas(30, 30);
								highlight.getGraphicsContext2D().setLineWidth(4);
								highlight.getGraphicsContext2D().strokeRoundRect(4, 4, 22, 22, 13, 13);
								highlight.setLayoutX(l.getxCoord() - 15);
								highlight.setLayoutY(l.getyCoord() - 15);
								zoomGroup.getChildren().add(highlight);
								onLocation = true;
							}
						}
						if (!onLocation) {
							while (zoomGroup.getChildren().size() > 3) {
								zoomGroup.getChildren().remove(3);
							}
						}
					}
				}
		);

		calcButton.setText("Ausblenden");
		nothingChanged = true;

		zoomGroup.getChildren().add(pathCanvas);
	}

	/**
	 * zooms in 1/10 of zoom-range
	 */
	@FXML
	private void onZoomInPressed() {
		double oldVal = zoomSlider.getValue();
		double newVal = oldVal + (zoomSlider.getMax() - zoomSlider.getMin()) / 10;
		zoomSlider.adjustValue(newVal);
	}

	/**
	 * zooms out 1/10 of zoom-range
	 */
	@FXML
	private void onZoomOutPressed() {
		double oldVal = zoomSlider.getValue();
		double newVal = oldVal - (zoomSlider.getMax() - zoomSlider.getMin()) / 10;
		zoomSlider.adjustValue(newVal);
	}

	/**
	 * updates the locationTable
	 */
	private void updateTables() {
		locationTable.getItems().setAll(locationService.getAll());

		if (selectedLocation != null) {
			List<Trader> traders = traderService.getAllForLocation(selectedLocation);
			List<Tavern> taverns = tavernService.getAllByLocation(selectedLocation.getId());
			List<Object> all = new ArrayList<Object>();
			for (Trader t : traders) {
				all.add(t);
			}
			for (Tavern t : taverns) {
				all.add(t);
			}
			traderList.getItems().setAll(all);
		}
	}

	/**
	 * updates the minimum zoom value to fit the map into the scrollPane
	 */
	private void updateZoom() {
		double minScaleX = (scrollPane.getWidth() - 2) / mapCanvas.getWidth();
		double minScaleY = (scrollPane.getHeight() - 2) / mapCanvas.getHeight();
		zoomSlider.setMin(Math.min(minScaleX, minScaleY));
		zoomSlider.setMax(2.69);
	}

	/**
	 * updates the map to be displayed and redraws all locations/traders/taverns
	 */
	private void updateMap() {
		log.debug("updateMap called");

		// save current scroll position for restoring
		hVal = scrollPane.getHvalue();
		vVal = scrollPane.getVvalue();

		if (mode == WORLDMODE) {
			// load map
			File worldMap = mapService.getWorldMap();
			if (worldMap == null) {
				worldMap = mapService.getNoMapImage();
			}
			Image image = new Image("file:" + worldMap.getAbsolutePath());

			// create the 3 canvases + group structure
			mapCanvas = new Canvas(image.getWidth(), image.getHeight());
			if (selectionCanvas == null) {
				selectionCanvas = new Canvas(1, 1);
			}
			pathCanvas = new Canvas(1, 1);
			GraphicsContext gc = mapCanvas.getGraphicsContext2D();
			gc.drawImage(image, 0, 0);
			drawLocations(gc);
			zoomGroup = new Group(mapCanvas, selectionCanvas, pathCanvas);
			Group contentGroup = new Group(zoomGroup);
			scrollPane.setContent(contentGroup);

			// zoom to current value
			zoomGroup.setScaleX(scaleFactor);
			zoomGroup.setScaleY(scaleFactor);

			// reset path calculation
			nothingChanged = false;
			calcButton.setText("Berechnen");
			resultLabel.setText("kein Ergebnis");

			// add mouse-listener for highlighting
			List<Location> locations = locationService.getAll();
			mapCanvas.addEventHandler(MouseEvent.MOUSE_MOVED,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							boolean onLocation = false;
							for (Location l : locations) {
								if (e.getX() > l.getxCoord() - 10 && e.getX() < l.getxCoord() + 10 &&
										e.getY() > l.getyCoord() - 10 && e.getY() < l.getyCoord() + 10) {
									Canvas highlight = new Canvas(30, 30);
									highlight.getGraphicsContext2D().setLineWidth(4);
									highlight.getGraphicsContext2D().strokeRoundRect(4, 4, 22, 22, 13, 13);
									highlight.setLayoutX(l.getxCoord() - 15);
									highlight.setLayoutY(l.getyCoord() - 15);
									zoomGroup.getChildren().add(highlight);
									onLocation = true;
								}
							}
							if (!onLocation) {
								while (zoomGroup.getChildren().size() > 3) {
									zoomGroup.getChildren().remove(3);
								}
							}
						}
					}
			);

		} else {
			// load map
			File map = mapService.getLocationMap(selectedLocation);
			if (map == null) {
				map = mapService.getNoMapImage();
			}
			Image image = new Image("file:" + map.getAbsolutePath());

			// create the 3 canvases + group structure
			mapCanvas = new Canvas(image.getWidth(), image.getHeight());
			if (selectionCanvas == null) {
				Canvas selectionCanvas = new Canvas(1, 1);
			}
			GraphicsContext gc = mapCanvas.getGraphicsContext2D();
			gc.drawImage(image, 0, 0);
			drawTraders(gc);
			zoomGroup = new Group(mapCanvas, selectionCanvas);
			Group contentGroup = new Group(zoomGroup);
			scrollPane.setContent(contentGroup);

			// zoom in to current value
			zoomGroup.setScaleX(scaleFactor);
			zoomGroup.setScaleY(scaleFactor);

			// add mouse listener for highlighting
			List<Trader> traders = traderService.getAllForLocation(selectedLocation);
			List<Tavern> taverns = tavernService.getAllByLocation(selectedLocation.getId());
			mapCanvas.addEventHandler(MouseEvent.MOUSE_MOVED,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							boolean onStuff = false;
							for (Trader t : traders) {
								if (e.getX() > t.getxPos() - 10 && e.getX() < t.getxPos() + 10 &&
										e.getY() > t.getyPos() - 10 && e.getY() < t.getyPos() + 10) {
									Canvas highlight = new Canvas(20, 20);
									highlight.getGraphicsContext2D().setLineWidth(6);
									highlight.getGraphicsContext2D().setStroke(Color.RED);
									highlight.getGraphicsContext2D().strokeLine(4, 4, 16, 16);
									highlight.getGraphicsContext2D().strokeLine(4, 16, 16, 4);
									highlight.setLayoutX(t.getxPos() - 10);
									highlight.setLayoutY(t.getyPos() - 10);
									zoomGroup.getChildren().add(highlight);
									onStuff = true;
								}
							}
							for (Tavern t : taverns) {
								if (e.getX() > t.getxPos() - 10 && e.getX() < t.getxPos() + 10 &&
										e.getY() > t.getyPos() - 10 && e.getY() < t.getyPos() + 10) {
									Canvas highlight = new Canvas(20, 20);
									highlight.getGraphicsContext2D().setLineWidth(6);
									highlight.getGraphicsContext2D().setStroke(Color.RED);
									highlight.getGraphicsContext2D().strokeLine(4, 4, 16, 16);
									highlight.getGraphicsContext2D().strokeLine(4, 16, 16, 4);
									highlight.setLayoutX(t.getxPos() - 10);
									highlight.setLayoutY(t.getyPos() - 10);
									zoomGroup.getChildren().add(highlight);
									onStuff = true;
								}
							}
							if (!onStuff) {
								while (zoomGroup.getChildren().size() > 2) {
									zoomGroup.getChildren().remove(2);
								}
							}
						}
					}
			);
		}
	}

	/**
	 * draws the locations and connections onto the world map
	 * @param gc the GraphicsContext of the mapCanvas
	 */
	private void drawLocations(GraphicsContext gc) {
		int posX1, posY1, posX2, posY2, posXm, posYm;
		Location loc1, loc2;
		List<Location> locations = locationService.getAll();
		gc.setLineWidth(3);
		for (Location l : locations) {                  // draw locations
			gc.setFill(new Color(
					(double) Integer.valueOf(l.getRegion().getColor().substring(0, 2), 16) / 255,
					(double) Integer.valueOf(l.getRegion().getColor().substring(2, 4), 16) / 255,
					(double) Integer.valueOf(l.getRegion().getColor().substring(4, 6), 16) / 255,
					1.0));
			posX1 = l.getxCoord();
			posY1 = l.getyCoord();
			if (posX1 != 0 && posY1 != 0) {
				gc.fillRoundRect(posX1 - 10, posY1 - 10, 20, 20, 10, 10);
				gc.setStroke(Color.BLACK);
				gc.setLineWidth(1);
				gc.strokeRoundRect(posX1 - 10, posY1 - 10, 20, 20, 10, 10);
				saveCancelService.refresh(l);
			}
		}
		connections = locationConnectionService.getAll();
		for (LocationConnection lc : connections) {     // draw conections
			loc1 = lc.getLocation1();
			loc2 = lc.getLocation2();
			posX1 = loc1.getxCoord();
			posY1 = loc1.getyCoord();
			posX2 = loc2.getxCoord();
			posY2 = loc2.getyCoord();
			posXm = posX1 + (posX2-posX1)/2;
			posYm = posY1 + (posY2-posY1)/2;

			gc.setLineWidth(3);
			gc.setStroke(new Color(
					(double) Integer.valueOf(loc1.getRegion().getColor().substring(0, 2), 16) / 255,
					(double) Integer.valueOf(loc1.getRegion().getColor().substring(2, 4), 16) / 255,
					(double) Integer.valueOf(loc1.getRegion().getColor().substring(4, 6), 16) / 255,
					1.0));
			gc.strokeLine(posX1, posY1, posXm, posYm);
			gc.setStroke(new Color(
					(double) Integer.valueOf(loc2.getRegion().getColor().substring(0, 2), 16) / 255,
					(double) Integer.valueOf(loc2.getRegion().getColor().substring(2, 4), 16) / 255,
					(double) Integer.valueOf(loc2.getRegion().getColor().substring(4, 6), 16) / 255,
					1.0));
			gc.strokeLine(posX2, posY2, posXm, posYm);
		}

		gc.setStroke(Color.BLACK);
		gc.setLineWidth(0.6);
		for (Location l : locations) {              // write names next to locations
			posX1 = l.getxCoord();
			posY1 = l.getyCoord();
			gc.strokeText(l.getName(), posX1 + 10, posY1 - 10);
		}
	}

	/**
	 * draws the traders and taverns onto the location map
	 * @param gc the GraphicsContext of the mapCanvas
	 */
	private void drawTraders(GraphicsContext gc) {
		int posX;
		int posY;
		gc.setLineWidth(5);
		List<Trader> traders = traderService.getAllForLocation(selectedLocation);
		for (Trader t : traders) {              // draw traders
			if (t instanceof MovingTrader) {
				gc.setStroke(Color.LIGHTBLUE);
			} else {
				gc.setStroke(Color.DARKBLUE);
			}
			posX = t.getxPos();
			posY = t.getyPos();
			if (posX != 0 && posY != 0) {
				gc.strokeLine(posX - 5, posY - 5, posX + 5, posY + 5);
				gc.strokeLine(posX - 5, posY + 5, posX + 5, posY - 5);
			}
		}
		gc.setStroke(Color.YELLOW);
		List<Tavern> taverns = tavernService.getAllByLocation(selectedLocation.getId());
		for (Tavern t : taverns) {              // draw taverns
			posX = t.getxPos();
			posY = t.getyPos();
			if (posX != 0 && posY != 0) {
				gc.strokeLine(posX - 5, posY - 5, posX + 5, posY + 5);
				gc.strokeLine(posX - 5, posY + 5, posX + 5, posY - 5);
			}
		}
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(0.6);
		for (Trader t : traders) {              // write names next to traders
			posX = t.getxPos();
			posY = t.getyPos();
			gc.strokeText(t.getName(), posX + 10, posY - 10);
		}
		for (Tavern t : taverns) {              // write names next to locations
			posX = t.getxPos();
			posY = t.getyPos();
			gc.strokeText(t.getName(), posX + 10, posY - 10);
		}
	}

	/**
	 * Shows a exit-confirm-dialog if more than the primaryStage are open and close all other stages if confirmed
	 *
	 * @return false if the user cancels or refuses the dialog, otherwise true
	 */
	public boolean exitProgramm() {
		Stage primaryStage = (Stage) menuBar.getScene().getWindow();
		List<Stage> stages = new ArrayList<Stage>(StageHelper.getStages());

		// only primaryStage
		if (stages.size() <= 1) {
			return true;
		}

		log.debug("open Dialog - Confirm-Exit-Dialog");
		Action response = Dialogs.create()
				.owner(primaryStage)
				.title("Programm beenden?")
				.masthead(null)
				.message("Wollen Sie das Händlertool wirklich beenden? Nicht gespeicherte Änderungen gehen dabei verloren.")
				.actions(Dialog.Actions.NO, Dialog.Actions.YES)
				.showConfirm();

		if (response == Dialog.Actions.YES) {
			log.debug("Confirm-Exit-Dialog confirmed");
			for (Stage s : stages) {
				if (!s.equals(primaryStage)) {
					s.close();
				}
			}
			return true;

		} else {
			log.debug("Confirm-Exit-Dialog refused");
			return false;
		}
	}

	/**
	 * checks the focus of the location-table and sets disables accordingly
	 * marks selected location on map
	 */
	private void checkLocationFocus() {
		selectedLocation = locationTable.getSelectionModel().getSelectedItem();
		if (selectedLocation == null) {
			selectedLocation = locationTable.getFocusModel().getFocusedItem();
		}
		if (selectedLocation == null) {
			deleteButton.setDisable(true);
			editButton.setDisable(true);
			chooseButton.setDisable(true);
			fromButton.setDisable(true);
			toButton.setDisable(true);

			zoomGroup.getChildren().remove(selectionCanvas);
			selectionCanvas = new Canvas(1, 1);
			zoomGroup.getChildren().add(selectionCanvas);
		} else {
			deleteButton.setDisable(false);
			editButton.setDisable(false);
			chooseButton.setDisable(false);
			fromButton.setDisable(false);
			toButton.setDisable(false);

			if (zoomGroup.getChildren().size() > 3) {
				zoomGroup.getChildren().remove(3);
			}
			zoomGroup.getChildren().remove(selectionCanvas);

			if (selectedLocation.getxCoord() > 0 && selectedLocation.getyCoord() > 0) {     // mark on map
				selectionCanvas = new Canvas(30, 30);
				selectionCanvas.getGraphicsContext2D().setLineWidth(4);
				selectionCanvas.getGraphicsContext2D().setStroke(Color.GREEN);
				selectionCanvas.getGraphicsContext2D().strokeRoundRect(4, 4, 22, 22, 13, 13);
				selectionCanvas.setLayoutX(selectedLocation.getxCoord() - 15);
				selectionCanvas.setLayoutY(selectedLocation.getyCoord() - 15);
			} else {
				selectionCanvas = new Canvas(1, 1);
			}

			zoomGroup.getChildren().add(selectionCanvas);
			zoomGroup.setScaleX(scaleFactor);
			zoomGroup.setScaleY(scaleFactor);
		}
	}

	/**
	 * checks the focus of the trader-list and sets disables accordingly
	 * marks selected traders/taverns on map
	 */
	private void checkTraderFocus() {
		selectedObject = traderList.getSelectionModel().getSelectedItem();//.getFocusModel().getFocusedItem();
		if (selectedObject == null) {
			selectedObject = traderList.getFocusModel().getFocusedItem();
		}
		if (selectedObject == null) {
			deleteButton.setDisable(true);
			editButton.setDisable(true);
			zoomGroup.getChildren().remove(selectionCanvas);
			selectionCanvas = new Canvas(1, 1);
			zoomGroup.getChildren().add(selectionCanvas);
		} else {
			deleteButton.setDisable(false);
			editButton.setDisable(false);
			if (selectedObject instanceof Trader) {
				editButton.setText("Details");
			}

			if (zoomGroup.getChildren().size() > 2) {
				zoomGroup.getChildren().remove(2);
			}
			zoomGroup.getChildren().remove(selectionCanvas);

			if ((selectedObject instanceof Trader && ((Trader) selectedObject).getxPos() > 0 && ((Trader) selectedObject).getyPos() > 0) ||
					(selectedObject instanceof Tavern && ((Tavern) selectedObject).getxPos() > 0 && ((Tavern) selectedObject).getyPos() > 0)) {     // mark on map
				selectionCanvas = new Canvas(30, 30);
				selectionCanvas.getGraphicsContext2D().setLineWidth(6);
				selectionCanvas.getGraphicsContext2D().setStroke(Color.GREEN);
				selectionCanvas.getGraphicsContext2D().strokeLine(4, 4, 16, 16);
				selectionCanvas.getGraphicsContext2D().strokeLine(4, 16, 16, 4);
				if (selectedObject instanceof Trader) {
					selectionCanvas.setLayoutX(((Trader) selectedObject).getxPos() - 10);
					selectionCanvas.setLayoutY(((Trader) selectedObject).getyPos() - 10);
				} else {
					selectionCanvas.setLayoutX(((Tavern) selectedObject).getxPos() - 10);
					selectionCanvas.setLayoutY(((Tavern) selectedObject).getyPos() - 10);
				}
			} else {
				selectionCanvas = new Canvas(1, 1);
			}

			zoomGroup.getChildren().add(selectionCanvas);
		}
		chooseButton.setDisable(false);
	}

	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setTavernService(TavernService tavernService) {
		this.tavernService = tavernService;
	}

	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

	public void setTraderPdfService(TraderPdfService traderPdfService) {
		this.traderPdfService = traderPdfService;
	}

	public void setSaveCancelService(SaveCancelService saveCancelService) {
		this.saveCancelService = saveCancelService;
	}

	public void setLocationConnectionService(LocationConnectionService locationConnectionService) {
		this.locationConnectionService = locationConnectionService;
	}

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}

}
