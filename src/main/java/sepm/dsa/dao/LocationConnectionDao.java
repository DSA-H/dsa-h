package sepm.dsa.dao;

import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import java.util.List;

public interface LocationConnectionDao {

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
     * Finds all LocationConnections
     *
     * @return the locationConnections or empty list of no locationConnections exist (not null)
     */
    public List<LocationConnection> getAll();

    /**
     * @param location
     * @param locationName
     * @return
     */
    public List<LocationConnection> getAllByLocationName(Location location, String locationName);

}
