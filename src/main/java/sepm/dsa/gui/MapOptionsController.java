package sepm.dsa.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.model.Location;
import sepm.dsa.service.*;

public class MapOptionsController extends BaseControllerImpl {
	private static final Logger log = LoggerFactory.getLogger(MapOptionsController.class);
	private MapService mapService;
	private LocationService locationService;
	private SaveCancelService saveCancelService;
	boolean first = true;

	@FXML
	private ColorPicker traderColorPicker;
	@FXML
	private ColorPicker movingTraderColorPicker;
	@FXML
	private ColorPicker tavernColorPicker;
	@FXML
	private ColorPicker borderColorPicker;
	@FXML
	private ColorPicker textColorPicker;
	@FXML
	private ColorPicker selectionColorPicker;
	@FXML
	private ColorPicker highlightColorPicker;
	@FXML
	private Slider locationSizeSlider;
	@FXML
	private Slider textSizeSlider;
	@FXML
	private Slider traderSizeSlider;
	@FXML
	private AnchorPane previewPane;
	@FXML
	private ChoiceBox locationBox;

	private Group canvasGroup = new Group();
	private Canvas demoCanvas;
	private Canvas highlight;
	private Canvas selection = new Canvas(0,0);

	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		super.initialize(location, resources);
		locationSizeSlider.setMin(5);
		locationSizeSlider.setMax(50);
		textSizeSlider.setMin(0.2);
		textSizeSlider.setMax(3);
		traderSizeSlider.setMin(5);
		traderSizeSlider.setMax(50);

		locationBox.setItems(FXCollections.observableArrayList(locationService.getAll()));
		if (locationService.getAll().size() != 0) {
			locationBox.getSelectionModel().select(0);
		}

		previewPane.getChildren().addAll(canvasGroup);

		ChangeListener drawListener = (observable, oldValue, newValue) -> drawExample();
		traderColorPicker.valueProperty().addListener(drawListener);
		movingTraderColorPicker.valueProperty().addListener(drawListener);
		tavernColorPicker.valueProperty().addListener(drawListener);
		borderColorPicker.valueProperty().addListener(drawListener);
		textColorPicker.valueProperty().addListener(drawListener);
		highlightColorPicker.valueProperty().addListener(drawListener);
		selectionColorPicker.valueProperty().addListener(drawListener);
		locationSizeSlider.valueProperty().addListener(drawListener);
		textSizeSlider.valueProperty().addListener(drawListener);
		traderSizeSlider.valueProperty().addListener(drawListener);
		locationBox.valueProperty().addListener(drawListener);
	}

	@Override
	public void reload() {
		Location selection = (Location) locationBox.getSelectionModel().getSelectedItem();
		locationBox.setItems(FXCollections.observableArrayList(locationService.getAll()));
		if (locationService.getAll().contains(selection)) {
			locationBox.getSelectionModel().select(selection);
		} else if (locationService.getAll().size() != 0) {
			locationBox.getSelectionModel().select(0);
		}
		traderColorPicker.setValue(mapService.getTraderColor());
		movingTraderColorPicker.setValue(mapService.getMovingTraderColor());
		tavernColorPicker.setValue(mapService.getTavernColor());
		borderColorPicker.setValue(mapService.getBorderColor());
		textColorPicker.setValue(mapService.getNameColor());
		selectionColorPicker.setValue(mapService.getSelectionColor());
		highlightColorPicker.setValue(mapService.getHighlightColor());
		locationSizeSlider.adjustValue(mapService.getWorldIconSize());
		textSizeSlider.adjustValue(mapService.getTextSize());
		if (locationBox.getSelectionModel().getSelectedItem() != null) {
			traderSizeSlider.adjustValue(mapService.getLocationIconSize((Location) locationBox.getSelectionModel().getSelectedItem()));
		} else {
			traderSizeSlider.adjustValue(10.0);
		}
		drawExample();
	}

	@FXML
	private void onSaveButtonPressed() {
		mapService.setTraderColor(traderColorPicker.getValue());
		mapService.setMovingTraderColor(movingTraderColorPicker.getValue());
		mapService.setTavernColor(tavernColorPicker.getValue());
		mapService.setBorderColor(borderColorPicker.getValue());
		mapService.setNameColor(textColorPicker.getValue());
		mapService.setSelectionColor(selectionColorPicker.getValue());
		mapService.setHighlightColor(highlightColorPicker.getValue());
		mapService.setWorldIconSize((int) locationSizeSlider.getValue());
		mapService.setTextSize(textSizeSlider.getValue());
		if (locationBox.getSelectionModel().getSelectedItem() != null) {
			mapService.setLocationIconSize((Location) locationBox.getSelectionModel().getSelectedItem(), traderSizeSlider.getValue());
		}

		saveCancelService.save();
		Stage stage = (Stage) traderColorPicker.getScene().getWindow();
		stage.close();
	}

	private void drawExample() {
		canvasGroup.getChildren().remove(demoCanvas);
		canvasGroup.getChildren().remove(highlight);
		if (canvasGroup.getChildren().size() > 0) {
			selection = (Canvas) canvasGroup.getChildren().remove(0);
		}
		demoCanvas = new Canvas(360, 250);
		GraphicsContext gc = demoCanvas.getGraphicsContext2D();
		if (locationBox.getSelectionModel().getSelectedItem() != null) {
			gc.setFill(mapService.stringToColor(((Location) locationBox.getSelectionModel().getSelectedItem()).getRegion().getColor()));
		} else {
			gc.setFill(Color.GREY);
		}
		double iconSize = locationSizeSlider.getValue();
		gc.fillRoundRect(100 - (iconSize * 0.5), 150 - (iconSize * 0.5), iconSize, iconSize, (iconSize * 0.5), (iconSize * 0.5));
		gc.setStroke(borderColorPicker.getValue());
		gc.setLineWidth((iconSize * 0.05));
		gc.strokeRoundRect(100 - (iconSize * 0.5), 150 - (iconSize * 0.5), iconSize, iconSize, (iconSize * 0.5), (iconSize * 0.5));


		iconSize = traderSizeSlider.getValue();
		gc.setLineWidth(iconSize * 0.5);
		gc.setStroke(traderColorPicker.getValue());
		gc.strokeLine(200 - (iconSize * 0.5), 83 - (iconSize * 0.5), 200 + (iconSize * 0.5), 83 + (iconSize * 0.5));
		gc.strokeLine(200 - (iconSize * 0.5), 83 + (iconSize * 0.5), 200 + (iconSize * 0.5), 83 - (iconSize * 0.5));

		gc.setStroke(movingTraderColorPicker.getValue());
		gc.strokeLine(200 - (iconSize * 0.5), 150 - (iconSize * 0.5), 200 + (iconSize * 0.5), 150 + (iconSize * 0.5));
		gc.strokeLine(200 - (iconSize * 0.5), 150 + (iconSize * 0.5), 200 + (iconSize * 0.5), 150 - (iconSize * 0.5));

		gc.setStroke(tavernColorPicker.getValue());
		gc.strokeLine(200 - (iconSize * 0.5), 216 - (iconSize * 0.5), 200 + (iconSize * 0.5), 216 + (iconSize * 0.5));
		gc.strokeLine(200 - (iconSize * 0.5), 216 + (iconSize * 0.5), 200 + (iconSize * 0.5), 216 - (iconSize * 0.5));


		gc.setFill(textColorPicker.getValue());
		iconSize = locationSizeSlider.getValue();
		gc.setFont(new Font(iconSize * textSizeSlider.getValue()));
		gc.fillText("Ortsname", 100 + (iconSize*0.5), 150 - (iconSize*0.5));
		iconSize = traderSizeSlider.getValue();
		gc.setFont(new Font(iconSize * textSizeSlider.getValue()));
		gc.fillText("Händler", 200 + iconSize, 86 - iconSize);
		gc.fillText("Fahrend", 200 + iconSize, 150 - iconSize);
		gc.fillText("Wirtshaus", 200 + iconSize, 216 - iconSize);

		canvasGroup.getChildren().addAll(demoCanvas, selection);

		Scene scene = demoCanvas.getScene();
		Point2D windowCoord = new Point2D(scene.getWindow().getX(), scene.getWindow().getY());
		Point2D sceneCoord = new Point2D(scene.getX(), scene.getY());
		Point2D nodeCoord = demoCanvas.localToScene(0.0, 0.0);
		double nodeX = Math.round(windowCoord.getX() + sceneCoord.getX() + nodeCoord.getX());
		double nodeY = Math.round(windowCoord.getY() + sceneCoord.getY() + nodeCoord.getY());

		demoCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				double xPos = e.getX();
				double yPos = e.getY();
				double iconSize = locationSizeSlider.getValue();
				double iconSize2 = traderSizeSlider.getValue();
				if (xPos > 100 - (iconSize/2) && xPos < 150 + (iconSize/2) &&
						yPos > 100 - (iconSize/2) && yPos < 150 + (iconSize/2)) {
					canvasGroup.getChildren().remove(highlight);
					highlight = new Canvas((iconSize*1.5), (iconSize*1.5));
					highlight.getGraphicsContext2D().setLineWidth((iconSize / 5));
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeRoundRect((iconSize / 5), (iconSize / 5), (iconSize * 1.1), (iconSize * 1.1), (iconSize * 0.65), (iconSize * 0.65));
					highlight.setLayoutX(100 - (iconSize * 0.75));
					highlight.setLayoutY(150 - (iconSize * 0.75));
					canvasGroup.getChildren().add(highlight);

					highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							canvasGroup.getChildren().remove(selection);
							selection = new Canvas((iconSize * 1.5), (iconSize * 1.5));
							selection.getGraphicsContext2D().setLineWidth((iconSize * 0.2));
							selection.getGraphicsContext2D().setStroke(selectionColorPicker.getValue());
							selection.getGraphicsContext2D().strokeRoundRect((iconSize * 0.2), (iconSize * 0.2), (iconSize * 1.1), (iconSize * 1.1), (iconSize * 0.65), (iconSize * 0.65));
							selection.setLayoutX(100 - (iconSize * 0.75));
							selection.setLayoutY(150 - (iconSize * 0.75));
							canvasGroup.getChildren().add(selection);
						}
					});
				} else if (e.getX() > 200 - iconSize2 && e.getX() < 200 + iconSize2 &&
						e.getY() > 83 - iconSize2 && e.getY() < 83 + iconSize2) {
					canvasGroup.getChildren().remove(highlight);
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(200 - iconSize2);
					highlight.setLayoutY(83 - iconSize2);
					canvasGroup.getChildren().add(highlight);

					highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							canvasGroup.getChildren().remove(selection);
							selection = new Canvas(iconSize2 * 2, iconSize2 * 2);
							selection.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
							selection.getGraphicsContext2D().setStroke(selectionColorPicker.getValue());
							selection.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
							selection.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
							selection.setLayoutX(200 - iconSize2);
							selection.setLayoutY(83 - iconSize2);
							canvasGroup.getChildren().add(selection);
						}
					});
				} else if (e.getX() > 200 - iconSize2 && e.getX() < 200 + iconSize2 &&
						e.getY() > 150 - iconSize2 && e.getY() < 150 + iconSize2) {
					canvasGroup.getChildren().remove(highlight);
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(200 - iconSize2);
					highlight.setLayoutY(150 - iconSize2);
					canvasGroup.getChildren().add(highlight);

					highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							canvasGroup.getChildren().remove(selection);
							selection = new Canvas(iconSize2 * 2, iconSize2 * 2);
							selection.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
							selection.getGraphicsContext2D().setStroke(selectionColorPicker.getValue());
							selection.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
							selection.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
							selection.setLayoutX(200 - iconSize2);
							selection.setLayoutY(150 - iconSize2);
							canvasGroup.getChildren().add(selection);
						}
					});
				} else if (e.getX() > 200 - iconSize2 && e.getX() < 200 + iconSize2 &&
						e.getY() > 216 - iconSize2 && e.getY() < 216 + iconSize2) {
					canvasGroup.getChildren().remove(highlight);
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(200 - iconSize2);
					highlight.setLayoutY(216 - iconSize2);
					canvasGroup.getChildren().add(highlight);

					highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							canvasGroup.getChildren().remove(selection);
							selection = new Canvas(iconSize2 * 2, iconSize2 * 2);
							selection.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
							selection.getGraphicsContext2D().setStroke(selectionColorPicker.getValue());
							selection.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
							selection.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
							selection.setLayoutX(200 - iconSize2);
							selection.setLayoutY(216 - iconSize2);
							canvasGroup.getChildren().add(selection);
						}
					});
				} else {
					canvasGroup.getChildren().remove(highlight);
				}

				}
		});
	}

	@FXML
	private void onCancelButtonPressed() {
		Stage stage = (Stage) traderColorPicker.getScene().getWindow();
		stage.close();
	}

	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}

	public void setSaveCancelService(SaveCancelService saveCancelService) {
		this.saveCancelService = saveCancelService;
	}

	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}

}
