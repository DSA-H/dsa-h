package sepm.dsa.service;

import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import java.util.List;

public interface LocationService {

    /**
     * Asks the DAO to persist a {@code Location} in the Database
     *
     * @param location to be persisted must not be null
     * @return
     */
   public void add(Location location);

    /**
     * Asks the DAO to update a already existing {@code Location} in the database
     *
     * @param location to update must not be null
     */
    public void update(Location location);

    /**
     * Asks the DAO to delete a location permanently
     *
     * @param location to be deleted must not be null
     */
    public void remove(Location location);

    Location get(int id);

    /**
     * Asks the DAO to find all Locations
     *
     * @return the locations or empty list of no locations exist (not null)
     */
    public  List<Location> getAll();


    /**
     * Suggests a list of location connections to the location from a given location.
     * Already existing location connections are not suggested. The distance
     * @param location the location
     * @param withinDistance the distance in [??] around the location, must be > 0
     * @return
     */
    List<LocationConnection> suggestLocationConnectionsAround(Location location, double withinDistance);

    /**
     * The suggested distance between two locations. Used in suggestConnectionsAround(Location, Integer)
     * @param location1
     * @param location2
     * @return the suggested distance (>= 0)
     * @throws java.lang.IllegalArgumentException if one of the locations doesn't have x- or y-coordinate specified
     * @see LocationService#suggestLocationConnectionsAround(sepm.dsa.model.Location, double) suggestLocationConnectionsAround
     */
    double suggestedDistanceBetween(Location location1, Location location2);

    /**
     * Calculates a travel
     * @param distance
     * @return
     */
    int suggestedTravelTimeForDistance(double distance);

}
