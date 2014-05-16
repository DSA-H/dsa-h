package sepm.dsa.dao;

import sepm.dsa.model.Location;

import java.util.List;

interface LocationDao {

    /**
     * Persists a {@code Location} in the Database
     *
     * @param location to be persisted must not be null
     * @return
     */
    void add(Location location);

    /**
     * Updates a already existing {@code Location} in the database
     *
     * @param location to update must not be null
     */
    void update(Location location);

    /**
     * Delete a location permanently
     *
     * @param location to be deleted must not be null
     */
    void remove(Location location);

    Location get(int id);

    /**
     * Finds all Locations
     *
     * @return the locations or empty list of no locations exist (not null)
     */
    List<Location> getAll();
}
