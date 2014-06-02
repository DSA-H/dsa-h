package sepm.dsa.dao;

import sepm.dsa.model.Location;

import java.util.List;

public interface LocationDao extends BaseDao<Location> {

    /**
     * Get all locations in a region
     *
     * @param regionId the {@code Region}'s ID
     * @return a list of locations in the region, or an emtpy list if no location is in the region (not null)
     */
    public List<Location> getAllByRegion(int regionId);


    /**
     * Gets a list of locations near a given location.
     *
     * @param location       the location
     * @param withinDistance the distance in [??] around the location
     * @return
     */
    List<Location> getAllAround(Location location, double withinDistance);

    /**
     * Gets a list of locations near a given location having no connection yet.
     *
     * @param location       the location
     * @param withinDistance the distance in [??] around the location
     * @return
     */
    List<Location> getAllAroundNotConnected(Location location, double withinDistance);

    /**
     * @param location
     * @param locationName
     * @return all locations that are not connected to 'location', with name 'locationName'
     *          might be an empty list (not null)
     */
    List<Location> getAllByNameNotConnectedTo(Location location, String locationName);

    /**
     * @param location
     * @param locationName
     * @return all locations with the specified locationName, without a specific location
     */
    List<Location> getAllByNameWithoutLocation(Location location, String locationName);
}
