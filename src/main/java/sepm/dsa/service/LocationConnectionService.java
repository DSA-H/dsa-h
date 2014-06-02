package sepm.dsa.service;

import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.service.path.NoPathException;

import java.util.List;

public interface LocationConnectionService {

    /**
     * Persists a {@code LocationConnection} in the Database
     *
     * @param locationConnection to be persisted must not be null
     * @return The created locationConnection model.
     */
    public LocationConnection add(LocationConnection locationConnection);

    /**
     * Updates a already existing {@code LocationConnection} in the database
     *
     * @param locationConnection to update must not be null
     * @return The updated locationConnection model.
     */
    public LocationConnection update(LocationConnection locationConnection);

    /**
     * Delete a locationConnection permanently
     *
     * @param locationConnection to be deleted must not be null
     */
    public void remove(LocationConnection locationConnection);

    /**
     * Gets the {@code LocationConnection} between two locations
     *
     * @param location1
     * @param location2
     * @return the connection, or null, if no such connections exists
     */
    LocationConnection get(Location location1, Location location2);

//    /**
//     * Gets all {@code LocationConnection}s
//     * @return
//     */
//    LocationConnection getAll();

    /**
     * Gets an ordered list being the shortest path of {@code LocationConnection}s between two locations
     *
     * @param location1
     * @param location2
     * @return a list of connections, or null, if no such path exists
     * @throws sepm.dsa.exceptions.DSALocationNotExistingException
     */
    List<LocationConnection> getShortestPathBetween(Location location1, Location location2) throws NoPathException;

    /**
     * @param location the location
     * @param filter   the filter string (location name), might be null
     * @return
     */
    List<LocationConnection> getAllByLocationFilter1(Location location, String filter);

    /**
     * Suggests a list of location connections to the location from a given location.
     * Already existing location connections are not suggested. The distance
     *
     * @param location       the location
     * @param withinDistance the distance in [??] around the location, must be > 0
     * @return
     */
    List<LocationConnection> suggestLocationConnectionsAround(Location location, double withinDistance);

    /**
     * Suggests a list of location connections to the location from a given location.
     * It is filtered by a filter string.
     * Already existing location connections are not suggested.
     *
     * @param location the location
     * @param filter   the filter string (location name), might be null
     * @return
     */
    List<LocationConnection> suggestLocationConnectionsByFilter(Location location, String filter);

    /**
     * The suggested distance between two locations. Used in suggestConnectionsAround(Location, Integer)
     *
     * @param location1
     * @param location2
     * @return the suggested distance (>= 0)
     * @throws java.lang.IllegalArgumentException if one of the locations doesn't have x- or y-coordinate specified
     * @see LocationConnectionService#suggestLocationConnectionsAround(sepm.dsa.model.Location, double) suggestLocationConnectionsAround
     */
    double suggestedDistanceBetween(Location location1, Location location2);

    /**
     * Calculates a travel
     *
     * @param distance
     * @return
     */
    int suggestedTravelTimeForDistance(double distance);


    /**
     *
     * @param locationId
     * @return all connections of a location
     */
    public List<LocationConnection> getAllByLocation(int locationId);

}
