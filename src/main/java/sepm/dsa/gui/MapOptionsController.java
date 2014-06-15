package sepm.dsa.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.controlsfx.dialog.Dialogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sepm.dsa.application.SpringFxmlLoader;
import sepm.dsa.model.Location;
import sepm.dsa.model.Tavern;
import sepm.dsa.model.Trader;
import sepm.dsa.service.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
	private Canvas demoCanvas;
	private Canvas highlight;
	private Canvas selection;

	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
		locationSizeSlider.setMin(5);
		locationSizeSlider.setMax(50);
		textSizeSlider.setMin(0.1);
		textSizeSlider.setMax(2);

		reload();

		traderColorPicker.valueProperty().addListener(new ChangeListener<Color>() {
			@Override
			public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
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
		GraphicsContext gc = demoCanvas.getGraphicsContext2D();
		gc.setFill(Color.GREY);
		double iconSize = locationSizeSlider.getValue();
		gc.fillRoundRect(100 - (iconSize*0.5), 100 - (iconSize*0.5), iconSize, iconSize, (iconSize*0.5), (iconSize*0.5));
		gc.setStroke(borderColorPicker.getValue());
		gc.setLineWidth((iconSize*0.05));
		gc.strokeRoundRect(100 - (iconSize*0.5), 100 - (iconSize*0.5), iconSize, iconSize, (iconSize*0.5), (iconSize*0.5));

		iconSize = 20;
		gc.setLineWidth(iconSize*0.5);
		gc.setStroke(traderColorPicker.getValue());
		gc.strokeLine(240 - (iconSize*0.5), 33 - (iconSize*0.5), 240 + (iconSize*0.5), 33 + (iconSize*0.5));
		gc.strokeLine(240 - (iconSize*0.5), 33 + (iconSize*0.5), 240 + (iconSize*0.5), 33 - (iconSize*0.5));

		gc.setStroke(movingTraderColorPicker.getValue());
		gc.strokeLine(240 - (iconSize*0.5), 100 - (iconSize*0.5), 240 + (iconSize*0.5), 100 + (iconSize*0.5));
		gc.strokeLine(240 - (iconSize*0.5), 100 + (iconSize*0.5), 240 + (iconSize*0.5), 100 - (iconSize*0.5));

		gc.setStroke(tavernColorPicker.getValue());
		gc.strokeLine(240 - (iconSize*0.5), 166 - (iconSize*0.5), 240 + (iconSize*0.5), 166 + (iconSize*0.5));
		gc.strokeLine(240 - (iconSize*0.5), 166 + (iconSize*0.5), 240 + (iconSize*0.5), 166 - (iconSize*0.5));

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
				System.out.println("("+xPos+"/"+yPos+")");
				double iconSize = locationSizeSlider.getValue();
				if (xPos > 100 - (iconSize/2) && xPos < 100 + (iconSize/2) &&
						yPos > 100 - (iconSize/2) && yPos < 100 + (iconSize/2)) {
					highlight = new Canvas((iconSize*1.5), (iconSize*1.5));
					highlight.getGraphicsContext2D().setLineWidth((iconSize / 5));
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeRoundRect((iconSize / 5), (iconSize / 5), (iconSize * 1.1), (iconSize * 1.1), (iconSize * 0.65), (iconSize * 0.65));
					highlight.setLayoutX(nodeCoord.getX() + 100 - (iconSize*0.75));
					highlight.setLayoutY(nodeCoord.getY() + 100 - (iconSize*0.75));
					highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							selection = new Canvas((iconSize*1.5), (iconSize*1.5));
							selection.getGraphicsContext2D().setLineWidth((iconSize*0.2));
							selection.getGraphicsContext2D().setStroke(selectionColorPicker.getValue());
							selection.getGraphicsContext2D().strokeRoundRect((iconSize*0.2), (iconSize*0.2), (iconSize*1.1), (iconSize*1.1), (iconSize*0.65), (iconSize*0.65));
							selection.setLayoutX(nodeX + 100 - (iconSize*0.75));
							selection.setLayoutY(nodeY + 100 - (iconSize*0.75));
						}
					});
				}
				double iconSize2 = 20;
				if (e.getX() > 240 - iconSize2 && e.getX() < 240 + iconSize2 &&
						e.getY() > 33 - iconSize2 && e.getY() < 3 + iconSize2) {
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(nodeX + 240 - iconSize2);
					highlight.setLayoutY(nodeY + 33 - iconSize2);

					highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							selection = new Canvas(iconSize*3, iconSize*3);
							selection.getGraphicsContext2D().setLineWidth(iconSize*0.6);
							selection.getGraphicsContext2D().setStroke(traderColorPicker.getValue());
							selection.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*0.4), (iconSize*1.6), (iconSize*1.6));
							selection.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*1.6), (iconSize*1.6), (iconSize*0.4));
							selection.setLayoutX(nodeX + 240 - iconSize);
							selection.setLayoutY(nodeY + 33 - iconSize);
						}
					});
				}
				if (e.getX() > 240 - iconSize2 && e.getX() < 240 + iconSize2 &&
						e.getY() > 100 - iconSize2 && e.getY() < 100 + iconSize2) {
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(nodeX + 240 - iconSize2);
					highlight.setLayoutY(nodeY + 100 - iconSize2);

					highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							selection = new Canvas(iconSize*3, iconSize*3);
							selection.getGraphicsContext2D().setLineWidth(iconSize*0.6);
							selection.getGraphicsContext2D().setStroke(traderColorPicker.getValue());
							selection.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*0.4), (iconSize*1.6), (iconSize*1.6));
							selection.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*1.6), (iconSize*1.6), (iconSize*0.4));
							selection.setLayoutX(nodeX + 240 - iconSize);
							selection.setLayoutY(nodeY + 100 - iconSize);
						}
					});
				}
				if (e.getX() > 240 - iconSize2 && e.getX() < 240 + iconSize2 &&
						e.getY() > 166 - iconSize2 && e.getY() < 166 + iconSize2) {
					highlight = new Canvas(iconSize2 * 2, iconSize2 * 2);
					highlight.getGraphicsContext2D().setLineWidth(iconSize2 * 0.6);
					highlight.getGraphicsContext2D().setStroke(highlightColorPicker.getValue());
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6));
					highlight.getGraphicsContext2D().strokeLine((iconSize2 * 0.4), (iconSize2 * 1.6), (iconSize2 * 1.6), (iconSize2 * 0.4));
					highlight.setLayoutX(nodeX + 240 - iconSize2);
					highlight.setLayoutY(nodeY + 166 - iconSize2);

					highlight.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							selection = new Canvas(iconSize*3, iconSize*3);
							selection.getGraphicsContext2D().setLineWidth(iconSize*0.6);
							selection.getGraphicsContext2D().setStroke(traderColorPicker.getValue());
							selection.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*0.4), (iconSize*1.6), (iconSize*1.6));
							selection.getGraphicsContext2D().strokeLine((iconSize*0.4), (iconSize*1.6), (iconSize*1.6), (iconSize*0.4));
							selection.setLayoutX(nodeX + 240 - iconSize);
							selection.setLayoutY(nodeY + 166 - iconSize);
						}
					});
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
