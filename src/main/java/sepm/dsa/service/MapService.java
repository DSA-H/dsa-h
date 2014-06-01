package sepm.dsa.service;

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
}
