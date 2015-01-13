package sepm.dsa.gui;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
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
	private TimeService timeService;
	private MapService mapService;
	private SaveCancelService saveCancelService;
	private LocationConnectionService locationConnectionService;
	private TraderPdfService traderPdfService;
	private DataSetService dataSetService;

	private Canvas mapCanvas, selectionCanvas, pathCanvas, highlight; // 3 canvases on top of each other in zoomGroup; bottom: mapCanvas, top: pathCanvas, additionally: highlight

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
    private Label weatherLabel;
    //private TableColumn weatherColumn;
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
        //weatherColumn.setCellValueFactory(new PropertyValueFactory<>("weather"));
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
	    if (mode == WORLDMODE) {
		    checkLocationFocus();
			weatherLabel.setText(timeService.getCurrentDate().toString());
	    } else {
		    checkTraderFocus();
            weatherLabel.setText("Wetter: " + selectedLocation.getWeather().getName());
	    }
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
			Parent scene = (Parent) loader.load("/gui/editlocation.fxml", stage);
            EditLocationController ctrl = loader.getController();
            ctrl.setLocation(selectedLocation);
            ctrl.reload();

			stage.setTitle("Ort bearbeiten");
			stage.setScene(new Scene(scene, 900, 438));
			stage.setResizable(false);
			stage.show();
		} else {
			if (selectedObject instanceof Trader) {
				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/traderdetails.fxml", stage);
				stage.setTitle("Händler-Details");

				TraderDetailsController controller = loader.getController();
				controller.setTrader((Trader) selectedObject);
                controller.reload();
				stage.setScene(new Scene(scene, 780, 720));
				stage.setResizable(false);
				stage.show();
			} else {
				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/edittavern.fxml", stage);
				stage.setTitle("Wirtshaus");

				EditTavernController controller = loader.getController();
				controller.setTavern((Tavern) selectedObject);
                controller.reload();
				stage.setScene(new Scene(scene, 383, 400));
				stage.setResizable(false);
				stage.show();
			}

		}
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

				Action response = Dialogs.create()
						.title("Löschen?")
						.masthead(null)
						.message("Wollen Sie den Ort '" + selectedLocation.getName() + "' wirklich löschen? Folgende verbundenden Einträge würden ebenfalls gelöscht werden:" + connectedEntries)
						.actions(Dialog.Actions.NO, Dialog.Actions.YES)
						.showConfirm();

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
				Parent scene = (Parent) loader.load("/gui/editlocation.fxml", stage);
                EditLocationController ctrl = loader.getController();
                ctrl.setLocation(null);
                ctrl.reload();

				stage.setTitle("Ort erstellen");
				stage.setScene(new Scene(scene, 900, 438));
				stage.setResizable(false);
				stage.show();

				updateTables();
			} else {
				locationTable.setDisable(true);
				createButton.setDisable(true);
				editButton.setDisable(true);
				deleteButton.setDisable(true);
				chooseButton.setText("Abbrechen");
				chooseButton.setDisable(false);
				creationMode = true;                    // -> waiting for scrollPane Click
			}
		} else {
			if (mapService.getLocationMap(selectedLocation) == null) {

				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/placement.fxml", stage);
				PlacementController controller = loader.getController();

				stage.setTitle("Händler/Wirtshaus platzieren");
				controller.setUp(selectedLocation, new Point2D(0, 0), null, true);
                controller.reload();

				stage.setScene(new Scene(scene, 350, 190));
				stage.setResizable(false);
				stage.show();

				updateTables();
			} else {
				traderList.setDisable(true);
				createButton.setDisable(true);
				editButton.setDisable(true);
				deleteButton.setDisable(true);
				chooseButton.setText("Abbrechen");
				chooseButton.setDisable(false);
				creationMode = true;                    // -> wait for Click on Map
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
			if (mode == WORLDMODE) {        //switch to LOCATIONMODE
				worldScrollH = scrollPane.getHvalue();
				worldScrollV = scrollPane.getVvalue();
				mode = LOCATIONMODE;
				pathCalcGrid.setVisible(false);
				locationTable.setVisible(false);
				traderList.setVisible(true);
				// weatherLabel.setVisible(true);
				weatherLabel.setText("Wetter: " + selectedLocation.getWeather().getName());
				selectedObject = null;
				deleteButton.setDisable(true);
				editButton.setDisable(true);
				chooseButton.setText("Weltansicht");
				createButton.setText("Händler / Wirtshaus platzieren");
				editButton.setText("Details");
				Stage stage = (Stage) editButton.getScene().getWindow();
				stage.setTitle("DSA-Händlertool - " + selectedLocation.getName());
				zoomSlider.adjustValue(1.0);
			} else {                            // switch to WORLDMODE
				dontUpdateScroll = true;
				scrollPane.setHvalue(worldScrollH);
				scrollPane.setVvalue(worldScrollV);
				dontUpdateScroll = false;
				mode = WORLDMODE;
				pathCalcGrid.setVisible(true);
				locationTable.setVisible(true);
				traderList.setVisible(false);
				// weatherLabel.setVisible(false);
				weatherLabel.setText(timeService.getCurrentDate().toString());
				chooseButton.setText("Ortsansicht");
				createButton.setText("Ort platzieren");
				editButton.setText("Bearbeiten");
				Stage stage = (Stage) editButton.getScene().getWindow();
				stage.setTitle("DSA-Händlertool");
			}
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


	@FXML
	public void onPlayerClicked(ActionEvent event) {
		log.debug("onPlayerClicked - open Player Window");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/playerList.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Spieler verwalten");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onCurrencyClicked() {
		log.debug("onCurrencyClicked - open Currency Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/currencyList.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Währungen");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

    @FXML
    private void onCurrencySetClicked() {
        log.debug("onCurrencySetClicked - open Currency Window");
        Stage stage = new Stage();

        Parent scene = (Parent) loader.load("/gui/currencysetlist.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

        stage.setTitle("Währungssysteme");
        stage.setScene(new Scene(scene, 600, 440));
        stage.setResizable(false);
        stage.show();
    }

	@FXML
	private void onCalculateCurrencyClicked() {
		log.debug("onCalculateCurrencyClicked - open Calculate Currency Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/calculatecurrency.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Währung umrechnen");
		stage.setScene(new Scene(scene, 600, 215));
		stage.setResizable(false);
		stage.show();
	}

    @FXML
    private void onCalculateProductPrice() {
        log.debug("onCalculateProductPriceClicked - open Calculate Product Price Window");
        Stage stage = new Stage();

        Parent scene = (Parent) loader.load("/gui/calculatePrice.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

        stage.setTitle("Preis berechnen");
        stage.setScene(new Scene(scene, 600, 360));
        stage.setResizable(false);
        stage.show();
    }

	@FXML
	private void onGrenzenGebieteClicked() {
		log.debug("onGrenzenGebieteClicked - open Grenzen und Gebiete Window");

		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/regionlist.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Grenzen und Gebiete");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onTraderCategoriesClicked() {
		log.debug("onTraderCategoriesClicked - open Trader Categories Window");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/tradercategorylist.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Händlerkategorien");
		stage.setScene(new Scene(scene, 600, 432));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onWarenClicked() {
		log.debug("onWarenClicked - open Waren Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/productslist.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Waren");
		stage.setScene(new Scene(scene, 600, 500));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onWarenKategorieClicked() {
		log.debug("onWarenKategorieClicked - open Warenkategorie Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/productcategorylist.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Warenkategorie");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onEditDateClicked() {
		log.debug("onEditDateClicked - open EditDate Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/edittime.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Datum umstellen");
		stage.setScene(new Scene(scene, 419, 150));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onForwardTimeClicked() {
		log.debug("onForwardTimeClicked - open ForwardTime Window");
		Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

		Parent scene = (Parent) loader.load("/gui/forwardtime.fxml", stage);
        BaseController ctrl = loader.getController();
        ctrl.reload();

		stage.setTitle("Zeit vorstellen");
		stage.setScene(new Scene(scene, 462, 217));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onNextDayClicked() {
		log.debug("onNextDayClicked - next day");

		timeService.resetProgress();
		Service<Void> service = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws InterruptedException {
						do {
							updateProgress(timeService.getForwardProgress(), timeService.getForwardMaxProgress());
							updateMessage(timeService.getForwardMessage());
							Thread.sleep(100);
						} while(timeService.getForwardProgress() != timeService.getForwardMaxProgress());
						return null;
					}
				};
			}
		};

		service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent t) {
				saveCancelService.save();
			}
		});

		service.setOnFailed((t) -> {
			saveCancelService.save();
			throw new DSAValidationException("Fehler! Zeit vorwärtsstellen konnte nicht abgeschlossen werden!");
		});

		Dialogs.create()
				.title("Fortschritt")
				.masthead("Zeit vorwärts stellen ...")
				.showWorkerProgress(service);
		service.start();

		new Thread() {
			public void run() {
				timeService.forwardTime(1);
			}
		}.start();
	}

	@FXML
	private void onImportClicked(ActionEvent actionEvent) {
		log.debug("onImportClicked");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("DSA Zip Dateien", "*.zip"));

		Action response = Dialogs.create()
			.title("Datenimport")
			.message("Der Datenimport entfernt den gesamten aktuellen Daten- und Spielstand. Wollen Sie fortfahren?")
			.showConfirm();

		if (response != Dialog.Actions.YES) {
			return;
		}

		File file = fileChooser.showOpenDialog(menuBar.getScene().getWindow());

		if (file == null) {
			return;
		}

		try {
			dataSetService.importDataSet(file);
			Dialogs.create()
				.title("Import erfolgreich")
				.message("Import erfolgreich")
				.showInformation();
		} catch (DSARuntimeException e) {
			Dialogs.create()
				.title("Fehler")
				.message("Fehler beim Import: " + e.getMessage())
				.showError();
		}
	}

	@FXML
	private void onExportClicked(ActionEvent actionEvent) {
		log.debug("onExportClicked");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName("DSA.zip");

		File file = fileChooser.showSaveDialog(menuBar.getScene().getWindow());
		if (file != null) {
			try {
				dataSetService.saveCurrentDataSet(file);
			} catch (DSARuntimeException e) {
				Dialogs.create()
					.title("Fehler")
					.masthead(null)
					.message("Export Fehler: " + e.getMessage())
					.showError();
			}
		}
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
		reload();
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

	@FXML
	private void onMapOptionsClicked() {
		log.debug("onMapOptionsClicked called");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/mapoptions.fxml", stage);
		stage.setScene(new Scene(scene, 600, 400));
		BaseController ctrl = loader.getController();
		ctrl.reload();

		stage.setTitle("Kartenoptionen");
		stage.setResizable(false);
		stage.show();
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

		double iconSize = mapService.getWorldIconSize();

		// mark start and end location on map
		zoomGroup.getChildren().remove(pathCanvas);
		pathCanvas = new Canvas(mapCanvas.getWidth(), mapCanvas.getHeight());
		GraphicsContext gc = pathCanvas.getGraphicsContext2D();
		gc.setFill(mapService.getSelectionColor());
		gc.setLineWidth(iconSize/5);
		gc.fillRoundRect(fromLocation.getxCoord() - (iconSize*0.65), fromLocation.getyCoord() - (iconSize*0.65), (iconSize*1.3), (iconSize*1.3), (iconSize*0.75), (iconSize*0.75));
		gc.fillRoundRect(toLocation.getxCoord() - (iconSize*0.65), toLocation.getyCoord() - (iconSize*0.65), (iconSize*1.3), (iconSize*1.3), (iconSize*0.75), (iconSize*0.75));

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
			gc.setStroke(mapService.getSelectionColor());
			gc.strokeLine(posX1, posY1, posX2, posY2);
		}

		// add same listener to pathCanvas as is on mapCanvas, to enable highlighting stuff "through" the pathCanvas
		List<Location> locations = locationService.getAll();
		pathCanvas.addEventHandler(MouseEvent.MOUSE_MOVED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
						boolean onLocation = false;
						double xPos = e.getX();
						double yPos = e.getY();

						for (Location l : locations) {
							if (xPos > l.getxCoord() - (iconSize/2) && xPos < l.getxCoord() + (iconSize/2) &&
									yPos > l.getyCoord() - (iconSize/2) && yPos < l.getyCoord() + (iconSize/2)) {
								zoomGroup.getChildren().remove(highlight);
								highlight = new Canvas((iconSize*1.5), (iconSize*1.5));
								highlight.getGraphicsContext2D().setLineWidth((iconSize/5));
								highlight.getGraphicsContext2D().setStroke(mapService.getHighlightColor());
								highlight.getGraphicsContext2D().strokeRoundRect((iconSize/5), (iconSize/5), (iconSize*1.1), (iconSize*1.1), (iconSize*0.65), (iconSize*0.65));
								highlight.setLayoutX(l.getxCoord() - (iconSize*0.75));
								highlight.setLayoutY(l.getyCoord() - (iconSize*0.75));

								highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
									@Override
									public void handle(MouseEvent event) {
										locationTable.getSelectionModel().select(l);
									}
								});

								zoomGroup.getChildren().add(highlight);
								onLocation = true;
							}
						}

						if (!onLocation) {
							for (LocationConnection lc : connections) {     // seach all location-connections for hit
								Location loc1 = lc.getLocation1();
								Location loc2 = lc.getLocation2();
								double loc1x = loc1.getxCoord();
								double loc1y = loc1.getyCoord();
								double loc2x = loc2.getxCoord();
								double loc2y = loc2.getyCoord();
								if ((xPos < loc1x + (iconSize/2) && xPos > loc2x - (iconSize/2)) || (xPos > loc1x - (iconSize/2) && xPos < loc2x + (iconSize/2))) {
									if ((yPos < loc1y + (iconSize/2) && yPos > loc2y - (iconSize/2)) || (yPos > loc1y - (iconSize/2) && yPos < loc2y + (iconSize/2))) {
										if (loc1x > loc2x) {
											Location temp = loc1;
											loc1 = loc2;
											loc2 = temp;
											loc1x = loc1.getxCoord();
											loc1y = loc1.getyCoord();
											loc2x = loc2.getxCoord();
											loc2y = loc2.getyCoord();
										}
										double d = loc1y;
										double k = ((double) (loc2y - loc1y)) / ((double) (loc2x - loc1x));
										double x = xPos - loc1x;
										if (yPos - (iconSize/2) < (int) (k * x + d) && yPos + (iconSize/2) > (int) (k * x + d)) {
											zoomGroup.getChildren().remove(highlight);

											double length = Math.sqrt(Math.pow(Math.abs(loc1x-loc2x),2) + Math.pow(Math.abs(loc1y-loc2y),2));
											double degrees = Math.toDegrees( Math.atan( (loc2y-loc1y)/Math.abs(loc2x-loc1x) ) );
											if (loc1x > loc2x) {
												degrees *= -1;
											}
											highlight = new Canvas(length, (iconSize*0.6));
											highlight.getGraphicsContext2D().setLineWidth((iconSize/5));
											highlight.getGraphicsContext2D().setStroke(mapService.getHighlightColor());
											highlight.getGraphicsContext2D().strokeLine(0, (iconSize*0.3), length, (iconSize*0.3));
											highlight.setLayoutX(loc1x + (loc2x-loc1x)/2 - length/2);
											if (loc2y > loc1y) {
												highlight.setLayoutY(loc1y + (loc2y-loc1y)/2 - (iconSize*0.3));

											} else {
												highlight.setLayoutY(loc2y + (loc1y-loc2y)/2 - (iconSize*0.3));
											}

											highlight.setRotate(degrees);

											highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
												@Override
												public void handle(MouseEvent event) {
													Stage stage = new Stage();
													Parent scene = (Parent) loader.load("/gui/movingtraderlist.fxml", stage);
													MovingTraderListController controller = loader.getController();
													controller.setLocationConnection(lc);
													controller.reload();
													stage.setTitle("Fahrende Händler");
													stage.setScene(new Scene(scene, 600, 430));
													stage.setResizable(false);
													stage.show();
												}
											});

											zoomGroup.getChildren().add(highlight);
											onLocation = true;
										}
									}
								}
							}
						}

						if (!onLocation) {
								zoomGroup.getChildren().remove(highlight);
						}
					}
				}
		);

		// add mouse-listener for placement
		pathCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				double xPos = e.getX();
				double yPos = e.getY();

				Point2D pos = new Point2D(xPos, yPos);

				if (!creationMode) {
					return;
				}                                   // in creation mode: chose position to place location/trader/tavern
				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/placement.fxml", stage);
				PlacementController controller = loader.getController();
				if (mode == WORLDMODE) {
					stage.setTitle("Ort platzieren");
					controller.setUp(null, pos, selectedLocation, false);           // (locaiton, position, selectedObject, noMap)
				} else {
					stage.setTitle("Händler/Wirtshaus platzieren");
					controller.setUp(selectedLocation, pos, selectedObject, false); // (locaiton, position, selectedObject, noMap)
				}
				controller.reload();

				stage.setScene(new Scene(scene, 350, 190));
				stage.setResizable(false);
				stage.show();

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

			}
		});

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
							double xPos = e.getX();
							double yPos = e.getY();

							double iconSize = (double) mapService.getWorldIconSize();
							for (Location l : locations) {
								if (xPos > l.getxCoord() - (iconSize/2) && xPos < l.getxCoord() + (iconSize/2) &&
										yPos > l.getyCoord() - (iconSize/2) && yPos < l.getyCoord() + (iconSize/2)) {
									zoomGroup.getChildren().remove(highlight);
									highlight = new Canvas((iconSize*1.5), (iconSize*1.5));
									highlight.getGraphicsContext2D().setLineWidth((iconSize/5));
									highlight.getGraphicsContext2D().setStroke(mapService.getHighlightColor());
									highlight.getGraphicsContext2D().strokeRoundRect((iconSize/5), (iconSize/5), (iconSize*1.1), (iconSize*1.1), (iconSize*0.65), (iconSize*0.65));
									highlight.setLayoutX(l.getxCoord() - (iconSize*0.75));
									highlight.setLayoutY(l.getyCoord() - (iconSize*0.75));

									highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
										@Override
										public void handle(MouseEvent event) {
											locationTable.getSelectionModel().select(l);
										}
									});

									zoomGroup.getChildren().add(highlight);
									onLocation = true;
								}
							}

							if (!onLocation) {
								for (LocationConnection lc : connections) {     // seach all location-connections for hit
									Location loc1 = lc.getLocation1();
									Location loc2 = lc.getLocation2();
									double loc1x = loc1.getxCoord();
									double loc1y = loc1.getyCoord();
									double loc2x = loc2.getxCoord();
									double loc2y = loc2.getyCoord();
									if ((xPos < loc1x + (iconSize/2) && xPos > loc2x - (iconSize/2)) || (xPos > loc1x - (iconSize/2) && xPos < loc2x + (iconSize/2))) {
										if ((yPos < loc1y + (iconSize/2) && yPos > loc2y - (iconSize/2)) || (yPos > loc1y - (iconSize/2) && yPos < loc2y + (iconSize/2))) {
											if (loc1x > loc2x) {
												Location temp = loc1;
												loc1 = loc2;
												loc2 = temp;
												loc1x = loc1.getxCoord();
												loc1y = loc1.getyCoord();
												loc2x = loc2.getxCoord();
												loc2y = loc2.getyCoord();
											}
											double d = loc1y;
											double k = ((double) (loc2y - loc1y)) / ((double) (loc2x - loc1x));
											double x = xPos - loc1x;
											if (yPos - (iconSize/2) < (int) (k * x + d) && yPos + (iconSize/2) > (int) (k * x + d)) {
												zoomGroup.getChildren().remove(highlight);

												double length = Math.sqrt(Math.pow(Math.abs(loc1x-loc2x),2) + Math.pow(Math.abs(loc1y-loc2y),2));
												double degrees = Math.toDegrees( Math.atan( (loc2y-loc1y)/Math.abs(loc2x-loc1x) ) );
												if (loc1x > loc2x) {
													degrees *= -1;
												}
												highlight = new Canvas(length, (iconSize*0.6));
												highlight.getGraphicsContext2D().setLineWidth((iconSize/5));
												highlight.getGraphicsContext2D().setStroke(mapService.getHighlightColor());
												highlight.getGraphicsContext2D().strokeLine(0, (iconSize*0.3), length, (iconSize*0.3));
												highlight.setLayoutX(loc1x + (loc2x-loc1x)/2 - length/2);
												if (loc2y > loc1y) {
													highlight.setLayoutY(loc1y + (loc2y-loc1y)/2 - (iconSize*0.3));

												} else {
													highlight.setLayoutY(loc2y + (loc1y-loc2y)/2 - (iconSize*0.3));
												}

												highlight.setRotate(degrees);

												highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
													@Override
													public void handle(MouseEvent event) {
														Stage stage = new Stage();
														Parent scene = (Parent) loader.load("/gui/movingtraderlist.fxml", stage);
														MovingTraderListController controller = loader.getController();
														controller.setLocationConnection(lc);
														controller.reload();
														stage.setTitle("Fahrende Händler");
														stage.setScene(new Scene(scene, 600, 430));
														stage.setResizable(false);
														stage.show();
													}
												});

												zoomGroup.getChildren().add(highlight);
												onLocation = true;
											}
										}
									}
								}
							}

							if (!onLocation) {
								zoomGroup.getChildren().remove(highlight);
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
			double iconSize = mapService.getLocationIconSize(selectedLocation);
			mapCanvas.addEventHandler(MouseEvent.MOUSE_MOVED,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							boolean onStuff = false;
							for (Trader t : traders) {
								if (e.getX() > t.getxPos() - iconSize && e.getX() < t.getxPos() + iconSize &&
										e.getY() > t.getyPos() - iconSize && e.getY() < t.getyPos() + iconSize) {
									zoomGroup.getChildren().remove(highlight);
									highlight = new Canvas(iconSize*2, iconSize*2);
									highlight.getGraphicsContext2D().setLineWidth(iconSize*0.6);
									highlight.getGraphicsContext2D().setStroke(mapService.getHighlightColor());
									highlight.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*0.4), (iconSize*1.6), (iconSize*1.6));
									highlight.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*1.6), (iconSize*1.6), (iconSize*0.4));
									highlight.setLayoutX(t.getxPos() - iconSize);
									highlight.setLayoutY(t.getyPos() - iconSize);

									highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
										@Override
										public void handle(MouseEvent event) {
											traderList.getSelectionModel().select(t);
										}
									});

									zoomGroup.getChildren().add(highlight);
									onStuff = true;
								}
							}
							for (Tavern t : taverns) {
								if (e.getX() > t.getxPos() - iconSize && e.getX() < t.getxPos() + iconSize &&
										e.getY() > t.getyPos() - iconSize && e.getY() < t.getyPos() + iconSize) {
									zoomGroup.getChildren().remove(highlight);
									highlight = new Canvas(iconSize*2, iconSize*2);
									highlight.getGraphicsContext2D().setLineWidth(iconSize*0.6);
									highlight.getGraphicsContext2D().setStroke(mapService.getHighlightColor());
									highlight.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*0.4), (iconSize*1.6), (iconSize*1.6));
									highlight.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*1.6), (iconSize*1.6), (iconSize*0.4));
									highlight.setLayoutX(t.getxPos() - iconSize);
									highlight.setLayoutY(t.getyPos() - iconSize);

									highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
										@Override
										public void handle(MouseEvent event) {
											traderList.getSelectionModel().select(t);
										}
									});

									zoomGroup.getChildren().add(highlight);
									onStuff = true;
								}
							}
							if (!onStuff) {
									zoomGroup.getChildren().remove(highlight);
							}
						}
					}
			);
		}

		// add mouse-listener for placement
		mapCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				double xPos = e.getX();
				double yPos = e.getY();

				Point2D pos = new Point2D(xPos, yPos);

				if (!creationMode) {
					return;
				}                                   // in creation mode: chose position to place location/trader/tavern
				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/placement.fxml", stage);
				PlacementController controller = loader.getController();
				if (mode == WORLDMODE) {
					stage.setTitle("Ort platzieren");
					controller.setUp(null, pos, selectedLocation, false);           // (locaiton, position, selectedObject, noMap)
				} else {
					stage.setTitle("Händler/Wirtshaus platzieren");
					controller.setUp(selectedLocation, pos, selectedObject, false); // (locaiton, position, selectedObject, noMap)
				}
				controller.reload();

				stage.setScene(new Scene(scene, 350, 190));
				stage.setResizable(false);
				stage.show();

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

			}
		});
	}

	/**
	 * draws the locations and connections onto the world map
	 * @param gc the GraphicsContext of the mapCanvas
	 */
	private void drawLocations(GraphicsContext gc) {
		int posX1, posY1, posX2, posY2, posXm, posYm;
		Location loc1, loc2;
		List<Location> locations = locationService.getAll();
		double iconSize = mapService.getWorldIconSize();
		gc.setLineWidth(iconSize*0.15);
		for (Location l : locations) {                  // draw locations
			gc.setFill(new Color(
					(double) Integer.valueOf(l.getRegion().getColor().substring(0, 2), 16) / 255,
					(double) Integer.valueOf(l.getRegion().getColor().substring(2, 4), 16) / 255,
					(double) Integer.valueOf(l.getRegion().getColor().substring(4, 6), 16) / 255,
					1.0));
			posX1 = l.getxCoord();
			posY1 = l.getyCoord();
			if (posX1 != 0 && posY1 != 0) {
				gc.fillRoundRect(posX1 - (iconSize*0.5), posY1 - (iconSize*0.5), iconSize, iconSize, (iconSize*0.5), (iconSize*0.5));
				gc.setStroke(mapService.getBorderColor());
				gc.setLineWidth((iconSize*0.05));
				gc.strokeRoundRect(posX1 - (iconSize*0.5), posY1 - (iconSize*0.5), iconSize, iconSize, (iconSize*0.5), (iconSize*0.5));
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

			gc.setLineWidth(iconSize*0.15);
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

		gc.setFill(mapService.getNameColor());
		gc.setLineWidth(iconSize*mapService.getTextSize()*0.075);
		gc.setFont(new Font(iconSize*mapService.getTextSize()));
		for (Location l : locations) {              // write names next to locations
			posX1 = l.getxCoord();
			posY1 = l.getyCoord();
			gc.fillText(l.getName(), posX1 + (iconSize*0.5), posY1 - (iconSize*0.5));
		}
	}

	/**
	 * draws the traders and taverns onto the location map
	 * @param gc the GraphicsContext of the mapCanvas
	 */
	private void drawTraders(GraphicsContext gc) {
		int posX;
		int posY;
		double iconSize = mapService.getLocationIconSize(selectedLocation);
		gc.setLineWidth(iconSize*0.5);
		List<Trader> traders = traderService.getAllForLocation(selectedLocation);
		for (Trader t : traders) {              // draw traders
			if (t instanceof MovingTrader) {
				gc.setStroke(mapService.getMovingTraderColor());
			} else {
				gc.setStroke(mapService.getTraderColor());
			}
			posX = t.getxPos();
			posY = t.getyPos();
			if (posX != 0 && posY != 0) {
				gc.strokeLine(posX - (iconSize*0.5), posY - (iconSize*0.5), posX + (iconSize*0.5), posY + (iconSize*0.5));
				gc.strokeLine(posX - (iconSize*0.5), posY + (iconSize*0.5), posX + (iconSize*0.5), posY - (iconSize*0.5));
			}
		}
		gc.setStroke(mapService.getTavernColor());
		List<Tavern> taverns = tavernService.getAllByLocation(selectedLocation.getId());
		for (Tavern t : taverns) {              // draw taverns
			posX = t.getxPos();
			posY = t.getyPos();
			if (posX != 0 && posY != 0) {
				gc.strokeLine(posX - (iconSize*0.5), posY - (iconSize*0.5), posX + (iconSize*0.5), posY + (iconSize*0.5));
				gc.strokeLine(posX - (iconSize*0.5), posY + (iconSize*0.5), posX + (iconSize*0.5), posY - (iconSize*0.5));
			}
		}
		gc.setFill(mapService.getNameColor());
		gc.setLineWidth(iconSize * mapService.getTextSize() * 0.15);
		gc.setFont(new Font(iconSize*mapService.getTextSize()*2));
		for (Trader t : traders) {              // write names next to traders
			posX = t.getxPos();
			posY = t.getyPos();
			gc.fillText(t.getName(), posX + iconSize, posY - iconSize);
		}
		for (Tavern t : taverns) {              // write names next to locations
			posX = t.getxPos();
			posY = t.getyPos();
			gc.fillText(t.getName(), posX + iconSize, posY - iconSize);
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

			zoomGroup.getChildren().remove(selectionCanvas);

			if (selectedLocation.getxCoord() > 0 && selectedLocation.getyCoord() > 0) {     // mark on map
				double iconSize = mapService.getWorldIconSize();
				selectionCanvas = new Canvas((iconSize*1.5), (iconSize*1.5));
				selectionCanvas.getGraphicsContext2D().setLineWidth((iconSize*0.2));
				selectionCanvas.getGraphicsContext2D().setStroke(mapService.getSelectionColor());
				selectionCanvas.getGraphicsContext2D().strokeRoundRect((iconSize*0.2), (iconSize*0.2), (iconSize*1.1), (iconSize*1.1), (iconSize*0.65), (iconSize*0.65));
				selectionCanvas.setLayoutX(selectedLocation.getxCoord() - (iconSize*0.75));
				selectionCanvas.setLayoutY(selectedLocation.getyCoord() - (iconSize*0.75));
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

			zoomGroup.getChildren().remove(selectionCanvas);

			if ((selectedObject instanceof Trader && ((Trader) selectedObject).getxPos() > 0 && ((Trader) selectedObject).getyPos() > 0) ||
					(selectedObject instanceof Tavern && ((Tavern) selectedObject).getxPos() > 0 && ((Tavern) selectedObject).getyPos() > 0)) {     // mark on map
				double iconSize = mapService.getLocationIconSize(selectedLocation);
				selectionCanvas = new Canvas(iconSize*2, iconSize*2);
				selectionCanvas.getGraphicsContext2D().setLineWidth(iconSize*0.6);
				selectionCanvas.getGraphicsContext2D().setStroke(mapService.getSelectionColor());
				selectionCanvas.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*0.4), (iconSize*1.6), (iconSize*1.6));
				selectionCanvas.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*1.6), (iconSize*1.6), (iconSize*0.4));
				if (selectedObject instanceof Trader) {
					selectionCanvas.setLayoutX(((Trader) selectedObject).getxPos() - iconSize);
					selectionCanvas.setLayoutY(((Trader) selectedObject).getyPos() - iconSize);
				} else {
					selectionCanvas.setLayoutX(((Tavern) selectedObject).getxPos() - iconSize);
					selectionCanvas.setLayoutY(((Tavern) selectedObject).getyPos() - iconSize);
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

	public void setDataSetService(DataSetServiceImpl dataSetService) {
		this.dataSetService = dataSetService;
	}

	public void setTimeService(TimeService timeService) {
		this.timeService = timeService;
	}
}
