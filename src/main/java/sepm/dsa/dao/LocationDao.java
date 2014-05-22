package sepm.dsa.dao;

import sepm.dsa.model.Location;

import java.util.List;

public interface LocationDao {

    /**
     * Persists a {@code Location} in the Database
     *
     * @param location to be persisted must not be null
     * @return
     */
    public void add(Location location);

    /**
     * Updates a already existing {@code Location} in the database
     *
     * @param location to update must not be null
     */
    public void update(Location location);

    /**
     * Delete a location permanently
     *
     * @param location to be deleted must not be null
     */
    public void remove(Location location);

    Location get(int id);

    /**
     * Finds all Locations
     *
     * @return the locations or empty list of no locations exist (not null)
     */
    public List<Location> getAll();

    /**
     * Get all locations in a region
     * @param regionId the {@code Region}'s ID
     * @return a list of locations in the region, or an emtpy list if no location is in the region (not null)
     */
    public List<Location> getAllByRegion(int regionId);

}
