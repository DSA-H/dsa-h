package sepm.dsa.service;

import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import java.util.List;

public interface LocationConnectionService {

    /**
     * Persists a {@code LocationConnection} in the Database
     *
     * @param locationConnection to be persisted must not be null
     * @return
     */
    public void add(LocationConnection locationConnection);

    /**
     * Updates a already existing {@code LocationConnection} in the database
     *
     * @param locationConnection to update must not be null
     */
    public void update(LocationConnection locationConnection);

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

    /**
     * Gets an ordered list being the shortest path of {@code LocationConnection}s between two locations
     *
     * @param location1
     * @param location2
     * @return a list of connections, or null, if no such path exists
     * @throws sepm.dsa.exceptions.DSALocationNotExistingException
     */
    List<LocationConnection> getShortestPathBetween(Location location1, Location location2);

}
