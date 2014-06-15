package sepm.dsa.service;

import javafx.scene.paint.Color;
import sepm.dsa.model.Location;

import java.io.File;

public interface MapService {

	/**
	 * opens a FileChooser and returns the chosen Image
	 */
	public File chooseMap();

	/**
	 * Copies the given File to the active-path
	 * If there is a worldMap set it will be placed in the alternative-path
	 * @param newmap must be a valid image
	 */
	public void setWorldMap(File newmap);

	/**
	 * Copies the given File to the active-path
	 * If there is a Location Map set it will be placed in the alternative-path
	 * @param newmap must be a valid image
	 */
	public void setLocationMap(Location location, File newmap);

	/**
	 * exports the map with the given name
	 * @param map the name of the map to be exported
	 */
	public void exportMap(String map);

	/**
	 * @return the directory where the active maps are stored
	 */
	public File getActiveDir();

	/**
	 * @return the directory where previous maps are stored
	 */
	public File getAlternativeDir();

	/**
	 * @return the current world map or null if none exists
	 */
	public File getWorldMap();

	/**
	 * @param location the location the map is wanted from
	 * @return the location's map or null if none exists
	 */
	public File getLocationMap(Location location);

	/**
	 * @return an Error Image or null if none exists
	 */
	public File getNoMapImage();

	/**
	 * @return Color of Traders in Location View
	 */
	public Color getTraderColor();

	/**
	 * @return Color of MovingTraders in Location View
	 */
	public Color getMovingTraderColor();

	/**
	 * @return Color of Taverns in Location View
	 */
	public Color getTavernColor();

	/**
	 * @return Color of Highlights
	 */
	public Color getHighlightColor();

	/**
	 * @return Color of Selection
	 */
	public Color getSelectionColor();

	/**
	 * @return Color of LocationBorders
	 */
	public Color getBorderColor();

	/**
	 * @return Color of Names
	 */
	public Color getNameColor();

	/**
	 * @return Size of Location-Icons
	 */
	public int getWorldIconSize();

	/**
	 * @return Size of Trader/Tavern-Icons
	 */
	public double getLocationIconSize(Location location);

	/**
	 * @return the Size of Names on the Map relative to icons
	 */
	public double getTextSize();

	public void setTraderColor(Color c);
	public void setMovingTraderColor(Color c);
	public void setTavernColor(Color c);
	public void setHighlightColor(Color c);
	public void setSelectionColor(Color c);
	public void setBorderColor(Color c);
	public void setNameColor(Color c);
	public void setWorldIconSize(int size);
	public void setLocationIconSize(Location location, double size);
	public void setTextSize(double size);
}