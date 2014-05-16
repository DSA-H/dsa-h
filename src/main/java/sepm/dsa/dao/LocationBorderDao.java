package sepm.dsa.dao;

import sepm.dsa.model.Location;

import java.util.List;

interface LocationBorderDao {

    /**
     * Persists a Locatoin in the Database
     * @param location to be persisted
     * @return
     */
    void add(Location location);

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

    Location get(int id);

    /**
     * Finds all Locations
     * @return the locations or empty list of no locations exist (not null)
     */
    List<Location> getAll();

    List<Location> getAllForLocation(int locationId);

    List<Location> getAllMerchantsForLocation(int locationId);
}