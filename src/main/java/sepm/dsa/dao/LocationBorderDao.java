package sepm.dsa.dao;

import java.util.List;

public class LocationBorderDao {

    /**
     * Persists a Locatoin in the Database
     * @param location to be persisted
     * @return
     */
    LocationPk add(Location location);

    /**
     * Updates a already existing {@code Location} in the database
     * @param location
     */
    void update(Location location);

    /**
     * Delete a location permanently
     * @param location to be deleted
     */
    void remove(Location location);


    LocationBorder get(LocationPk pk);

    /**
     * Finds all Locations
     * @return the locations or empty list of no locations exist (not null)
     */
    List<Location> getAll();

    List<Location> getAllForLocation(int locationId);

    List<Location> getAllMerchantsForLocation(int locationId);
}
