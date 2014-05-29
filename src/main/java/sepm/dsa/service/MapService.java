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

	public File getActiveDir();

	public File getAlternativeDir();

	public File getWorldMap();

	public File getLocationMap(Location location);


}
