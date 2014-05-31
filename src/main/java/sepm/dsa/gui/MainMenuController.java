package sepm.dsa.gui;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.model.Tavern;
import sepm.dsa.model.Trader;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.MapService;
import sepm.dsa.service.TavernService;
import sepm.dsa.service.TraderService;

import javafx.scene.Node;
import java.awt.Point;
import java.awt.MouseInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainMenuController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);
	private SpringFxmlLoader loader;
	private LocationService locationService;
	private TraderService traderService;
	private TavernService tavernService;
	private MapService mapService;
	private Point2D SPLocation;
	private double scrollPositionX;
	private double scrollPositionY;
	private Location selectedLocation;
	private Trader selectedTrader;
	private int mode;   // 0..worldMode(default) 1..locationMode
	private Boolean creationMode = false;

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
	@FXML
	private MenuItem location;

	@FXML
	private TableView<Location> locationTable;
	@FXML
	private TableColumn locationColumn;
	@FXML
	private TableColumn regionColumn;
	@FXML
	private Button createButton;
	@FXML
	private Button editButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button chooseButton;
	@FXML
	private ListView traderList;

	private ImageView mapImageView = new ImageView();
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private Label xlabel;
	@FXML
	private Label ylabel;

	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
			                    Number old_val, Number new_val) {
				scrollPositionY = new_val.doubleValue();
			}
		});
		scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
			                    Number old_val, Number new_val) {
				scrollPositionX = new_val.doubleValue();
			}
		});

		updateMap();

		// init table
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

		ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
		locationTable.setItems(data);

		locationTable.getFocusModel().focusedItemProperty().addListener(new ChangeListener<Location>() {
			@Override
			public void changed(ObservableValue<? extends Location> observable, Location oldValue, Location newValue) {
				checkLocationFocus();
			}
		});

		traderList.getFocusModel().focusedItemProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				selectedTrader = (Trader) traderList.getFocusModel().getFocusedItem();
				checkTraderFocus();
			}
		});

		mode = 0;

		chooseButton.setStyle("-fx-font-weight: bold;");
	}

	private void changeMode() {
		if (selectedLocation == null) {
			Dialogs.create()
					.title("Fehler")
					.masthead(null)
					.message("Es muss ein Ort ausgewählt sein!")
					.showWarning();
			return;
		}
		if (mode == 0) {
			mode = 1;
			locationTable.setVisible(false);
			traderList.setVisible(true);
			selectedTrader = null;
			deleteButton.setDisable(true);
			editButton.setDisable(true);

			ObservableList<Trader> data = FXCollections.observableArrayList(traderService.getAllForLocation(selectedLocation));
			traderList.setItems(data);

			chooseButton.setText("Weltansicht");
			editButton.setText("Details");

			checkTraderFocus();
		} else {
			mode = 0;
			locationTable.setVisible(true);
			traderList.setVisible(false);
			chooseButton.setText("Ortsansicht");
			editButton.setText("Bearbeiten");
			checkLocationFocus();
		}

		updateMap();
	}

	@FXML
	private void onEditButtonPressed() {
		log.debug("onEditButtonPressed - open Details Window");

		if (mode == 0) {
			EditLocationController.setLocation(selectedLocation);

			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/editlocation.fxml");

			stage.setTitle("Ort bearbeiten");
			stage.setScene(new Scene(scene, 900, 438));
			stage.setResizable(false);
			stage.showAndWait();
			ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
			locationTable.setItems(data);
		} else {
			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/traderdetails.fxml");
			stage.setTitle("Händler-Details");

			TraderDetailsController controller = loader.getController();
			controller.setTrader(selectedTrader);
			stage.setScene(new Scene(scene, 800, 400));
			stage.setResizable(false);
			stage.showAndWait();
			ObservableList<Trader> data = FXCollections.observableArrayList(traderService.getAllForLocation(selectedLocation));
			traderList.setItems(data);
		}

		updateMap();

	}

	@FXML
	private void onDeleteButtonPressed() {
		log.debug("onDeleteButtonPressed - deleting selected Data");

		if (mode == 0) {
			Location selectedLocation = locationTable.getFocusModel().getFocusedItem();

			if (selectedLocation != null) {
				log.debug("open Confirm-Delete-Location Dialog");
				int traderSize = traderService.getAllForLocation(selectedLocation).size();
				int tavernsSize = 0;    // TODO get taverns connected to location
				String connectedEntries = "";
				connectedEntries += "\n" + traderSize + " Händler";
				connectedEntries += "\n" + tavernsSize + " Wirtshäuser";
				// TODO ? make a 'DeleteService' to providing information of all entities, which connected entities exist

				Action response = Dialogs.create()
						.title("Löschen?")
						.masthead(null)
						.message("Wollen Sie den Ort '" + selectedLocation.getName() + "' wirklich löschen? Folgende verbundenden Einträge würden ebenfalls gelöscht werden:" + connectedEntries)
						.showConfirm(); // TODO was ist hier sinnvoll?
				if (response == Dialog.Actions.YES) {
					locationService.remove(selectedLocation);
					locationTable.getItems().remove(selectedLocation);
				}
			}
		} else {
			if (selectedTrader != null) {
				log.debug("open Confirm-Delete-Trader Dialog");
				Action response = Dialogs.create()
						.title("Löschen?")
						.masthead(null)
						.message("Wollen Sie den Händer '" + selectedTrader.getName() + "' wirklich löschen")
						.showConfirm();
				if (response == Dialog.Actions.YES) {
					traderService.remove(selectedTrader);
					traderList.getItems().remove(selectedTrader);
				}
			}
		}

	}

	@FXML
	private void onCreateButtonPressed() {
		log.debug("onCreateButtonPressed - open Details Window");

		if (mode == 0) {
			if (mapService.getWorldMap() == null) {
				EditLocationController.setLocation(null);

				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/editlocation.fxml");

				stage.setTitle("Ort erstellen");
				stage.setScene(new Scene(scene, 900, 438));
				stage.setResizable(false);
				stage.showAndWait();
				ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
				locationTable.setItems(data);
			} else {
				locationTable.setDisable(true);
				createButton.setDisable(true);
				editButton.setDisable(true);
				deleteButton.setDisable(true);
				chooseButton.setText("Weltansicht");
				chooseButton.setDisable(false);
				creationMode = true;
			}
		} else {
			if (mapService.getLocationMap(selectedLocation) == null) {
				Stage stage = new Stage();
				Parent scene = (Parent) loader.load("/gui/edittrader.fxml");
				stage.setTitle("Händler erstellen");

				EditTraderController controller = loader.getController();
				controller.setTrader(null);
				controller.setLocation(selectedLocation);
				stage.setScene(new Scene(scene, 600, 400));
				stage.setResizable(false);
				stage.showAndWait();
				ObservableList<Trader> data = FXCollections.observableArrayList(traderService.getAllForLocation(selectedLocation));
				traderList.setItems(data);
			} else {
				traderList.setDisable(true);
				createButton.setDisable(true);
				editButton.setDisable(true);
				deleteButton.setDisable(true);
				chooseButton.setText("Weltansicht");
				chooseButton.setDisable(false);
				creationMode = true;
			}
		}

	}

	@FXML
	private void onChooseButtonPressed() {
		if (creationMode) {
			createButton.setDisable(false);
			creationMode = false;
			if (mode == 0) {
				chooseButton.setText("Ortsansicht");
				checkLocationFocus();
				locationTable.setDisable(false);
			} else {
				chooseButton.setText("Weltansicht");
				checkTraderFocus();
				traderList.setDisable(false);
			}
		} else {
			changeMode();
		}
	}

	private void setSPLocation() {
		Scene scene = scrollPane.getScene();
		Point2D windowCoord = new Point2D(scene.getWindow().getX(), scene.getWindow().getY());
		Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
		Point2D nodeCoord = scrollPane.localToScene(0.0, 0.0);
		SPLocation = new Point2D(windowCoord.getX() + sceneCoord.getX() + nodeCoord.getX(), windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());
	}

	@FXML
	private void onScrollPaneClicked() {

		setSPLocation();
		Point mousePosition = MouseInfo.getPointerInfo().getLocation();
		Pane pane = (Pane) scrollPane.getContent();
		Canvas canvas = (Canvas) pane.getChildren().get(0);
		double scrollableX = canvas.getWidth() - scrollPane.getWidth();
		double scrollableY = canvas.getHeight() - scrollPane.getHeight();
		if (canvas.getWidth() > scrollPane.getWidth()) {
			scrollableX += 12;
		}
		if (canvas.getHeight() > scrollPane.getHeight()) {
			scrollableY += 12;
		}

		int xPos = (int) (mousePosition.getX() - SPLocation.getX() + scrollPositionX * scrollableX);
		int yPos = (int) (mousePosition.getY() - SPLocation.getY() + scrollPositionY * scrollableY);

		Point2D pos = new Point2D(xPos, yPos);

		if (creationMode) {
			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/placement.fxml");
			PlacementController controller = loader.getController();
			if (mode == 0) {
				stage.setTitle("Ort platzieren");
				controller.setUp(null, pos, selectedLocation);
			} else {
				stage.setTitle("Händler platzieren");
				controller.setUp(selectedLocation, pos, selectedTrader);
			}
			stage.setScene(new Scene(scene, 350, 160));
			stage.setResizable(false);
			stage.showAndWait();

			if (mode == 0) {
				checkLocationFocus();
			} else {
				checkTraderFocus();
			}

			creationMode = false;
			createButton.setDisable(false);
			if (mode == 0) {
				chooseButton.setText("Ortsansicht");
				checkLocationFocus();
				locationTable.setDisable(false);
			} else {
				chooseButton.setText("Weltansicht");
				checkTraderFocus();
				traderList.setDisable(false);
			}

			ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
			locationTable.setItems(data);

			updateMap();
		} else {
			if (mode == 0) {
				int locX;
				int locY;
				List<Location> locations = locationService.getAll();
				for (Location l : locations) {
					locX = l.getxCoord();
					locY = l.getyCoord();
					if (xPos > locX - 10 && xPos < locX + 10 && yPos > locY - 10 && yPos < locY + 10) {
						locationTable.getSelectionModel().select(l);
					}
				}
			} else {
				int locX;
				int locY;
				List<Trader> traders = traderService.getAllForLocation(selectedLocation);
				for (Trader t : traders) {
					locX = t.getxPos();
					locY = t.getyPos();
					if (xPos > locX - 10 && xPos < locX + 10 && yPos > locY - 10 && yPos < locY + 10) {
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

		stage.setTitle("Spieler verwalten");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onCurrencyClicked() {
		log.debug("onWarenClicked - open Currency Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/currencyList.fxml");

		stage.setTitle("Währungen");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onCalculateCurrencyClicked(){
		log.debug("onCalculateCurrencyClicked - open Calculate Currency Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/calculatecurrency.fxml");

		stage.setTitle("Währung umrechnen");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onGrenzenGebieteClicked() {
		log.debug("onGrenzenGebieteClicked - open Grenzen und Gebiete Window");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/regionlist.fxml");

		stage.setTitle("Grenzen und Gebiete");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onTraderCategoriesClicked() {
		log.debug("onTraderCategoriesClicked - open Trader Categories Window");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/tradercategorylist.fxml");

		stage.setTitle("Händlerkategorien");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onWarenClicked() {
		log.debug("onWarenClicked - open Waren Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/productslist.fxml");

		stage.setTitle("Waren");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
	}

	@FXML
	private void onTavernClicked() {
		log.debug("onTavernClicked - open Tavern Window");
		Stage stage = new Stage();

		Parent scene = (Parent) loader.load("/gui/tavernList.fxml");

		stage.setTitle("Wirtshäuser");
		stage.setScene(new Scene(scene, 600, 438));
		stage.setResizable(false);
		stage.show();
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
	private void onWeltkarteImportierenPressed() {
		log.debug("onWeltkarteImportierenPressed called");

		File newmap = mapService.chooseMap();
		if (newmap == null) {
			return;
		}
		mapService.setWorldMap(newmap);

		updateMap();
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

	private void updateMap() {
		log.debug("updateMap called");
		if (mode == 0) {
			File worldMap = mapService.getWorldMap();
			if (worldMap == null) {
				worldMap = mapService.getNoMapImage();
			}
			Image image = new Image("file:" + worldMap.getAbsolutePath());
			Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.drawImage(image, 0, 0);
			drawLocations(gc);
			Pane pane = new Pane(canvas);
			scrollPane.setContent(pane);

			canvas.addEventHandler(MouseEvent.MOUSE_MOVED,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							List<Location> locations = locationService.getAll();
							boolean onLocation = false;
							Canvas canvas = (Canvas) pane.getChildren().get(0);
							for (Location l : locations) {
								if (e.getX() > l.getxCoord()-10 && e.getX() < l.getxCoord()+10 &&
										e.getY() > l.getyCoord()-10 && e.getY() < l.getyCoord()+10) {
									Canvas highlight = new Canvas(30, 30);
									highlight.getGraphicsContext2D().setLineWidth(4);
									highlight.getGraphicsContext2D().strokeRoundRect(4, 4, 22, 22, 13, 13);
									highlight.setLayoutX(l.getxCoord()-15);
									highlight.setLayoutY(l.getyCoord()-15);
									pane.getChildren().add(highlight);
									onLocation = true;
								}
							}
							if (!onLocation) {
								while(pane.getChildren().size() > 1) {
									pane.getChildren().remove(1);
								}
							}
						}
					});

		} else {
			File map = mapService.getLocationMap(selectedLocation);
			if (map == null) {
				map = mapService.getNoMapImage();
			}
			Image image = new Image("file:" + map.getAbsolutePath());
			Canvas canvas = new Canvas(image.getWidth(), image.getHeight());
			GraphicsContext gc = canvas.getGraphicsContext2D();
			gc.drawImage(image, 0, 0);
			drawTraders(gc);
			Pane pane = new Pane(canvas);
			scrollPane.setContent(pane);

			canvas.addEventHandler(MouseEvent.MOUSE_MOVED,
					new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							List<Trader> traders = traderService.getAllForLocation(selectedLocation);
							List<Tavern> taverns = tavernService.getAllForLocation(selectedLocation);
							boolean onStuff = false;
							Canvas canvas = (Canvas) pane.getChildren().get(0);
							for (Trader t : traders) {
								if (e.getX() > t.getxPos()-10 && e.getX() < t.getxPos()+10 &&
										e.getY() > t.getyPos()-10 && e.getY() < t.getyPos()+10) {
									Canvas highlight = new Canvas(30, 30);
									highlight.getGraphicsContext2D().setLineWidth(7);
									highlight.getGraphicsContext2D().setStroke(Color.RED);
									highlight.getGraphicsContext2D().strokeLine(2.5, 2.5, 27.5, 27.5);
									highlight.getGraphicsContext2D().strokeLine(2.5, 27.5, 27.5, 2.5);
									highlight.setLayoutX(t.getxPos()-15);
									highlight.setLayoutY(t.getxPos()-15);
									pane.getChildren().add(highlight);
									onStuff = true;
								}
							}
							for (Tavern t : taverns) {
								if (e.getX() > t.getxPos()-10 && e.getX() < t.getxPos()+10 &&
										e.getY() > t.getyPos()-10 && e.getY() < t.getyPos()+10) {
									Canvas highlight = new Canvas(30, 30);
									highlight.getGraphicsContext2D().setLineWidth(7);
									highlight.getGraphicsContext2D().setStroke(Color.RED);
									highlight.getGraphicsContext2D().strokeLine(2.5, 2.5, 27.5, 27.5);
									highlight.getGraphicsContext2D().strokeLine(2.5, 27.5, 27.5, 2.5);
									highlight.setLayoutX(t.getxPos()-15);
									highlight.setLayoutY(t.getxPos()-15);
									pane.getChildren().add(highlight);
									onStuff = true;
								}
							}
							if (!onStuff) {
								while(pane.getChildren().size() > 1) {
									pane.getChildren().remove(1);
								}
							}
						}
					});
		}
	}

	private void drawLocations(GraphicsContext gc) {
		int posX1, posY1, posX2, posY2, posXm, posYm;
		Location loc1, loc2;
		List<Location> locations = locationService.getAll();
		gc.setLineWidth(3);
		for (Location l : locations) {
			gc.setFill(new Color(
					(double) Integer.valueOf(l.getRegion().getColor().substring(0, 2), 16) / 255,
					(double) Integer.valueOf(l.getRegion().getColor().substring(2, 4), 16) / 255,
					(double) Integer.valueOf(l.getRegion().getColor().substring(4, 6), 16) / 255,
					1.0));
			gc.setStroke(new Color(
					(double) Integer.valueOf(l.getRegion().getColor().substring(0, 2), 16) / 255,
					(double) Integer.valueOf(l.getRegion().getColor().substring(2, 4), 16) / 255,
					(double) Integer.valueOf(l.getRegion().getColor().substring(4, 6), 16) / 255,
					1.0));
			posX1 = l.getxCoord();
			posY1 = l.getyCoord();
			if (posX1 != 0 && posY1 != 0) {
				gc.fillRoundRect(posX1 - 10, posY1 - 10, 20, 20, 10, 10);
				for (LocationConnection lc : l.getAllConnections()) {
					loc1 = lc.getLocation1();
					loc2 = lc.getLocation2();
					posX1 = loc1.getxCoord();
					posY1 = loc1.getyCoord();
					posX2 = loc2.getxCoord();
					posY2 = loc2.getyCoord();
					posXm = posX1 + (posX2-posX1)/2;
					posYm = posY1 + (posY2-posY1)/2;
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

			}
		}
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(0.6);
		for (Location l : locations) {
			posX1 = l.getxCoord();
			posY1 = l.getyCoord();
			gc.strokeText(l.getName(), posX1 + 10, posY1 - 10);
		}
	}

	private void drawTraders(GraphicsContext gc) {
		int posX;
		int posY;
		gc.setLineWidth(5);
		gc.setStroke(Color.GREEN);
		List<Trader> traders = traderService.getAllForLocation(selectedLocation);
		for (Trader t : traders) {
			posX = t.getxPos();
			posY = t.getyPos();
			gc.strokeLine(posX - 5, posY - 5, posX + 5, posY + 5);
			gc.strokeLine(posX - 5, posY + 5, posX + 5, posY - 5);
		}
		gc.setStroke(Color.YELLOW);
		List<Tavern> taverns = tavernService.getAllForLocation(selectedLocation);
		for (Tavern t : taverns) {
			posX = t.getxPos();
			posY = t.getyPos();
			gc.strokeLine(posX - 5, posY - 5, posX + 5, posY + 5);
			gc.strokeLine(posX - 5, posY + 5, posX + 5, posY - 5);
		}
		gc.setStroke(Color.BLACK);
		gc.setLineWidth(0.6);
		for (Trader t : traders) {
			posX = t.getxPos();
			posY = t.getyPos();
			gc.strokeText(t.getName(), posX + 10, posY - 10);
		}
		for (Tavern t : taverns) {
			posX = t.getxPos();
			posY = t.getyPos();
			gc.strokeText(t.getName(), posX + 10, posY - 10);
		}
	}

	/**
	 * Shows a exit-confirm-dialog if more than the primaryStage are open and close all other stages if confirmed
	 *
	 * @return false if the user cancle or refuse the dialog, otherwise true
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

	private void checkLocationFocus() {
		selectedLocation = locationTable.getFocusModel().getFocusedItem();
		if (selectedLocation == null) {
			deleteButton.setDisable(true);
			editButton.setDisable(true);
			chooseButton.setDisable(true);
		} else {
			deleteButton.setDisable(false);
			editButton.setDisable(false);
			chooseButton.setDisable(false);
		}
	}

	private void checkTraderFocus() {
		selectedTrader = (Trader) traderList.getFocusModel().getFocusedItem();
		if (selectedTrader == null) {
			deleteButton.setDisable(true);
			editButton.setDisable(true);
		} else {
			deleteButton.setDisable(false);
			editButton.setDisable(false);
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

    public void setLoader(SpringFxmlLoader loader) {
        this.loader = loader;
    }

}
