package sepm.dsa.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import sepm.dsa.service.*;

public class MapOptionsController extends BaseControllerImpl {
	private static final Logger log = LoggerFactory.getLogger(MapOptionsController.class);
	private MapService mapService;
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
	private AnchorPane previewPane;
	private Group canvasGroup = new Group();
	private Canvas demoCanvas;
	private Canvas highlight;
	private Canvas selection = new Canvas(0,0);

	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		locationSizeSlider.setMin(5);
		locationSizeSlider.setMax(50);
		textSizeSlider.setMin(0.1);
		textSizeSlider.setMax(2);

		previewPane.getChildren().addAll(canvasGroup);

		traderColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				drawExample();
			}
		});

		movingTraderColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				drawExample();
			}
		});

		tavernColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				drawExample();
			}
		});

		borderColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				drawExample();
			}
		});

		textColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				drawExample();
			}
		});

		highlightColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				drawExample();
			}
		});

		selectionColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
				drawExample();
			}
		});

		locationSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				drawExample();
			}
		});

		textSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				drawExample();
			}
		});
	}

	@Override
	public void reload() {
		traderColorPicker.setValue(mapService.getTraderColor());
		movingTraderColorPicker.setValue(mapService.getMovingTraderColor());
		tavernColorPicker.setValue(mapService.getTavernColor());
		borderColorPicker.setValue(mapService.getBorderColor());
		textColorPicker.setValue(mapService.getNameColor());
		selectionColorPicker.setValue(mapService.getSelectionColor());
		highlightColorPicker.setValue(mapService.getHighlightColor());
		locationSizeSlider.adjustValue(mapService.getWorldIconSize());
		textSizeSlider.adjustValue(mapService.getTextSize());
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
	}

	private void drawExample() {
		canvasGroup.getChildren().remove(demoCanvas);
		canvasGroup.getChildren().remove(highlight);
		if (canvasGroup.getChildren().size() > 0) {
			selection = (Canvas) canvasGroup.getChildren().remove(0);
		}
		demoCanvas = new Canvas(340, 200);
		GraphicsContext gc = demoCanvas.getGraphicsContext2D();
		gc.setFill(Color.GREY);
		double iconSize = locationSizeSlider.getValue();
		gc.fillRoundRect(100 - (iconSize * 0.5), 100 - (iconSize * 0.5), iconSize, iconSize, (iconSize * 0.5), (iconSize * 0.5));
		gc.setStroke(borderColorPicker.getValue());
		gc.setLineWidth((iconSize * 0.05));
		gc.strokeRoundRect(100 - (iconSize * 0.5), 100 - (iconSize * 0.5), iconSize, iconSize, (iconSize * 0.5), (iconSize * 0.5));
		gc.setFill(textColorPicker.getValue());
		gc.setFont(new Font(iconSize * textSizeSlider.getValue()));
		gc.fillText("Ortsname", 100 + (iconSize*0.5), 100 - (iconSize*0.5));


		iconSize = 10;
		gc.setFont(new Font(iconSize * textSizeSlider.getValue()));
		gc.setLineWidth(iconSize * 0.5);
		gc.setStroke(traderColorPicker.getValue());
		gc.strokeLine(240 - (iconSize * 0.5), 33 - (iconSize * 0.5), 240 + (iconSize * 0.5), 33 + (iconSize * 0.5));
		gc.strokeLine(240 - (iconSize * 0.5), 33 + (iconSize * 0.5), 240 + (iconSize * 0.5), 33 - (iconSize * 0.5));
		gc.fillText("Händler", 240 + iconSize, 33 - iconSize);

		gc.setStroke(movingTraderColorPicker.getValue());
		gc.strokeLine(240 - (iconSize * 0.5), 100 - (iconSize * 0.5), 240 + (iconSize * 0.5), 100 + (iconSize * 0.5));
		gc.strokeLine(240 - (iconSize * 0.5), 100 + (iconSize * 0.5), 240 + (iconSize * 0.5), 100 - (iconSize * 0.5));
		gc.fillText("Fahrend", 240 + iconSize, 100 - iconSize);

		gc.setStroke(tavernColorPicker.getValue());
		gc.strokeLine(240 - (iconSize * 0.5), 166 - (iconSize * 0.5), 240 + (iconSize * 0.5), 166 + (iconSize * 0.5));
		gc.strokeLine(240 - (iconSize * 0.5), 166 + (iconSize * 0.5), 240 + (iconSize * 0.5), 166 - (iconSize * 0.5));
		gc.fillText("Wirtshaus", 240 + iconSize, 166 - iconSize);

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
				double iconSize2 = 10;
				if (xPos > 100 - (iconSize/2) && xPos < 100 + (iconSize/2) &&
						yPos > 100 - (iconSize/2) && yPos < 100 + (iconSize/2)) {
					canvasGroup.getChildren().remove(highlight);
					highlight = new Canvas((iconSize*1.5), (iconSize*1.5));
					highlight.getGraphicsContext2D().setLineWidth((iconSize / 5));
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeRoundRect((iconSize / 5), (iconSize / 5), (iconSize * 1.1), (iconSize * 1.1), (iconSize * 0.65), (iconSize * 0.65));
					highlight.setLayoutX(100 - (iconSize * 0.75));
					highlight.setLayoutY(100 - (iconSize * 0.75));
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
							selection.setLayoutY(100 - (iconSize * 0.75));
							canvasGroup.getChildren().add(selection);
						}
					});
				} else if (e.getX() > 240 - iconSize2 && e.getX() < 240 + iconSize2 &&
						e.getY() > 33 - iconSize2 && e.getY() < 33 + iconSize2) {
					canvasGroup.getChildren().remove(highlight);
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(240 - iconSize2);
					highlight.setLayoutY(33 - iconSize2);
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
							selection.setLayoutX(240 - iconSize2);
							selection.setLayoutY(33 - iconSize2);
							canvasGroup.getChildren().add(selection);
						}
					});
				} else if (e.getX() > 240 - iconSize2 && e.getX() < 240 + iconSize2 &&
						e.getY() > 100 - iconSize2 && e.getY() < 100 + iconSize2) {
					canvasGroup.getChildren().remove(highlight);
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(240 - iconSize2);
					highlight.setLayoutY(100 - iconSize2);
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
							selection.setLayoutX(240 - iconSize2);
							selection.setLayoutY(100 - iconSize2);
							canvasGroup.getChildren().add(selection);
						}
					});
				} else if (e.getX() > 240 - iconSize2 && e.getX() < 240 + iconSize2 &&
						e.getY() > 166 - iconSize2 && e.getY() < 166 + iconSize2) {
					canvasGroup.getChildren().remove(highlight);
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(240 - iconSize2);
					highlight.setLayoutY(166 - iconSize2);
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
							selection.setLayoutX(240 - iconSize2);
							selection.setLayoutY(166 - iconSize2);
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

}
