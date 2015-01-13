package sepm.dsa.service;

import javafx.scene.paint.Color;
import sepm.dsa.model.Location;
import sepm.dsa.model.TownSize;

import java.io.File;

public interface MapService {

    /**
     * opens a FileChooser and returns the chosen Image
     */
    public File chooseMap();

    /**
     * Copies the given File to the active-path
     * If there is a worldMap set it will be placed in the alternative-path
     *
     * @param newmap must be a valid image
     */
    public void setWorldMap(File newmap);

    /**
     * Copies the given File to the active-path
     * If there is a Location Map set it will be placed in the alternative-path
     *
     * @param newmap must be a valid image
     */
    public void setLocationMap(Location location, File newmap);

    /**
     * exports the map with the given name
     *
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
     * @param color the color to convert to String
     * @return a String representation of the @param color (e.g. "FF00FF")
     */
    public String colorToString(Color color);

    /**
     * @param colorString a String representation of the wanted color (e.g. "FF00FF")
     * @return the Color object represented by @param colorString
     */
    public Color stringToColor(String colorString);

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

    /**
     * Map Options - set color of trader
     *
     * @param c color of trader
     */
    public void setTraderColor(Color c);

    /**
     * Map Options - set color of movingTrader
     *
     * @param c color of movingTrader
     */
    public void setMovingTraderColor(Color c);

    /**
     * Map Options - set color of tavern
     *
     * @param c color of tavern
     */
    public void setTavernColor(Color c);

    /**
     * Map Options - set highlight color
     *
     * @param c highlight color
     */
    public void setHighlightColor(Color c);

    /**
     * Map Options - set selection color
     *
     * @param c selection color
     */
    public void setSelectionColor(Color c);

    /**
     * Map Options - set color of tavern
     *
     * @param c color of tavern
     */
    public void setBorderColor(Color c);

    /**
     * Map Options - set color of names / legends
     *
     * @param c color of names e.g. location names
     */
    public void setNameColor(Color c);

    /**
     * Map options set the size of icons on world-map
     *
     * @param size of icons
     */
    public void setWorldIconSize(int size);

    /**
     * Map options set the size of icons on world-map
     *
     * @param location
     * @param size     size of location icons
     */
    public void setLocationIconSize(Location location, double size);

    /**
     * Map options set the size of text / names /legends
     *
     * @param size of text/ names e.g. location names
     */
    public void setTextSize(double size);

    public void setShowBiggerThan(TownSize townSize);

    public TownSize getShowBiggerThan();

    public void setShowConnections(boolean show);

    public boolean getShowConnections();
}