package sepm.dsa.service;

import sepm.dsa.model.Location;

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

    /**
     * Gets a location by it's id
     *
     * @param id
     * @return the location, or null if it does not exist
     */
    Location get(int id);

    /**
     * Asks the DAO to find all Locations
     *
     * @return the locations or empty list of no locations exist (not null)
     */
    public List<Location> getAll();

    /**
     * Get all locations in a region
     *
     * @param regionId the {@code Region}'s ID
     * @return a list of locations in the region, or an emtpy list if no location is in the region (not null)
     */
    public List<Location> getAllByRegion(int regionId);

}