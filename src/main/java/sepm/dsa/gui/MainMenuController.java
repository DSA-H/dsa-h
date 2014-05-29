package sepm.dsa.gui;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.service.LocationService;
import sepm.dsa.service.MapService;
import sepm.dsa.service.TraderService;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainMenuController implements Initializable {

	private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);
	private SpringFxmlLoader loader;
	private LocationService locationService;
	private TraderService traderService;
	private MapService mapService;
	private Point2D SPLocation;
	private double scrollPositionX;
	private double scrollPositionY;
	private int mode;

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
	private TableView<Location> dataTable;
	@FXML
	private TableColumn firstColumn;
	@FXML
	private TableColumn secondColumn;
	@FXML
	private Button createButton;
	@FXML
	private Button editButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button chooseButton;

	private ImageView mapImageView = new ImageView();
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private Label xlabel;
	@FXML
	private Label ylabel;

	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		mode = 0;

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
		firstColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		secondColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

		ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
		dataTable.setItems(data);

		dataTable.getFocusModel().focusedItemProperty().addListener(new ChangeListener<Location>() {
			@Override
			public void changed(ObservableValue<? extends Location> observable, Location oldValue, Location newValue) {
				Location selectedLocation = dataTable.getFocusModel().getFocusedItem();
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
		});
	}

	private void changeMode() {

	}

	@FXML
	private void onEditButtonPressed() {
		log.debug("onEditButtonPressed - open Details Window");

		//TODO schön machen
		if (mode == 0) {
			Location selectedLocation = dataTable.getFocusModel().getFocusedItem();
			EditLocationController.setLocation(selectedLocation);


			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/editlocation.fxml");

			stage.setTitle("Ort bearbeiten");
			stage.setScene(new Scene(scene, 900, 438));
			stage.setResizable(false);
			stage.showAndWait();
		} else {}

		ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
		dataTable.setItems(data);
	}

	@FXML
	private void onDeleteButtonPressed() {
		log.debug("onDeleteButtonPressed - deleting selected Data");

		if (mode == 0) {
			Location selectedLocation = dataTable.getFocusModel().getFocusedItem();

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
					dataTable.getItems().remove(selectedLocation);
				}
			}
		} else {}

	}

	@FXML
	private void onCreateButtonPressed() {
		log.debug("onCreateButtonPressed - open Details Window");

		if (mode == 0) {
			EditLocationController.setLocation(null);

			Stage stage = new Stage();
			Parent scene = (Parent) loader.load("/gui/editlocation.fxml");

			stage.setTitle("Ort erstellen");
			stage.setScene(new Scene(scene, 900, 438));
			stage.setResizable(false);
			stage.showAndWait();
		} else {}

		ObservableList<Location> data = FXCollections.observableArrayList(locationService.getAll());
		dataTable.setItems(data);
	}

	@FXML
	private void onChooseButtonPressed() {
		changeMode();
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

		double scrollableX = mapImageView.getImage().getWidth() - scrollPane.getWidth();
		double scrollableY = mapImageView.getImage().getHeight() - scrollPane.getHeight();
		if (mapImageView.getImage().getWidth() > scrollPane.getWidth()) {
			scrollableX += 15;
		}
		if (mapImageView.getImage().getHeight() > scrollPane.getHeight()) {
			scrollableY += 15;
		}

		int xPos = (int) (mousePosition.getX() - SPLocation.getX() + scrollPositionX * scrollableX);
		int yPos = (int) (mousePosition.getY() - SPLocation.getY() + scrollPositionY * scrollableY);
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
	private void onShowLocationsClicked() {
		log.debug("onShowLocationsClicked - open Location Window");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/locationlist.fxml");

		stage.setTitle("Orte verwalten");
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
	private void onTradersPressed() {

		log.debug("called onTradersPressed");
		Stage stage = new Stage();
		Parent scene = (Parent) loader.load("/gui/traderlist.fxml");
		stage.setTitle("Händlerverwaltung");
		stage.setScene(new Scene(scene, 600, 400));
		stage.setResizable(false);
		stage.show();

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
		//choose File to export
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export-Datei auswählen");
		List<String> extensions = new ArrayList<String>();
		extensions.add("*.jpg");
		extensions.add("*.png");
		extensions.add("*.gif");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", extensions),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("GIF", "*.gif"),
				new FileChooser.ExtensionFilter("PNG", "*.png")
		);
		File exportFile = fileChooser.showSaveDialog(new Stage());

		if (exportFile == null) {
			return;
		}

		//look for world map
		File activeDir = new File("maps/active");
		File[] matchingFiles = activeDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("worldMap");
			}
		});

		if (matchingFiles != null && matchingFiles.length >= 1) {
			File worldMap = matchingFiles[0];
			String extSource = FilenameUtils.getExtension(worldMap.getAbsolutePath());
			String extTarget = FilenameUtils.getExtension(exportFile.getAbsolutePath());
			if (exportFile.exists() && !exportFile.isDirectory()) {
			}
			try {
				if (extTarget == "") {
					FileUtils.copyFile(worldMap, new File(exportFile.getAbsolutePath() + "." + extSource));
					exportFile.delete();
				} else {
					FileUtils.copyFile(worldMap, exportFile);
				}
				log.debug("exported worldMap");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateMap() {
		log.debug("updateMap called");
		if (mode == 0) {
			File worldMap = mapService.getWorldMap();

			if (worldMap != null) {
				Image image = new Image("file:" + worldMap.getAbsolutePath(), true);
				mapImageView.setImage(image);
				mapImageView.setSmooth(true);
				mapImageView.setPreserveRatio(true);
				scrollPane.setContent(mapImageView);
			}
		} else {}
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

	public void setLoader(SpringFxmlLoader loader) {
		this.loader = loader;
	}

	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}

	public void setTraderService(TraderService traderService) {
		this.traderService = traderService;
	}

	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

}
